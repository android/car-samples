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
package androidx.car.app.sample.navigation.common.app

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.car.app.connection.CarConnection
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.sample.navigation.common.nav.NavigationService
import androidx.car.app.sample.navigation.common.nav.NavigationService.LocalBinder

/**
 * The main app activity.
 *
 *
 * See [androidx.car.app.sample.navigation.common.car.NavigationCarAppService] for the
 * app's entry point to the cat host.
 */
class MainActivity : ComponentActivity() {
    // A reference to the navigation service used to get location updates and routing.
    var mService: NavigationService? = null

    // Tracks the bound state of the navigation service.
    var mIsBound: Boolean = false

    // Monitors the state of the connection to the navigation service.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(TAG, "In onServiceConnected() component:$name")
            val binder = service as LocalBinder
            mService = binder.service
            mIsBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i(TAG, "In onServiceDisconnected() component:$name")
            mService = null
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "In onCreate()")

        setContentView(R.layout.activity_main)

        // Hook up some manual navigation controls.
        val startNavButton = findViewById<Button>(R.id.start_nav)
        startNavButton.setOnClickListener { view: View -> this.startNavigation(view) }
        val stopNavButton = findViewById<Button>(R.id.stop_nav)
        stopNavButton.setOnClickListener { view: View -> this.stopNavigation(view) }

        CarConnection(this).type.observe(
            this
        ) { connectionState: Int -> this.onConnectionStateUpdate(connectionState) }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "In onStart()")
        bindService(
            Intent(this, NavigationService::class.java),
            mServiceConnection,
            BIND_AUTO_CREATE
        )
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }

    override fun onStop() {
        Log.i(TAG, "In onStop(). bound$mIsBound")
        if (mIsBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mIsBound = false
            mService = null
        }
        super.onStop()
    }

    private fun onConnectionStateUpdate(connectionState: Int) {
        val message = if (connectionState > CarConnection.CONNECTION_TYPE_NOT_CONNECTED
        ) "Connected to a car head unit"
        else "Not Connected to a car head unit"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun startNavigation(view: View) {
        if (mService != null) {
            mService!!.startNavigation()
        }
    }

    private fun stopNavigation(view: View) {
        if (mService != null) {
            mService!!.stopNavigation()
        }
    }

    companion object {
        val TAG: String = MainActivity::class.java.simpleName
    }
}
