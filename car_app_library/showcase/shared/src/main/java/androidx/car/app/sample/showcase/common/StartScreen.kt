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

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.misc.MiscDemoScreen
import androidx.car.app.sample.showcase.common.navigation.NavigationDemosScreen
import androidx.car.app.sample.showcase.common.templates.MiscTemplateDemosScreen
import androidx.car.app.sample.showcase.common.textandicons.TextAndIconsDemosScreen
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat

/** The starting screen of the app.  */
class StartScreen(carContext: CarContext, private val mShowcaseSession: ShowcaseSession) :
    Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.selectable_lists_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(
                            SelectableListsDemoScreen(
                                carContext
                            )
                        )
                }
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.task_restriction_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(
                            TaskRestrictionDemoScreen(
                                1, carContext
                            )
                        )
                }
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_map_white_48dp
                        )
                    )
                        .build(),
                    Row.IMAGE_TYPE_ICON
                )
                .setTitle(carContext.getString(R.string.nav_demos_title))
                .setOnClickListener {
                    screenManager
                        .push(NavigationDemosScreen(carContext))
                }
                .setBrowsable(true)
                .build())
        var listLimit = 6
        // Adjust the item limit according to the car constrains.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            listLimit = carContext.getCarService(ConstraintManager::class.java).getContentLimit(
                ConstraintManager.CONTENT_LIMIT_TYPE_LIST
            )
        }
        val miscTemplateDemoScreenItemLimit = listLimit
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.misc_templates_demos_title))
                .setOnClickListener {
                    screenManager
                        .push(
                            MiscTemplateDemosScreen(
                                carContext,
                                0,
                                miscTemplateDemoScreenItemLimit
                            )
                        )
                }
                .setBrowsable(true)
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.text_icons_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(TextAndIconsDemosScreen(carContext))
                }
                .setBrowsable(true)
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.misc_demo_title))
                .setOnClickListener {
                    screenManager
                        .push(
                            MiscDemoScreen(
                                carContext,
                                mShowcaseSession
                            )
                        )
                }
                .setBrowsable(true)
                .build())
        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(
                carContext.getString(R.string.showcase_demos_title) + " ("
                        + carContext.getString(
                    R.string.cal_api_level_prefix,
                    carContext.carAppApiLevel
                ) + ")"
            )
            .setHeaderAction(Action.APP_ICON)
            .build()
    }
}