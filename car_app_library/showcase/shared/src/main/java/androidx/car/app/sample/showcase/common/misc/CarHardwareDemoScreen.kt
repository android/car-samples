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

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.ShowcaseSession
import androidx.car.app.sample.showcase.common.renderer.CarHardwareRenderer
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Simple demo of how access car hardware information.  */
class CarHardwareDemoScreen(
    carContext: CarContext,
    showcaseSession: ShowcaseSession
) : Screen(carContext) {
    val mCarHardwareRenderer: CarHardwareRenderer = CarHardwareRenderer(carContext)

    init {
        val lifecycle = lifecycle
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            val mShowcaseSession: ShowcaseSession = showcaseSession

            override fun onResume(owner: LifecycleOwner) {
                // When this screen is visible set the SurfaceRenderer to show
                // CarHardware information.
                mShowcaseSession.overrideRenderer(mCarHardwareRenderer)
            }

            override fun onPause(owner: LifecycleOwner) {
                // When this screen is hidden set the SurfaceRenderer to show
                // CarHardware information.
                mShowcaseSession.overrideRenderer(null)
            }
        })
    }

    override fun onGetTemplate(): Template {
        val actionStrip =
            ActionStrip.Builder() // Add a Button to show the CarHardware info screen
                .addAction(
                    Action.Builder()
                        .setIcon(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable.info_gm_grey_24dp
                                )
                            )
                                .build()
                        )
                        .setOnClickListener {
                            screenManager.push(
                                CarHardwareInfoScreen(carContext)
                            )
                        }
                        .build())
                .addAction(
                    Action.Builder()
                        .setTitle(
                            carContext
                                .getString(R.string.back_caps_action_title)
                        )
                        .setOnClickListener { this.finish() }
                        .build())
                .build()

        return NavigationTemplate.Builder().setActionStrip(actionStrip).build()
    }
}
