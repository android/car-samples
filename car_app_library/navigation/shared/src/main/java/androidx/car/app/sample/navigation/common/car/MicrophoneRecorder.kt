/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.car.app.sample.navigation.common.car

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresPermission
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.media.CarAudioRecord
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/** Manages recording the microphone and accessing the stored data from the microphone.  */
class MicrophoneRecorder(private val mCarContext: CarContext) {
    /**
     * Starts recording the car microphone, then plays it back.
     */
    fun record() {
        if (mCarContext.checkSelfPermission(permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            CarToast.makeText(
                mCarContext, "Grant mic permission on phone",
                CarToast.LENGTH_LONG
            ).show()
            val permissions = listOf(permission.RECORD_AUDIO)
            mCarContext.requestPermissions(permissions) { grantedPermissions: List<String?>, rejectedPermissions: List<String?>? ->
                if (grantedPermissions.contains(permission.RECORD_AUDIO)) {
                    record()
                }
            }
            return
        }
        val record = CarAudioRecord.create(mCarContext)

        val recordingThread =
            Thread(
                { doRecord(record) },
                "AudioRecorder Thread"
            )
        recordingThread.start()
    }

    @SuppressLint("ClassVerificationFailure") // runtime check for < API 26
    @RequiresPermission(permission.RECORD_AUDIO)
    private fun play(audioFocusRequest: AudioFocusRequest) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.O) {
            return
        }

        val inputStream: InputStream
        try {
            inputStream = mCarContext.openFileInput(FILE_NAME)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_DEFAULT)
                    .setSampleRate(CarAudioRecord.AUDIO_CONTENT_SAMPLING_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE)
            .build()
        audioTrack.play()
        try {
            while (inputStream.available() > 0) {
                val audioData = ByteArray(CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE)
                val size = inputStream.read(audioData, 0, audioData.size)

                if (size < 0) {
                    // End of file
                    return
                }
                audioTrack.write(audioData, 0, size)
            }
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        audioTrack.stop()
        // Abandon the FocusRequest so that user's media can be resumed
        mCarContext.getSystemService(AudioManager::class.java).abandonAudioFocusRequest(
            audioFocusRequest
        )
    }

    @SuppressLint("ClassVerificationFailure") // runtime check for < API 26
    @RequiresPermission(permission.RECORD_AUDIO)
    private fun doRecord(record: CarAudioRecord) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.O) {
            return
        }

        // Take audio focus so that user's media is not recorded
        val audioAttributes =
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .build()

        val audioFocusRequest =
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener { state: Int ->
                    if (state == AudioManager.AUDIOFOCUS_LOSS) {
                        // Stop recording if audio focus is lost
                        record.stopRecording()
                    }
                }
                .build()

        if (mCarContext.getSystemService(AudioManager::class.java)
                .requestAudioFocus(audioFocusRequest)
            != AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        ) {
            return
        }

        record.startRecording()

        val bytes: MutableList<Byte> = ArrayList()
        var isRecording = true
        while (isRecording) {
            // gets the voice output from microphone to byte format
            val bData = ByteArray(CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE)
            val len = record.read(bData, 0, CarAudioRecord.AUDIO_CONTENT_BUFFER_SIZE)

            if (len > 0) {
                for (i in 0 until len) {
                    bytes.add(bData[i])
                }
            } else {
                isRecording = false
            }
        }

        try {
            val outputStream: OutputStream =
                mCarContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
            addHeader(outputStream, bytes.size)
            for (b in bytes) {
                outputStream.write(b.toInt())
            }

            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
        record.stopRecording()
        play(audioFocusRequest)
    }

    @Throws(IOException::class)
    private fun addHeader(outputStream: OutputStream, totalAudioLen: Int) {
        val totalDataLen = totalAudioLen + 36
        val header = ByteArray(44)
        val dataElementSize = 8
        val longSampleRate = CarAudioRecord.AUDIO_CONTENT_SAMPLING_RATE.toLong()

        // See http://soundfile.sapp.org/doc/WaveFormat/
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalAudioLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1 PCM
        header[21] = 0
        header[22] = 1 // Num channels (mono)
        header[23] = 0
        header[24] = (longSampleRate and 0xffL).toByte() // sample rate
        header[25] = ((longSampleRate shr 8) and 0xffL).toByte()
        header[26] = ((longSampleRate shr 16) and 0xffL).toByte()
        header[27] = ((longSampleRate shr 24) and 0xffL).toByte()
        header[28] = (longSampleRate and 0xffL).toByte() // byte rate
        header[29] = ((longSampleRate shr 8) and 0xffL).toByte()
        header[30] = ((longSampleRate shr 16) and 0xffL).toByte()
        header[31] = ((longSampleRate shr 24) and 0xffL).toByte()
        header[32] = 1 // block align
        header[33] = 0
        header[34] = (dataElementSize and 0xff).toByte() // bits per sample
        header[35] = ((dataElementSize shr 8) and 0xff).toByte()
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        outputStream.write(header, 0, 44)
    }

    companion object {
        private const val FILE_NAME = "recording.wav"
    }
}
