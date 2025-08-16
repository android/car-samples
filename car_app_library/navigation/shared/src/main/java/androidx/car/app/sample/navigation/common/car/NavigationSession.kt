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

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.sample.navigation.common.model.Instruction
import androidx.car.app.sample.navigation.common.nav.NavigationService
import androidx.car.app.sample.navigation.common.nav.NavigationService.LocalBinder
import androidx.core.graphics.drawable.IconCompat
import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Session class for the Navigation sample app.  */
internal class NavigationSession(sessionInfo: SessionInfo) : Session(), NavigationScreen.Listener {
    var mNavigationScreen: NavigationScreen? = null

    var mNavigationCarSurface: SurfaceRenderer? = null

    // A reference to the navigation service used to get location updates and routing.
    var mService: NavigationService? = null

    var mSettingsAction: Action? = null

    val mServiceListener: NavigationService.Listener = object : NavigationService.Listener {
        override fun navigationStateChanged(
            isNavigating: Boolean,
            isRerouting: Boolean,
            hasArrived: Boolean,
            destinations: List<Destination?>?,
            steps: List<Step?>?,
            nextDestinationTravelEstimate: TravelEstimate?,
            nextStepRemainingDistance: Distance?,
            shouldShowNextStep: Boolean,
            shouldShowLanes: Boolean,
            junctionImage: CarIcon?,
        ) {
            mNavigationScreen!!.updateTrip(
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
    }

    // A listener to periodically update the surface with the location coordinates
    var mLocationListener: LocationListenerCompat = LocationListenerCompat { location: Location? ->
        mNavigationCarSurface!!.updateLocationString(
            getLocationString(location)
        )
    }

    // Monitors the state of the connection to the Navigation service.
    val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(TAG, "In onServiceConnected() component:$name")
            val binder = service as LocalBinder
            mService = binder.service
            mService!!.setCarContext(carContext, mServiceListener)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i(TAG, "In onServiceDisconnected() component:$name")
            // Unhook map models here
            mService!!.clearCarContext()
            mService = null
        }
    }

    private val mLifeCycleObserver: LifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onCreate()")
        }

        override fun onStart(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onStart()")
            carContext
                .bindService(
                    Intent(carContext, NavigationService::class.java),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE
                )
        }

        override fun onResume(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onResume()")
        }

        override fun onPause(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onPause()")
        }

        override fun onStop(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onStop()")
            carContext.unbindService(mServiceConnection)
            mService = null
        }

        override fun onDestroy(lifecycleOwner: LifecycleOwner) {
            Log.i(TAG, "In onDestroy()")

            val locationManager =
                carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(mLocationListener)
        }
    }

    init {
        if (sessionInfo.displayType == SessionInfo.DISPLAY_TYPE_MAIN) {
            val lifecycle = lifecycle
            lifecycle.addObserver(mLifeCycleObserver)
        }
    }

    override fun onCreateScreen(intent: Intent): Screen {
        Log.i(TAG, "In onCreateScreen()")

        mSettingsAction =
            Action.Builder()
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext, R.drawable.ic_settings
                        )
                    )
                        .build()
                )
                .setOnClickListener {
                    carContext
                        .getCarService(ScreenManager::class.java)
                        .push(SettingsScreen(carContext))
                }
                .build()

        mNavigationCarSurface = SurfaceRenderer(carContext, lifecycle)
        mNavigationScreen =
            NavigationScreen(carContext, mSettingsAction!!, this, mNavigationCarSurface!!)

        val action = intent.action
        if (action != null && CarContext.ACTION_NAVIGATE == action) {
            CarToast.makeText(
                carContext,
                "Navigation intent: " + intent.dataString,
                CarToast.LENGTH_LONG
            )
                .show()
        }

        if (carContext.checkSelfPermission(permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            // If we do not have the location permission, preseed the navigation screen first, and
            // push
            // the request permission screen. When the user grants the location permission, the
            // request
            // permission screen will be popped and the navigation screen will be displayed.
            carContext.getCarService(ScreenManager::class.java).push(
                mNavigationScreen!!
            )
            return RequestPermissionScreen(carContext) { this.requestLocationUpdates() }
        }

        return mNavigationScreen!!
    }

    override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "In onNewIntent() $intent")
        val screenManager = carContext.getCarService(
            ScreenManager::class.java
        )
        if (CarContext.ACTION_NAVIGATE == intent.action) {
            val uri = Uri.parse("http://" + intent.dataString)
            screenManager.popToRoot()
            screenManager.pushForResult(
                SearchResultsScreen(
                    carContext,
                    mSettingsAction!!,
                    mNavigationCarSurface!!,
                    uri.getQueryParameter("q")!!
                )
            ) { obj: Any? ->
                if (obj != null) {
                    // Need to copy over each element to satisfy Java type safety.
                    val results = obj as List<*>
                    val instructions: MutableList<Instruction> = ArrayList()
                    for (result in results) {
                        instructions.add(result as Instruction)
                    }
                    executeScript(instructions)
                }
            }

            return
        }

        // Process the intent from DeepLinkNotificationReceiver. Bring the routing screen back to
        // the
        // top if any other screens were pushed onto it.
        val uri = intent.data
        if (uri != null && URI_SCHEME == uri.scheme && URI_HOST == uri.schemeSpecificPart) {
            val top = screenManager.top
            when (uri.fragment) {
                NavigationService.DEEP_LINK_ACTION -> if (top !is NavigationScreen) {
                    screenManager.popToRoot()
                }

                else -> {}
            }
        }
    }

    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        mNavigationCarSurface!!.onCarConfigurationChanged()
    }

    override fun executeScript(instructions: List<Instruction>) {
        if (mService != null) {
            mService!!.executeInstructions(instructions)
        }
    }

    override fun stopNavigation() {
        if (mService != null) {
            mService!!.stopNavigation()
        }
    }

    /**
     * Requests location updates for the navigation surface.
     *
     * @throws java.lang.SecurityException if the app does not have the location permission.
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        val locationManager =
            carContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        mNavigationCarSurface!!.updateLocationString(getLocationString(location))
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,  /* minTimeMs= */
            1000,  /* minDistanceM= */
            1f,
            mLocationListener
        )
    }

    companion object {
        val TAG: String = NavigationSession::class.java.simpleName

        const val URI_SCHEME: String = "samples"
        const val URI_HOST: String = "navigation"

        fun getLocationString(location: Location?): String {
            if (location == null) {
                return "unknown"
            }
            return ("time: "
                    + location.time
                    + " lat: "
                    + location.latitude
                    + " lng: "
                    + location.longitude)
        }
    }
}
