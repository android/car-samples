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
package androidx.car.app.sample.showcase.common.navigation.routing

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/** A screen showing a demos for the navigation template in different states.  */
class NavigationTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.loading_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(
                            LoadingDemoScreen(
                                carContext
                            )
                        )
                }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.navigating_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(NavigatingDemoScreen(carContext))
                }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.arrived_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(ArrivedDemoScreen(carContext))
                }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.junction_image_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(JunctionImageDemoScreen(carContext))
                }
                .build())

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.nav_template_demos_title))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
