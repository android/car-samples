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
package androidx.car.app.sample.navigation.common.car

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.validation.HostValidator
import androidx.core.app.NotificationCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Entry point for the templated app.
 *
 *
 * [CarAppService] is the main interface between the app and the car host. For more
 * details, see the [Android for
 * Cars Library developer guide](https://developer.android.com/training/cars/navigation).
 */
class NavigationCarAppService : CarAppService() {
    @Suppress("deprecation")
    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        createNotificationChannel()

        // Turn the car app service into a foreground service in order to make sure we can use all
        // granted "while-in-use" permissions (e.g. location) in the app's process.
        // The "while-in-use" location permission is granted as long as there is a foreground
        // service running in a process in which location access takes place. Here, we set this
        // service, and not NavigationService (which runs only during navigation), as a
        // foreground service because we need location access even when not navigating. If
        // location access is needed only during navigation, we can set NavigationService as a
        // foreground service instead.
        // See https://developer.android.com/reference/com/google/android/libraries/car/app
        // /CarAppService#accessing-location for more details.
        startForeground(NOTIFICATION_ID, notification)
        val session = NavigationSession(sessionInfo)
        session.lifecycle
            .addObserver(
                object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        stopForeground(true)
                    }
                })

        return session
    }

    override fun createHostValidator(): HostValidator {
        return if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext)
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build()
        }
    }

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val name: CharSequence = "Car App Service"
            val serviceChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private val notification: Notification
        /** Returns the [NotificationCompat] used as part of the foreground service.  */
        get() {
            val builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Navigation App")
                    .setContentText("App is running")
                    .setSmallIcon(R.drawable.ic_launcher)

            if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
                builder.setPriority(NotificationManager.IMPORTANCE_HIGH)
            }
            return builder.build()
        }

    companion object {
        /** Navigation session channel id.  */
        const val CHANNEL_ID: String = "NavigationSessionChannel"

        /** The identifier for the notification displayed for the foreground service.  */
        private const val NOTIFICATION_ID = 97654321

        /** Create a deep link URL from the given deep link action.  */
        fun createDeepLinkUri(deepLinkAction: String): Uri {
            return Uri.fromParts(
                NavigationSession.Companion.URI_SCHEME,
                NavigationSession.Companion.URI_HOST,
                deepLinkAction
            )
        }
    }
}
