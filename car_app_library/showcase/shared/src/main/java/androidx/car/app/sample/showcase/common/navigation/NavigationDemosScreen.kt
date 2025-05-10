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
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.navigation.routing.NavigationTemplateDemoScreen
import androidx.core.graphics.drawable.IconCompat

/** A screen showing a list of navigation demos  */
class NavigationDemosScreen @JvmOverloads constructor(
    carContext: CarContext,
    private val mPage: Int = 0
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        when (mPage) {
            0 -> {
                listBuilder.addItem(
                    Row.Builder()
                        .setImage(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable.ic_explore_white_24dp
                                )
                            )
                                .build(),
                            Row.IMAGE_TYPE_ICON
                        )
                        .setTitle(
                            carContext.getString(
                                R.string.nav_template_demos_title
                            )
                        )
                        .setOnClickListener {
                            screenManager
                                .push(
                                    NavigationTemplateDemoScreen(
                                        carContext
                                    )
                                )
                        }
                        .setBrowsable(true)
                        .build())
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.place_list_nav_template_demo_title),
                        PlaceListNavigationTemplateDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.route_preview_template_demo_title),
                        RoutePreviewDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.notification_template_demo_title),
                        NavigationNotificationsDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.nav_map_template_demo_title),
                        NavigationMapOnlyScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.map_template_pane_demo_title),
                        MapTemplateWithPaneDemoScreen(carContext)
                    )
                )
            }

            1 -> listBuilder.addItem(
                createRow(
                    carContext.getString(R.string.map_template_list_demo_title),
                    MapTemplateWithListDemoScreen(carContext)
                )
            )
        }
        val builder = ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.nav_demos_title))
            .setHeaderAction(Action.BACK)

        if (mPage + 1 < MAX_PAGES) {
            builder.setActionStrip(ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.more_action_title))
                        .setOnClickListener {
                            screenManager.push(
                                NavigationDemosScreen(carContext, mPage + 1)
                            )
                        }
                        .build())
                .build())
        }

        return builder.build()
    }

    private fun createRow(title: String, screen: Screen): Row {
        return Row.Builder()
            .setTitle(title)
            .setOnClickListener { screenManager.push(screen) }
            .build()
    }

    companion object {
        private const val MAX_PAGES = 2
    }
}
