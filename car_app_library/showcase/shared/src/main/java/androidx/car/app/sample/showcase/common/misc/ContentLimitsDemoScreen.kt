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
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/**
 * A [Screen] that shows examples on how to query for various content limits via the
 * {@lnk ConstraintManager} API.
 */
class ContentLimitsDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val manager = carContext.getCarService(
            ConstraintManager::class.java
        )
        val listBuilder = ItemList.Builder()
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.list_limit))
                .addText(
                    manager.getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_LIST
                    ).toString()
                )
                .build()
        )
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.grid_limit))
                .addText(
                    manager.getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_GRID
                    ).toString()
                )
                .build()
        )
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.pane_limit))
                .addText(
                    manager.getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_PANE
                    ).toString()
                )
                .build()
        )
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.place_list_limit))
                .addText(
                    manager.getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_PLACE_LIST
                    ).toString()
                )
                .build()
        )
        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.route_list_limit))
                .addText(
                    manager.getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_ROUTE_LIST
                    ).toString()
                )
                .build()
        )


        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.content_limits))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
