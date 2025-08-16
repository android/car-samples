/*
 * Copyright 2022 The Android Open Source Project
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
package androidx.car.app.sample.showcase.common.navigation

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.ParkedOnlyOnClickListener
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.MapController
import androidx.car.app.navigation.model.MapTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.navigation.routing.RoutingDemoModels
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import kotlin.math.min

/** Simple demo of how to present a map template with a list.  */
class MapTemplateWithListDemoScreen(carContext: CarContext) : Screen(carContext) {
    private var mIsFavorite = false

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()
        listBuilder.addItem(
            Row.Builder()
                .setOnClickListener(
                    ParkedOnlyOnClickListener.create {
                        onClick(
                            carContext.getString(R.string.parked_toast_msg)
                        )
                    })
                .setTitle(carContext.getString(R.string.parked_only_title))
                .addText(carContext.getString(R.string.parked_only_text))
                .build()
        )
        // Some hosts may allow more items in the list than others, so create more.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            val listLimit = min(
                MAX_LIST_ITEMS.toDouble(),
                carContext.getCarService(ConstraintManager::class.java)
                    .getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_LIST
                    ).toDouble()
            ).toInt()

            for (i in 2..listLimit) {
                // For row text, set text variants that fit best in different screen sizes.
                val secondTextStr = carContext.getString(R.string.second_line_text)
                val secondText =
                    CarText.Builder(
                        "================= $secondTextStr ================"
                    )
                        .addVariant(
                            "--------------------- " + secondTextStr
                                    + " ----------------------"
                        )
                        .addVariant(secondTextStr)
                        .build()
                val onClickText = (carContext.getString(R.string.clicked_row_prefix)
                        + ": " + i)
                listBuilder.addItem(
                    Row.Builder()
                        .setOnClickListener { onClick(onClickText) }
                        .setTitle(
                            carContext.getString(R.string.title_prefix) + " " + i
                        )
                        .addText(carContext.getString(R.string.first_line_text))
                        .addText(secondText)
                        .build())
            }
        }

        val header = Header.Builder()
            .setStartHeaderAction(Action.BACK)
            .addEndHeaderAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                if (mIsFavorite
                                ) R.drawable.ic_favorite_filled_white_24dp
                                else R.drawable.ic_favorite_white_24dp
                            )
                        )
                            .build()
                    )
                    .setOnClickListener {
                        mIsFavorite = !mIsFavorite
                        CarToast.makeText(
                            carContext,
                            if (mIsFavorite
                            ) carContext.getString(
                                R.string.favorite_toast_msg
                            )
                            else carContext.getString(
                                R.string.not_favorite_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                        invalidate()
                    }
                    .build())
            .addEndHeaderAction(
                Action.Builder()
                    .setOnClickListener { finish() }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_close_white_24dp
                            )
                        )
                            .build()
                    )
                    .build())
            .setTitle(carContext.getString(R.string.map_template_list_demo_title))
            .build()


        val mapController = MapController.Builder()
            .setMapActionStrip(RoutingDemoModels.getMapActionStrip(carContext))
            .build()

        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.bug_reported_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_bug_report_24px
                            )
                        )
                            .build()
                    )
                    .setFlags(Action.FLAG_IS_PERSISTENT)
                    .build())
            .build()

        val builder = MapTemplate.Builder()
            .setItemList(listBuilder.build())
            .setActionStrip(actionStrip)
            .setHeader(header)
            .setMapController(mapController)

        return builder.build()
    }

    private fun onClick(text: String) {
        CarToast.makeText(carContext, text, CarToast.LENGTH_LONG).show()
    }

    companion object {
        private const val MAX_LIST_ITEMS = 100
    }
}
