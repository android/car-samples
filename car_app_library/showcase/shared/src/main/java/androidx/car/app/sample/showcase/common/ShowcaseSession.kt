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

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.ScreenManager
import androidx.car.app.Session
import androidx.car.app.sample.showcase.common.misc.RequestPermissionScreen
import androidx.car.app.sample.showcase.common.misc.ResultDemoScreen
import androidx.car.app.sample.showcase.common.navigation.NavigationNotificationService
import androidx.car.app.sample.showcase.common.navigation.NavigationNotificationsDemoScreen
import androidx.car.app.sample.showcase.common.navigation.routing.NavigatingDemoScreen
import androidx.car.app.sample.showcase.common.renderer.Renderer
import androidx.car.app.sample.showcase.common.renderer.SurfaceController
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Session class for the Showcase sample app.  */
class ShowcaseSession : Session(), DefaultLifecycleObserver {
    private var mSurfaceController: SurfaceController? = null

    override fun onCreateScreen(intent: Intent): Screen {
        val lifecycle = lifecycle
        lifecycle.addObserver(this)
        mSurfaceController = SurfaceController(carContext, lifecycle)

        if (CarContext.ACTION_NAVIGATE == intent.action) {
            // Handle the navigation Intent by pushing first the "home" screen onto the stack, then
            // returning the screen that we want to show a template for.
            // Doing this allows the app to go back to the previous screen when the user clicks on a
            // back action.
            carContext
                .getCarService(ScreenManager::class.java)
                .push(StartScreen(carContext, this))
            return NavigatingDemoScreen(carContext)
        }

        if (carContext.callingComponent != null) {
            // Similarly, if the application has been called "for result", we push a "home"
            // screen onto the stack and return the results demo screen.
            carContext
                .getCarService(ScreenManager::class.java)
                .push(StartScreen(carContext, this))
            return ResultDemoScreen(carContext)
        }

        // For demo purposes this uses a shared preference setting to store whether we should
        // pre-seed the screen back stack. This allows the app to have a way to go back to the
        // home/start screen making the home/start screen the 0th position.
        // For a real application, it would probably check if it has all the needed system
        // permissions, and if any are missing, it would pre-seed the start screen and return a
        // screen that will send the user to the phone to grant the needed permissions.
        val shouldPreSeedBackStack =
            carContext
                .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                .getBoolean(ShowcaseService.PRE_SEED_KEY, false)
        if (shouldPreSeedBackStack) {
            // Reset so that we don't require it next time
            carContext
                .getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(ShowcaseService.PRE_SEED_KEY, false)
                .apply()

            carContext
                .getCarService(ScreenManager::class.java)
                .push(StartScreen(carContext, this))
            return RequestPermissionScreen(carContext,  /*preSeedMode*/true)
        }
        return StartScreen(carContext, this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.i("SHOWCASE", "onDestroy")

        // Stop navigation notification service if it is running.
        val context = carContext
        context.stopService(Intent(context, NavigationNotificationService::class.java))
    }

    override fun onNewIntent(intent: Intent) {
        // Process various deeplink intents.

        val screenManager = carContext.getCarService(
            ScreenManager::class.java
        )

        if (CarContext.ACTION_NAVIGATE == intent.action) {
            // If the Intent is to navigate, and we aren't already, push the navigation screen.
            if (screenManager.top is NavigatingDemoScreen) {
                return
            }
            screenManager.push(NavigatingDemoScreen(carContext))
            return
        }

        if (carContext.callingComponent != null) {
            // Remove any other instances of the results screen.
            screenManager.popToRoot()
            screenManager.push(ResultDemoScreen(carContext))
            return
        }

        val uri = intent.data
        if (uri != null && URI_SCHEME == uri.scheme && URI_HOST == uri.schemeSpecificPart) {
            val top = screenManager.top
            // No-op
            if (ShowcaseService.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP == uri.fragment) {
                if (top !is NavigationNotificationsDemoScreen) {
                    screenManager.push(NavigationNotificationsDemoScreen(carContext))
                }
            }
        }
    }

    override fun onCarConfigurationChanged(configuration: Configuration) {
        if (mSurfaceController != null) {
            mSurfaceController!!.onCarConfigurationChanged()
        }
    }

    /** Tells the session whether to override the default renderer.  */
    fun overrideRenderer(renderer: Renderer?) {
        mSurfaceController!!.overrideRenderer(renderer)
    }

    companion object {
        const val URI_SCHEME: String = "samples"
        const val URI_HOST: String = "showcase"
    }
}
