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

import android.graphics.BitmapFactory
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
import androidx.core.graphics.drawable.IconCompat

/** Creates a screen that demonstrate the usage of icons in the library.  */
class IconsDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setImage(CarIcon.Builder(CarIcon.APP_ICON).build())
                .setTitle(carContext.getString(R.string.app_icon_title))
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
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
                .setTitle(carContext.getString(R.string.vector_no_tint_title))
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
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
                .setTitle(carContext.getString(R.string.vector_with_tint_title))
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_themed_icon_48dp
                        )
                    )
                        .build()
                )
                .setTitle(
                    carContext
                        .getString(R.string.vector_with_app_theme_attr_title)
                )
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext, R.drawable.banana
                        )
                    )
                        .build()
                )
                .setTitle(carContext.getString(R.string.png_res_title))
                .build()
        )

        listBuilder.addItem(
            Row.Builder()
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithBitmap(
                            BitmapFactory.decodeResource(
                                carContext.resources,
                                R.drawable.banana
                            )
                        )
                    )
                        .build()
                )
                .setTitle(carContext.getString(R.string.png_bitmap_title))
                .build()
        )

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.icons_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
