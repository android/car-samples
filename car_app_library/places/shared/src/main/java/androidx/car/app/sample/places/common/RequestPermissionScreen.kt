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
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.OnClickListener
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template

/** Screen for asking the user to grant location permission.  */
class RequestPermissionScreen(
    carContext: CarContext,
    private var mLocationPermissionCheckCallback: LocationPermissionCheckCallback
) : Screen(carContext) {
    /** Callback called when the location permission is granted.  */
    fun interface LocationPermissionCheckCallback {
        /** Callback called when the location permission is granted.  */
        fun onPermissionGranted()
    }

    override fun onGetTemplate(): Template {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(permission.ACCESS_FINE_LOCATION)

        val message = "This app needs access to location in order to show the map around you"

        val listener: OnClickListener = ParkedOnlyOnClickListener.create {
            carContext.requestPermissions(
                permissions
            ) { approved: List<String?>, rejected: List<String?>? ->
                CarToast.makeText(
                    carContext,
                    String.format("Approved: %s Rejected: %s", approved, rejected),
                    CarToast.LENGTH_LONG
                ).show()
                if (!approved.isEmpty()) {
                    mLocationPermissionCheckCallback.onPermissionGranted()
                    finish()
                }
            }
        }

        val action = Action.Builder()
            .setTitle("Grant Access")
            .setBackgroundColor(CarColor.GREEN)
            .setOnClickListener(listener)
            .build()

        return MessageTemplate.Builder(message).addAction(action).setHeaderAction(
            Action.APP_ICON
        ).build()
    }
}
