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
package androidx.car.app.sample.showcase.common.misc

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
import androidx.car.app.sample.showcase.common.common.Utils.colorize
import androidx.core.graphics.drawable.IconCompat

/** Creates a screen that demonstrate the usage of colored texts and icons in the library.  */
class ColorDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 1))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_1_text),
                        CarColor.RED, 16, 3
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.RED)
                        .build()
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 2))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_2_text),
                        CarColor.GREEN, 16, 5
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.GREEN)
                        .build()
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 3))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_3_text),
                        CarColor.BLUE, 16, 4
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.BLUE)
                        .build()
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 4))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_4_text),
                        CarColor.YELLOW, 16, 6
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.YELLOW)
                        .build()
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 5))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_5_text),
                        CarColor.PRIMARY, 19, 7
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.PRIMARY)
                        .build()
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.example_title, 6))
                .addText(
                    colorize(
                        carContext.getString(R.string.example_6_text),
                        CarColor.SECONDARY, 19, 9
                    )
                )
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_fastfood_white_48dp
                        )
                    )
                        .setTint(CarColor.SECONDARY)
                        .build()
                )
                .build()
        )

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.color_demo))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
