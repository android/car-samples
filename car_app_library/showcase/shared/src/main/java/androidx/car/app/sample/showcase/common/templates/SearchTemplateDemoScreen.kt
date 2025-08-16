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
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.SearchTemplate.SearchCallback
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/** A screen that demonstrates the search template.  */
class SearchTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        for (i in 1..6) {
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(carContext.getString(R.string.title_prefix) + " " + i)
                    .addText(carContext.getString(R.string.first_line_text))
                    .addText(carContext.getString(R.string.second_line_text))
                    .build()
            )
        }

        val searchListener: SearchCallback =
            object : SearchCallback {
                override fun onSearchTextChanged(searchText: String) {
                }

                override fun onSearchSubmitted(searchText: String) {
                }
            }

        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.settings_action_title))
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
                    .build())
            .build()

        return SearchTemplate.Builder(searchListener)
            .setSearchHint(carContext.getString(R.string.search_hint))
            .setHeaderAction(Action.BACK)
            .setShowKeyboardByDefault(false)
            .setItemList(listBuilder.build())
            .setActionStrip(actionStrip)
            .build()
    }
}
