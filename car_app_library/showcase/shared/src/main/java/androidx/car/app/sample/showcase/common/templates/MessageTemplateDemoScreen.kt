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
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat

/** A screen that demonstrates the message template.  */
class MessageTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val primaryActionBuilder = Action.Builder()
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.primary_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }
            .setTitle(carContext.getString(R.string.ok_action_title))
        if (carContext.carAppApiLevel >= CarAppApiLevels.LEVEL_4) {
            primaryActionBuilder.setFlags(Action.FLAG_PRIMARY)
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

        return MessageTemplate.Builder(
            carContext.getString(R.string.msg_template_demo_text)
        )
            .setTitle(carContext.getString(R.string.msg_template_demo_title))
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.ic_emoji_food_beverage_white_48dp
                    )
                )
                    .setTint(CarColor.GREEN)
                    .build()
            )
            .setHeaderAction(Action.BACK)
            .addAction(primaryActionBuilder.build())
            .addAction(
                Action.Builder()
                    .setBackgroundColor(CarColor.RED)
                    .setTitle(carContext.getString(R.string.throw_action_title))
                    .setOnClickListener {
                        throw RuntimeException("Error")
                    }
                    .build())

            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(settings)
                    .build()
            )
            .build()
    }
}
