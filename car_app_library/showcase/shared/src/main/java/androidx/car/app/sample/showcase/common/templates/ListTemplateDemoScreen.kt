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

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarText
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.lifecycle.DefaultLifecycleObserver
import kotlin.math.min

/**
 * Creates a screen that demonstrates usage of the full screen [ListTemplate] to display a
 * full-screen list.
 */
class ListTemplateDemoScreen(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setOnClickListener(
                    ParkedOnlyOnClickListener.create {
                        onClick(
                            carContext.getString(R.string.parked_toast_msg)
                        )
                    })
                .setTitle(carContext.getString(R.string.parked_only_title))
                .addText(carContext.getString(R.string.parked_only_text))
                .build()
        )

        // Some hosts may allow more items in the list than others, so create more.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            val listLimit = min(
                MAX_LIST_ITEMS.toDouble(),
                carContext.getCarService(ConstraintManager::class.java)
                    .getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_LIST
                    ).toDouble()
            ).toInt()

            for (i in 2..listLimit) {
                // For row text, set text variants that fit best in different screen sizes.
                val secondTextStr = carContext.getString(R.string.second_line_text)
                val secondText =
                    CarText.Builder(
                        "================= $secondTextStr ================"
                    )
                        .addVariant(
                            "--------------------- " + secondTextStr
                                    + " ----------------------"
                        )
                        .addVariant(secondTextStr)
                        .build()
                val onClickText = (carContext.getString(R.string.clicked_row_prefix)
                        + ": " + i)
                val rowBuilder = Row.Builder()
                    .setOnClickListener { onClick(onClickText) }
                    .setTitle(
                        carContext.getString(R.string.title_prefix) + " " + i
                    )
                if (i % 2 == 0) {
                    rowBuilder.addText(carContext.getString(R.string.long_line_text))
                } else {
                    rowBuilder
                        .addText(carContext.getString(R.string.first_line_text))
                        .addText(secondText)
                }
                listBuilder.addItem(rowBuilder.build())
            }
        }

        val settings = Action.Builder()
            .setTitle(
                carContext.getString(
                    R.string.settings_action_title
                )
            )
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(
                        R.string.settings_toast_msg
                    ),
                    CarToast.LENGTH_LONG
                )
                    .show()
            }
            .build()

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.list_template_demo_title))
            .setHeaderAction(Action.BACK)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(settings)
                    .build()
            )
            .build()
    }

    private fun onClick(text: String) {
        CarToast.makeText(carContext, text, CarToast.LENGTH_LONG).show()
    }

    companion object {
        private const val MAX_LIST_ITEMS = 100
    }
}
