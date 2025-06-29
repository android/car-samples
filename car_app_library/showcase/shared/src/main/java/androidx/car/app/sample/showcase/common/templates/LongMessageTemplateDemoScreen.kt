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
package androidx.car.app.sample.showcase.common.templates

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.LongMessageTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels

/** A screen that demonstrates the long message template.  */
class LongMessageTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        if (carContext.carAppApiLevel < CarAppApiLevels.LEVEL_2) {
            return MessageTemplate.Builder(
                carContext.getString(R.string.long_msg_template_not_supported_text)
            )
                .setTitle(
                    carContext.getString(
                        R.string.long_msg_template_not_supported_title
                    )
                )
                .setHeaderAction(Action.BACK)
                .build()
        }

        val primaryActionBuilder = Action.Builder()
            .setOnClickListener(
                ParkedOnlyOnClickListener.create {
                    screenManager.pop()
                    CarToast.makeText(
                        carContext,
                        carContext.getString(R.string.primary_action_title),
                        CarToast.LENGTH_LONG
                    ).show()
                })
            .setTitle(carContext.getString(R.string.accept_action_title))
        if (carContext.carAppApiLevel >= CarAppApiLevels.LEVEL_4) {
            primaryActionBuilder.setFlags(Action.FLAG_PRIMARY)
        }

        return LongMessageTemplate.Builder(
            carContext.getString(R.string.long_msg_template_text)
        )
            .setTitle(carContext.getString(R.string.long_msg_template_demo_title))
            .setHeaderAction(Action.BACK)
            .addAction(primaryActionBuilder.build())
            .addAction(
                Action.Builder()
                    .setBackgroundColor(CarColor.RED)
                    .setOnClickListener(
                        ParkedOnlyOnClickListener.create { screenManager.pop() })
                    .setTitle(carContext.getString(R.string.reject_action_title))
                    .build()
            )
            .setActionStrip(ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.more_action_title))
                        .setOnClickListener {
                            CarToast.makeText(
                                carContext,
                                carContext.getString(
                                    R.string.more_toast_msg
                                ),
                                CarToast.LENGTH_LONG
                            )
                                .show()
                        }
                        .build())
                .build())
            .build()
    }
}
