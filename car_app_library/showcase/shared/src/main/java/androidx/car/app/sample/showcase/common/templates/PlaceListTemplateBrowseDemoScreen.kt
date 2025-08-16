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
package androidx.car.app.sample.showcase.common.templates

import android.Manifest.permission
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.HandlerThread
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarLocation
import androidx.car.app.model.ItemList
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Creates a screen using the [PlaceListMapTemplate].
 *
 *
 * This screen shows the ability to anchor the map around the current location when there are
 * no other POI markers present.
 */
class PlaceListTemplateBrowseDemoScreen(carContext: CarContext) : Screen(carContext) {
    val mLocationListener: LocationListenerCompat
    val mLocationUpdateHandlerThread: HandlerThread
    var mHasPermissionLocation: Boolean

    private var mCurrentLocation: Location? = null

    init {
        mHasPermissionLocation = (carContext.checkSelfPermission(permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || carContext.checkSelfPermission(permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)

        mLocationUpdateHandlerThread = HandlerThread("LocationThread")
        mLocationListener = LocationListenerCompat { location: Location? ->
            mCurrentLocation = location
            invalidate()
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                mHasPermissionLocation =
                    (carContext.checkSelfPermission(permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED
                            || carContext.checkSelfPermission(permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                if (mHasPermissionLocation) {
                    val locationManager =
                        carContext.getSystemService(LocationManager::class.java)
                    locationManager.requestLocationUpdates(
                        LocationManager.FUSED_PROVIDER,
                        LOCATION_UPDATE_MIN_INTERVAL_MILLIS.toLong(),
                        LOCATION_UPDATE_MIN_DISTANCE_METER.toFloat(),
                        mLocationListener,
                        mLocationUpdateHandlerThread.looper
                    )
                } else {
                    CarToast.makeText(
                        carContext,
                        getCarContext().getString(R.string.grant_location_permission_toast_msg),
                        CarToast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                val locationManager =
                    getCarContext().getSystemService(LocationManager::class.java)
                locationManager.removeUpdates(mLocationListener)
            }
        })
    }

    override fun onGetTemplate(): Template {
        val builder = PlaceListMapTemplate.Builder()
            .setItemList(
                ItemList.Builder()
                    .addItem(
                        Row.Builder()
                            .setTitle(carContext.getString(R.string.browse_places_title))
                            .setBrowsable(true)
                            .setOnClickListener {
                                screenManager.push(
                                    PlaceListTemplateDemoScreen(
                                        carContext
                                    )
                                )
                            }.build()
                    )
                    .build()
            )
            .setTitle(carContext.getString(R.string.place_list_template_demo_title))
            .setHeaderAction(Action.BACK)
            .setCurrentLocationEnabled(mHasPermissionLocation)

        mCurrentLocation?.let {
            builder.setAnchor(Place.Builder(CarLocation.create(it)).build())
        }

        return builder.build()
    }

    companion object {
        private const val LOCATION_UPDATE_MIN_INTERVAL_MILLIS = 1000
        private const val LOCATION_UPDATE_MIN_DISTANCE_METER = 1
    }
}
