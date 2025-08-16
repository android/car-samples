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
package androidx.car.app.sample.places.common

import android.Manifest.permission
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

//import androidx.car.app.SessionInfo;
/**
 * Entry point for the app.
 *
 *
 * [CarAppService] is the main interface between the app and the car host. For more
 * details, see the [Android for
 * Cars Library developer guide](https://developer.android.com/training/cars/navigation).
 */
class PlacesCarAppService : CarAppService() {
    //    @Override
    //    public Session onCreateSession(@NonNull SessionInfo sessionInfo) {
    override fun onCreateSession(): Session {
        return object : Session() {
            override fun onCreateScreen(intent: Intent): Screen {
                if (carContext.checkSelfPermission(permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    return PlaceCategoryListScreen.create(carContext)
                }

                // If we do not have the location permission, show the request permission screen.
                val screenManager = carContext.getCarService(ScreenManager::class.java)
                screenManager.push(PlaceCategoryListScreen.create(carContext))
                return RequestPermissionScreen(
                    carContext,
                    RequestPermissionScreen.LocationPermissionCheckCallback { screenManager.pop() })
            }
        }
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
}
