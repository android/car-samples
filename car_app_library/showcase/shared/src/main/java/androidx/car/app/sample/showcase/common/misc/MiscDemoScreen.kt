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
package androidx.car.app.sample.showcase.common.misc

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.ShowcaseSession

/** Creates a screen that has an assortment of API demos.  */
class MiscDemoScreen @JvmOverloads constructor(
    carContext: CarContext,
    showcaseSession: ShowcaseSession, page: Int = 0
) : Screen(carContext) {
    private val mPage: Int

    private val mShowcaseSession: ShowcaseSession

    init {
        marker = MARKER
        mShowcaseSession = showcaseSession
        mPage = page
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        when (mPage) {
            0 -> {
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.notification_demo),
                        NotificationDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.pop_to_title),
                        PopToDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.loading_demo_title),
                        LoadingDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.request_permissions_title),
                        RequestPermissionScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.finish_app_demo_title),
                        FinishAppScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.car_hardware_demo_title),
                        CarHardwareDemoScreen(carContext, mShowcaseSession)
                    )
                )
            }

            1 -> {
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.content_limits_demo_title),
                        ContentLimitsDemoScreen(carContext)
                    )
                )
                listBuilder.addItem(
                    createRow(
                        carContext.getString(R.string.color_demo),
                        ColorDemoScreen(carContext)
                    )
                )
            }
        }
        val builder = ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.misc_demo_title))
            .setHeaderAction(Action.BACK)

        if (mPage + 1 < MAX_PAGES) {
            builder.setActionStrip(ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.more_action_title))
                        .setOnClickListener {
                            screenManager.push(
                                MiscDemoScreen(
                                    carContext, mShowcaseSession,
                                    mPage + 1
                                )
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
        const val MARKER: String = "MiscDemoScreen"
        private const val MAX_PAGES = 2
    }
}
