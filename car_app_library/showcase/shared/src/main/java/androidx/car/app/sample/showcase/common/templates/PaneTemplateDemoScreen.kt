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

import android.graphics.BitmapFactory
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Creates a screen that demonstrates usage of the full screen [PaneTemplate] to display a
 * details screen.
 */
class PaneTemplateDemoScreen(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    private var mPaneImage: IconCompat? = null

    private var mRowLargeIcon: IconCompat? = null

    private var mCommuteIcon: IconCompat? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        val resources = carContext.resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.patio)
        mPaneImage = IconCompat.createWithBitmap(bitmap)
        mRowLargeIcon = IconCompat.createWithResource(
            carContext,
            R.drawable.ic_fastfood_white_48dp
        )
        mCommuteIcon = IconCompat.createWithResource(carContext, R.drawable.ic_commute_24px)
    }

    private fun createRow(index: Int): Row {
        return when (index) {
            0 ->                 // Row with a large image.
                Row.Builder()
                    .setTitle(carContext.getString(R.string.first_row_title))
                    .addText(carContext.getString(R.string.first_row_text))
                    .addText(carContext.getString(R.string.first_row_text))
                    .setImage(CarIcon.Builder(mRowLargeIcon!!).build())
                    .build()

            else -> Row.Builder()
                .setTitle(
                    carContext.getString(R.string.other_row_title_prefix) + (index
                            + 1)
                )
                .addText(carContext.getString(R.string.other_row_text))
                .addText(carContext.getString(R.string.other_row_text))
                .build()
        }
    }

    override fun onGetTemplate(): Template {
        var listLimit = 4

        // Adjust the item limit according to the car constrains.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            listLimit =
                carContext.getCarService(ConstraintManager::class.java).getContentLimit(
                    ConstraintManager.CONTENT_LIMIT_TYPE_PANE
                )
        }

        val paneBuilder = Pane.Builder()
        for (i in 0 until listLimit) {
            paneBuilder.addRow(createRow(i))
        }

        // Also set a large image outside of the rows.
        paneBuilder.setImage(CarIcon.Builder(mPaneImage!!).build())

        val primaryActionBuilder = Action.Builder()
            .setTitle(carContext.getString(R.string.search_action_title))
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.search_toast_msg),
                    CarToast.LENGTH_SHORT
                )
                    .show()
            }
        if (carContext.carAppApiLevel >= CarAppApiLevels.LEVEL_4) {
            primaryActionBuilder.setFlags(Action.FLAG_PRIMARY)
        }

        paneBuilder
            .addAction(primaryActionBuilder.build())
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.options_action_title))
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.options_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .build())

        return PaneTemplate.Builder(paneBuilder.build())
            .setHeaderAction(Action.BACK)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(
                                carContext.getString(
                                    R.string.commute_action_title
                                )
                            )
                            .setIcon(
                                CarIcon.Builder(mCommuteIcon!!)
                                    .setTint(CarColor.BLUE)
                                    .build()
                            )
                            .setOnClickListener {
                                CarToast.makeText(
                                    carContext,
                                    carContext.getString(
                                        R.string.commute_toast_msg
                                    ),
                                    CarToast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            .build())
                    .build())
            .setTitle(carContext.getString(R.string.pane_template_demo_title))
            .build()
    }
}
