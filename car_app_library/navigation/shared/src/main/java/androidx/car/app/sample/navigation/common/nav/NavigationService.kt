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
package androidx.car.app.sample.navigation.common.nav

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RawRes
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.navigation.NavigationManager
import androidx.car.app.navigation.NavigationManagerCallback
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.navigation.model.Trip
import androidx.car.app.notification.CarAppExtender
import androidx.car.app.notification.CarPendingIntent
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.sample.navigation.common.app.MainActivity
import androidx.car.app.sample.navigation.common.car.NavigationCarAppService
import androidx.car.app.sample.navigation.common.model.Instruction
import androidx.car.app.sample.navigation.common.model.Script
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.IOException

/** Foreground service to provide navigation directions.  */
class NavigationService : Service() {
    private var mNotificationManager: NotificationManager? = null
    private val mBinder: IBinder = LocalBinder()

    private var mCarContext: CarContext? = null

    private var mListener: Listener? = null

    private var mNavigationManager: NavigationManager? = null
    var isNavigating: Boolean = false
        private set
    private var mStepsSent = 0

    private val mDestinations: MutableList<Destination?> = ArrayList()
    private val mSteps: MutableList<Step?> = ArrayList()

    private var mScript: Script? = null

    /** A listener for the navigation state changes.  */
    interface Listener {
        /** Callback called when the navigation state changes.  */
        fun navigationStateChanged(
            isNavigating: Boolean,
            isRerouting: Boolean,
            hasArrived: Boolean,
            destinations: List<Destination?>?,
            steps: List<Step?>?,
            nextDestinationTravelEstimate: TravelEstimate?,
            nextStepRemainingDistance: Distance?,
            shouldShowNextStep: Boolean,
            shouldShowLanes: Boolean,
            junctionImage: CarIcon?
        )
    }

    /**
     * Class used for the client Binder. Since this service runs in the same process as its clients,
     * we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        val service: NavigationService
            get() = this@NavigationService
    }

    override fun onCreate() {
        Log.i(TAG, "In onCreate()")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "In onStartCommand()")
        if (CANCEL_ACTION == intent.action) {
            stopNavigation()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return true // Ensures onRebind() is called when a client re-binds.
    }

    /** Sets the [CarContext] to use while the service is connected.  */
    fun setCarContext(carContext: CarContext, listener: Listener) {
        mCarContext = carContext
        mNavigationManager = mCarContext!!.getCarService(NavigationManager::class.java)
        mNavigationManager!!.setNavigationManagerCallback(
            object : NavigationManagerCallback {
                override fun onStopNavigation() {
                    this@NavigationService.stopNavigation()
                }

                override fun onAutoDriveEnabled() {
                    Log.d(TAG, "onAutoDriveEnabled called")
                    CarToast.makeText(carContext, "Auto drive enabled", CarToast.LENGTH_LONG)
                        .show()
                }
            })
        mListener = listener

        // Uncomment if navigating
        // mNavigationManager.navigationStarted();
    }

    /** Clears the currently used [CarContext].  */
    fun clearCarContext() {
        mCarContext = null
        mNavigationManager = null
    }

    /** Executes the given list of navigation instructions.  */
    fun executeInstructions(instructions: List<Instruction>) {
        mScript =
            Script.execute(
                instructions,
                object  : Script.Processor {
                    override fun process(instruction: Instruction, nextInstruction: Instruction?) {
                        when (instruction.type) {
                            Instruction.Type.START_NAVIGATION -> startNavigation()
                            Instruction.Type.END_NAVIGATION -> endNavigationFromScript()
                            Instruction.Type.ADD_DESTINATION_NAVIGATION -> {
                                val destination = instruction.destination
                                mDestinations.add(destination)
                            }

                            Instruction.Type.POP_DESTINATION_NAVIGATION -> mDestinations.removeAt(0)
                            Instruction.Type.ADD_STEP_NAVIGATION -> {
                                val step = instruction.step
                                mSteps.add(step)
                            }

                            Instruction.Type.POP_STEP_NAVIGATION -> mSteps.removeAt(0)
                            Instruction.Type.SET_TRIP_POSITION_NAVIGATION -> if (isNavigating) {
                                val destinationTravelEstimate =
                                    instruction.destinationTravelEstimate
                                val stepTravelEstimate =
                                    instruction.stepTravelEstimate
                                val tripBuilder = Trip.Builder()
                                tripBuilder
                                    .addStep(mSteps[0]!!, stepTravelEstimate!!)
                                    .addDestination(
                                        mDestinations[0]!!,
                                        destinationTravelEstimate!!
                                    )
                                    .setLoading(false)

                                if (instruction.shouldShowNextStep && nextInstruction != null && mSteps.size > 1) {
                                    val nextStep = mSteps[1]
                                    val nextStepTravelEstimate =
                                        nextInstruction.stepTravelEstimate
                                    if (nextStepTravelEstimate != null) {
                                        tripBuilder.addStep(
                                            nextStep!!,
                                            nextStepTravelEstimate
                                        )
                                    }
                                }

                                val road = instruction.road
                                if (road != null) {
                                    tripBuilder.setCurrentRoad(road)
                                }
                                mNavigationManager!!.updateTrip(tripBuilder.build())

                                if (++mStepsSent % 10 == 0) {
                                    // For demo purposes only play audio of next turn every
                                    // 10 steps.
                                    playNavigationDirection(R.raw.turn_right)
                                    mNotificationManager!!.notify(
                                        NOTIFICATION_ID,
                                        trafficAccidentWarningNotification
                                    )
                                }

                                update( /* isNavigating= */
                                    true,  /* isRerouting= */
                                    false,  /* hasArrived= */
                                    false,
                                    mDestinations,
                                    mSteps,
                                    destinationTravelEstimate,
                                    instruction.stepRemainingDistance,
                                    instruction.shouldNotify,
                                    instruction.notificationTitle,
                                    instruction.notificationContent,
                                    instruction.notificationIcon,
                                    instruction.shouldShowNextStep,
                                    instruction.shouldShowLanes,
                                    instruction.junctionImage
                                )
                            }

                            Instruction.Type.SET_REROUTING -> if (isNavigating) {
                                val destinationTravelEstimate =
                                    instruction.destinationTravelEstimate
                                val tripBuilder = Trip.Builder()
                                tripBuilder
                                    .addDestination(
                                        mDestinations[0]!!,
                                        destinationTravelEstimate!!
                                    )
                                    .setLoading(true)
                                mNavigationManager!!.updateTrip(tripBuilder.build())
                                update( /* isNavigating= */
                                    true,  /* isRerouting= */
                                    true,  /* hasArrived= */
                                    false,
                                    null,
                                    null,
                                    null,
                                    null,
                                    instruction.shouldNotify,
                                    instruction.notificationTitle,
                                    instruction.notificationContent,
                                    instruction.notificationIcon,
                                    instruction.shouldShowNextStep,
                                    instruction.shouldShowLanes,
                                    instruction.junctionImage
                                )
                            }

                            Instruction.Type.SET_ARRIVED -> if (isNavigating) {
                                update( /* isNavigating= */
                                    true,  /* isRerouting= */
                                    false,  /* hasArrived= */
                                    true,
                                    mDestinations,
                                    null,
                                    null,
                                    null,
                                    instruction.shouldNotify,
                                    instruction.notificationTitle,
                                    instruction.notificationContent,
                                    instruction.notificationIcon,
                                    instruction.shouldShowNextStep,
                                    instruction.shouldShowLanes,
                                    instruction.junctionImage
                                )
                            }
                        }
                    }
                })
    }

    fun update(
        isNavigating: Boolean,
        isRerouting: Boolean,
        hasArrived: Boolean,
        destinations: List<Destination?>?,
        steps: List<Step?>?,
        nextDestinationTravelEstimate: TravelEstimate?,
        nextStepRemainingDistance: Distance?,
        shouldNotify: Boolean,
        notificationTitle: String?,
        notificationContent: String?,
        notificationIcon: Int,
        shouldShowNextStep: Boolean,
        shouldShowLanes: Boolean,
        junctionImage: CarIcon?
    ) {
        if (mListener != null) {
            mListener!!.navigationStateChanged(
                isNavigating,
                isRerouting,
                hasArrived,
                destinations,
                steps,
                nextDestinationTravelEstimate,
                nextStepRemainingDistance,
                shouldShowNextStep,
                shouldShowLanes,
                junctionImage
            )
        }

        if (mNotificationManager != null && !TextUtils.isEmpty(notificationTitle)) {
            mNotificationManager!!.notify(
                NAV_NOTIFICATION_ID,
                getNotification(
                    shouldNotify,
                    true,
                    notificationTitle,
                    notificationContent,
                    notificationIcon
                )
            )
        }
    }

    /** Starts navigation.  */
    fun startNavigation() {
        Log.i(TAG, "Starting Navigation")
        startService(Intent(applicationContext, NavigationService::class.java))

        Log.i(TAG, "Starting foreground service")
        startForeground(
            NAV_NOTIFICATION_ID,
            getNotification(
                true,
                false,
                getString(R.string.navigation_active),
                null,
                R.drawable.ic_launcher
            )
        )

        if (mNavigationManager != null) {
            mNavigationManager!!.navigationStarted()
            isNavigating = true
            mListener!!.navigationStateChanged(
                isNavigating,  /* isRerouting= */
                true,  /* hasArrived= */
                false,  /* destinations= */
                null,  /* steps= */
                null,  /* nextDestinationTravelEstimate= */
                null,  /* nextStepRemainingDistance= */
                null,  /* shouldShowNextStep= */
                false,  /* shouldShowLanes= */
                false,  /* junctionImage= */
                null
            )
        }
    }

    /** Stops navigation.  */
    @Suppress("deprecation")
    fun stopNavigation() {
        Log.i(TAG, "Stopping Navigation")
        if (mScript != null) {
            mScript!!.stop()
            mDestinations.clear()
            mSteps.clear()
            mScript = null
        }

        if (mNavigationManager != null) {
            mNavigationManager!!.navigationEnded()
            isNavigating = false
            mListener!!.navigationStateChanged(
                isNavigating,  /* isRerouting= */
                false,  /* hasArrived= */
                false,  /* destinations= */
                null,  /* steps= */
                null,  /* nextDestinationTravelEstimate= */
                null,  /* nextStepRemainingDistance= */
                null,  /* shouldShowNextStep= */
                false,  /* shouldShowLanes= */
                false,  /* junctionImage= */
                null
            )
        }
        stopForeground(true)
        stopSelf()
    }

    private fun playNavigationDirection(@RawRes resourceId: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val carContext = mCarContext ?: return

        val mediaPlayer = MediaPlayer()

        // Use USAGE_ASSISTANCE_NAVIGATION_GUIDANCE as the usage type for any navigation related
        // audio, so that the audio will be played in the car speaker.
        val audioAttributes =
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                .build()

        mediaPlayer.setAudioAttributes(audioAttributes)

        // Request audio focus with AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK so that it will duck ongoing
        // media.
        // Ducking will behave differently depending on what is playing, if it is music it will
        // lower
        // the volume, if it is speech, it will pause it.
        val request =
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(audioAttributes)
                .build()

        val audioManager = carContext.getSystemService(AudioManager::class.java)

        mediaPlayer.setOnCompletionListener { player: MediaPlayer ->
            try {
                // When the audio finishes playing: stop and release the media player.
                player.stop()
                player.release()
            } finally {
                // Release the audio focus so that any previously playing audio can
                // continue.
                audioManager.abandonAudioFocusRequest(request)
            }
        }

        // Requesting the audio focus.
        if (audioManager.requestAudioFocus(request) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // If audio focus is not granted ignore it.
            return
        }

        try {
            // Load our raw resource file, in the case where you synthesize the audio for the given
            // direction, just use that audio file.
            val afd = carContext.resources.openRawResourceFd(resourceId)
            mediaPlayer.setDataSource(
                afd.fileDescriptor, afd.startOffset, afd.length
            )
            afd.close()

            mediaPlayer.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "Failure loading audio resource", e)
            // Release the audio focus so that any previously playing audio can continue.
            audioManager.abandonAudioFocusRequest(request)
        }

        // Start the audio playback.
        mediaPlayer.start()
    }

    private fun endNavigationFromScript() {
        stopNavigation()
    }

    private fun createNotificationChannel() {
        mNotificationManager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val serviceChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager?.createNotificationChannel(serviceChannel)
        }
    }

    /** Returns the [NotificationCompat] used as part of the foreground service.  */
    private fun getNotification(
        shouldNotify: Boolean,
        showInCar: Boolean,
        navigatingDisplayTitle: CharSequence?,
        navigatingDisplayContent: CharSequence?,
        notificationIcon: Int
    ): Notification {
        val builder =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(createMainActivityPendingIntent())
                .setContentTitle(navigatingDisplayTitle)
                .setContentText(navigatingDisplayContent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
                .setOnlyAlertOnce(!shouldNotify) // Set the notification's background color on the car screen.

                .setColor(
                    resources.getColor(
                        R.color.nav_notification_background_color,
                        null
                    )
                )
                .setColorized(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(
                    BitmapFactory.decodeResource(resources, notificationIcon)
                )
                .setTicker(navigatingDisplayTitle)
                .setWhen(System.currentTimeMillis())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
            builder.setPriority(NotificationManager.IMPORTANCE_HIGH)
        }
        if (showInCar) {
            val intent = Intent(Intent.ACTION_VIEW)
                .setComponent(ComponentName(this, NavigationCarAppService::class.java))
                .setData(NavigationCarAppService.createDeepLinkUri(Intent.ACTION_VIEW))
            builder.extend(
                CarAppExtender.Builder()
                    .setImportance(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setContentIntent(
                        CarPendingIntent.getCarApp(
                            this, intent.hashCode(),
                            intent,
                            0
                        )
                    )
                    .build()
            )
        }
        return builder.build()
    }

    private val trafficAccidentWarningNotification: Notification
        get() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Traffic accident ahead")
            .setContentText("Drive slowly")
            .setSmallIcon(R.drawable.ic_settings)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_settings))
            .extend(
                CarAppExtender.Builder()
                    .setImportance(NotificationManagerCompat.IMPORTANCE_HIGH)
                    .build()
            )
            .build()

    private fun createMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    companion object {
        private const val TAG = "NavigationService"

        const val DEEP_LINK_ACTION: String = ("androidx.car.app.samples.navigation.car"
                + ".NavigationDeepLinkAction")
        const val CHANNEL_ID: String = "NavigationServiceChannel"

        /** The identifier for the navigation notification displayed for the foreground service.  */
        private const val NAV_NOTIFICATION_ID = 87654321

        /** The identifier for the non-navigation notifications, such as a traffic accident warning.  */
        private const val NOTIFICATION_ID = 77654321

        // Constants for location broadcast
        private const val PACKAGE_NAME =
            "androidx.car.app.sample.navigation.common.nav.navigationservice"

        private const val EXTRA_STARTED_FROM_NOTIFICATION =
            PACKAGE_NAME + ".started_from_notification"
        const val CANCEL_ACTION: String = "CANCEL"
    }
}
