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

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/** Creates a screen that shows different types of texts and icons.  */
class TextAndIconsDemosScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.icons_demo_title))
                .setOnClickListener { screenManager.push(IconsDemoScreen(carContext)) }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(
                    carContext.getString(
                        R.string.content_provider_icons_demo_title
                    )
                )
                .setOnClickListener {
                    screenManager
                        .push(
                            ContentProviderIconsDemoScreen(
                                carContext
                            )
                        )
                }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.row_text_icons_demo_title))
                .setOnClickListener { screenManager.push(RowDemoScreen(carContext)) }
                .build())

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.text_icons_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
