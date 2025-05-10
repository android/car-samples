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

import android.graphics.BitmapFactory
import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Header
import androidx.car.app.model.Pane
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.MapController
import androidx.car.app.navigation.model.MapTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.navigation.routing.RoutingDemoModels
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat

/** Simple demo of how to present a map template with a pane.  */
class MapTemplateWithPaneDemoScreen(carContext: CarContext) : Screen(carContext) {
    private val mPaneImage: IconCompat?

    private val mRowLargeIcon: IconCompat?

    private var mIsFavorite = false

    init {
        val resources = getCarContext().resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.patio)
        mPaneImage = IconCompat.createWithBitmap(bitmap)
        mRowLargeIcon = IconCompat.createWithResource(
            getCarContext(),
            R.drawable.ic_fastfood_white_48dp
        )
    }

    override fun onGetTemplate(): Template {
        val listLimit = 4

        val paneBuilder = Pane.Builder()
        for (i in 0 until listLimit) {
            paneBuilder.addRow(createRow(i))
        }

        // Also set a large image outside of the rows.
        paneBuilder.setImage(CarIcon.Builder(mPaneImage!!).build())

        val primaryActionBuilder = Action.Builder()
            .setTitle(carContext.getString(R.string.primary_action_title))
            .setBackgroundColor(CarColor.BLUE)
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.primary_toast_msg),
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
                            ) carContext
                                .getString(R.string.favorite_toast_msg)
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
            .setTitle(carContext.getString(R.string.map_template_pane_demo_title))
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
            .setActionStrip(actionStrip)
            .setPane(paneBuilder.build())
            .setHeader(header)
            .setMapController(mapController)

        return builder.build()
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
}
