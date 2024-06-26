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
package androidx.car.app.sample.showcase.common.navigation

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.sample.showcase.common.R

/** Simple demo of how to present a navigation screen with only a map.  */
class NavigationMapOnlyScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val actionStrip =
            ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(
                            carContext.getString(
                                R.string.back_caps_action_title
                            )
                        )
                        .setOnClickListener { this.finish() }
                        .build())
                .build()

        return NavigationTemplate.Builder().setActionStrip(actionStrip).build()
    }
}
