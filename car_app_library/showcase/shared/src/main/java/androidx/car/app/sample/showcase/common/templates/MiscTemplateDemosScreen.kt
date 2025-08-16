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
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import kotlin.math.min

/** An assortment of demos for different templates.  */
class MiscTemplateDemosScreen(
    carContext: CarContext,
    private val mPage: Int,
    private val mItemLimit: Int
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        val screenArray = arrayOf(
            createRow(
                carContext.getString(R.string.pane_template_demo_title),
                PaneTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.list_template_demo_title),
                ListTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.place_list_template_demo_title),
                PlaceListTemplateBrowseDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.search_template_demo_title),
                SearchTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.msg_template_demo_title),
                MessageTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.grid_template_demo_title),
                GridTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.sign_in_template_demo_title),
                SignInTemplateDemoScreen(carContext)
            ),
            createRow(
                carContext.getString(R.string.long_msg_template_demo_title),
                LongMessageTemplateDemoScreen(carContext)
            )
        )
        // If the screenArray size is under the limit, we will show all of them on the first page.
        // Otherwise we will show them in multiple pages.
        if (screenArray.size <= mItemLimit) {
            for (i in screenArray.indices) {
                listBuilder.addItem(screenArray[i])
            }
        } else {
            val currentItemStartIndex = mPage * mItemLimit
            val currentItemEndIndex = min(
                (currentItemStartIndex + mItemLimit).toDouble(),
                screenArray.size.toDouble()
            ).toInt()
            for (i in currentItemStartIndex until currentItemEndIndex) {
                listBuilder.addItem(screenArray[i])
            }
        }
        val builder = ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.misc_templates_demos_title))
            .setHeaderAction(Action.BACK)
        // If the current page does not cover the last item, we will show a More button
        if ((mPage + 1) * mItemLimit < screenArray.size && mPage + 1 < MAX_PAGES) {
            builder.setActionStrip(ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.more_action_title))
                        .setOnClickListener {
                            screenManager.push(
                                MiscTemplateDemosScreen(
                                    carContext, mPage + 1,
                                    mItemLimit
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
        private const val MAX_PAGES = 2
    }
}