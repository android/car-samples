/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.car.app.sample.showcase.common.textandicons

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom

/** A simple file provider that returns files after a random delay.  */
class DelayedFileProvider : FileProvider() {
    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor {
        try {
            // Wait for a random period between the minimum and maximum delay.
            Thread.sleep(ThreadLocalRandom.current().nextLong(MIN_DELAY_MILLIS, MAX_DELAY_MILLIS))
        } catch (e: InterruptedException) {
            throw FileNotFoundException(e.message)
        }

        return super.openFile(uri, mode)!!
    }

    companion object {
        private const val FILE_PROVIDER_AUTHORITY = "com.showcase.fileprovider"
        private const val RESOURCE_DIR = "res"
        private const val MIN_DELAY_MILLIS: Long = 1000
        private const val MAX_DELAY_MILLIS: Long = 3000

        /** Creates a file from the given resource id and returns the URI for it.  */
        fun getUriForResource(
            context: Context,
            hostPackageName: String, resId: Int
        ): Uri {
            val resourceFile =
                File(context.filesDir.absolutePath, RESOURCE_DIR + "/" + resId)
            if (!resourceFile.exists()) {
                resourceFile.parentFile?.mkdir()

                val bm = BitmapFactory.decodeResource(context.resources, resId)
                try {
                    FileOutputStream(resourceFile).use { fos ->
                        bm.compress(CompressFormat.PNG, 10, fos)
                    }
                } catch (ex: IOException) {
                    throw IllegalArgumentException("Invalid resource $resId")
                }
            }
            val uri = getUriForFile(context, FILE_PROVIDER_AUTHORITY, resourceFile)

            // FileProvider requires the app to grant temporary access to the car hosts for the file.
            // A URI from a content provider may not need to do this if its contents are public.
            context.grantUriPermission(
                hostPackageName, uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            return uri
        }
    }
}
