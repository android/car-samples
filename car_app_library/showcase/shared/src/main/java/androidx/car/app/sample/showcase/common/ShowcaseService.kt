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
package androidx.car.app.sample.showcase.common

import android.content.pm.ApplicationInfo
import android.net.Uri
import androidx.car.app.CarAppService
import androidx.car.app.R
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator

/**
 * Entry point for the showcase app.
 *
 *
 * [CarAppService] is the main interface between the app and the car host. For more
 * details, see the [Android for
 * Cars Library developer guide](https://developer.android.com/training/cars/navigation).
 */
class ShowcaseService : CarAppService() {
    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return ShowcaseSession()
    }

    override fun createHostValidator(): HostValidator {
        return if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext)
                .addAllowedHosts(R.array.hosts_allowlist_sample)
                .build()
        }
    }

    companion object {
        const val SHARED_PREF_KEY: String = "ShowcasePrefs"
        const val PRE_SEED_KEY: String = "PreSeed"

        // Intent actions for notification actions in car and phone
        const val INTENT_ACTION_NAVIGATE: String =
            "androidx.car.app.sample.showcase.INTENT_ACTION_PHONE"
        const val INTENT_ACTION_CALL: String =
            "androidx.car.app.sample.showcase.INTENT_ACTION_CANCEL_RESERVATION"
        const val INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP: String =
            "androidx.car.app.sample.showcase.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP"

        /** Creates a deep link URI with the given deep link action.  */
        @JvmStatic
        fun createDeepLinkUri(deepLinkAction: String): Uri {
            return Uri.fromParts(
                ShowcaseSession.URI_SCHEME,
                ShowcaseSession.URI_HOST,
                deepLinkAction
            )
        }
    }
}
