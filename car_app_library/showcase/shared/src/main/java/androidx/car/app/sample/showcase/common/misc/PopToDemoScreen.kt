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
package androidx.car.app.sample.showcase.common.misc

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/**
 * A [Screen] that allows you to push deeper in the screen stack, or pop to previous marker,
 * or pop to the root [Screen].
 */
class PopToDemoScreen @JvmOverloads constructor(carContext: CarContext, private val mId: Int = 0) :
    Screen(carContext) {
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.pop_to_root))
                .setOnClickListener { screenManager.popToRoot() }
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.pop_to_marker))
                .setOnClickListener { screenManager.popTo(MiscDemoScreen.Companion.MARKER) }
                .build())
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.push_stack))
                .setOnClickListener {
                    screenManager
                        .push(
                            PopToDemoScreen(
                                carContext, mId + 1
                            )
                        )
                }
                .build())

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.pop_to_prefix) + mId)
            .setHeaderAction(Action.BACK)
            .build()
    }
}
