/*
 * Copyright 2021 The Android Open Source Project
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
package androidx.car.app.sample.showcase.common.misc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.car.app.CarAppPermission
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.LongMessageTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.OnClickListener
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.core.location.LocationManagerCompat

/**
 * A screen to show a request for a runtime permission from the user.
 *
 *
 * Scans through the possible dangerous permissions and shows which ones have not been
 * granted in the message. Clicking on the action button will launch the permission request on
 * the phone.
 *
 *
 * If all permissions are granted, corresponding message is displayed with a refresh button which
 * will scan again when clicked.
 */
class RequestPermissionScreen @JvmOverloads constructor(
    carContext: CarContext, // This field can and should be removed once b/192386096 and/or b/192385602 have been resolved.
    private val mPreSeedMode: Boolean = false
) : Screen(carContext) {
    /**
     * Action which invalidates the template.
     *
     *
     * This can give the user a chance to revoke the permissions and then refresh will pickup
     * the permissions that need to be granted.
     */
    private val mRefreshAction = Action.Builder()
        .setTitle(getCarContext().getString(R.string.refresh_action_title))
        .setBackgroundColor(CarColor.BLUE)
        .setOnClickListener { this.invalidate() }
        .build()

    @Suppress("deprecation")
    override fun onGetTemplate(): Template {
        val headerAction = if (mPreSeedMode) Action.APP_ICON else Action.BACK
        val permissions: MutableList<String> = ArrayList()
        val declaredPermissions: Array<String>
        try {
            val info =
                carContext.packageManager.getPackageInfo(
                    carContext.packageName,
                    PackageManager.GET_PERMISSIONS
                )
            declaredPermissions = info.requestedPermissions
        } catch (e: PackageManager.NameNotFoundException) {
            return MessageTemplate.Builder(
                carContext.getString(R.string.package_not_found_error_msg)
            )
                .setHeaderAction(headerAction)
                .addAction(mRefreshAction)
                .build()
        }

        if (declaredPermissions != null) {
            for (declaredPermission in declaredPermissions) {
                // Don't include permissions against the car app host as they are all normal but
                // show up as ungranted by the system.
                if (declaredPermission.startsWith("androidx.car.app")) {
                    continue
                }
                try {
                    CarAppPermission.checkHasPermission(carContext, declaredPermission)
                } catch (e: SecurityException) {
                    permissions.add(declaredPermission)
                }
            }
        }
        if (permissions.isEmpty()) {
            return MessageTemplate.Builder(
                carContext.getString(R.string.permissions_granted_msg)
            )
                .setHeaderAction(headerAction)
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.close_action_title))
                        .setOnClickListener { this.finish() }
                        .build())
                .build()
        }

        val message = StringBuilder()
            .append(carContext.getString(R.string.needs_access_msg_prefix))
        for (permission in permissions) {
            message.append(permission)
            message.append("\n")
        }

        val listener: OnClickListener = ParkedOnlyOnClickListener.create {
            carContext.requestPermissions(
                permissions
            ) { approved: List<String?>?, rejected: List<String?>? ->
                CarToast.makeText(
                    carContext,
                    String.format("Approved: %s Rejected: %s", approved, rejected),
                    CarToast.LENGTH_LONG
                ).show()
            }
            if (!carContext.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.phone_screen_permission_msg),
                    CarToast.LENGTH_LONG
                ).show()
            }
        }

        val action = Action.Builder()
            .setTitle(carContext.getString(R.string.grant_access_action_title))
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener(listener)
            .build()


        var action2: Action? = null
        val locationManager =
            carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            message.append(
                carContext.getString(R.string.enable_location_permission_on_device_msg)
            )
            message.append("\n")
            action2 = Action.Builder()
                .setTitle(carContext.getString(R.string.enable_location_action_title))
                .setBackgroundColor(CarColor.BLUE)
                .setOnClickListener(ParkedOnlyOnClickListener.create {
                    carContext.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                    )
                    if (!carContext.packageManager.hasSystemFeature(
                            PackageManager.FEATURE_AUTOMOTIVE
                        )
                    ) {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.enable_location_permission_on_phone_msg
                            ),
                            CarToast.LENGTH_LONG
                        ).show()
                    }
                })
                .build()
        }


        val builder = LongMessageTemplate.Builder(message)
            .setTitle(carContext.getString(R.string.required_permissions_title))
            .addAction(action)
            .setHeaderAction(headerAction)

        if (action2 != null) {
            builder.addAction(action2)
        }

        return builder.build()
    }
}
