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
package androidx.car.app.sample.showcase.common.textandicons

import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.common.Utils
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver

/** Creates a screen that shows different types of rows in a list  */
class RowDemoScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {
    init {
        lifecycle.addObserver(this)
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.just_row_title))
                .build()
        )
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.title_with_app_icon_row_title))
                .setImage(CarIcon.APP_ICON)
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(
                    carContext.getString(
                        R.string.title_with_res_id_image_row_title
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .build(),
                    Row.IMAGE_TYPE_ICON
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(
                    carContext.getString(R.string.title_with_svg_image_row_title)
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable
                                .ic_emoji_food_beverage_white_48dp
                        )
                    )
                        .build(),
                    Row.IMAGE_TYPE_ICON
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(
                    carContext.getString(
                        R.string.title_with_secondary_lines_row_title
                    )
                )
                .addText(
                    carContext.getString(
                        R.string.title_with_secondary_lines_row_text_1
                    )
                )
                .addText(
                    carContext.getString(
                        R.string.title_with_secondary_lines_row_text_2
                    )
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.colored_secondary_row_title))
                .addText(getRatingsString(3.5))
                .build()
        )

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.rows_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }

    companion object {
        private const val FULL_STAR = "\u2605"
        private const val HALF_STAR = "\u00BD"

        private fun getRatingsString(ratings: Double): CharSequence {
            var s = ""
            var r = ratings
            while (r > 0) {
                s += if (r < 1) HALF_STAR else FULL_STAR
                --r
            }
            val ss = SpannableString("$s ratings: $ratings")
            if (!s.isEmpty()) {
                Utils.colorize(ss, CarColor.YELLOW, 0, s.length)
            }
            return ss
        }
    }
}
