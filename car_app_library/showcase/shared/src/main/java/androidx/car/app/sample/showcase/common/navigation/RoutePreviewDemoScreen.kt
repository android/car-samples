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
package androidx.car.app.sample.showcase.common.navigation

import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.DurationSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.navigation.routing.RoutingDemoModels
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import java.util.concurrent.TimeUnit

/** Creates a screen using the [RoutePreviewNavigationTemplate]  */
class RoutePreviewDemoScreen(carContext: CarContext) : Screen(carContext) {
    private var mIsFavorite = false

    private fun createRouteText(index: Int): CarText {
        when (index) {
            0 -> {
                // Set text variants for the first route.
                val shortRouteLongText = SpannableString(
                    "   \u00b7 ---------------- " + carContext.getString(
                        R.string.short_route
                    )
                            + " -------------------"
                )
                shortRouteLongText.setSpan(
                    DurationSpan.create(TimeUnit.HOURS.toSeconds(26)), 0, 1,
                    0
                )
                val firstRouteShortText = SpannableString(
                    "   \u00b7 " + carContext.getString(R.string.short_route)
                )
                firstRouteShortText.setSpan(
                    DurationSpan.create(TimeUnit.HOURS.toSeconds(26)), 0, 1,
                    0
                )
                return CarText.Builder(shortRouteLongText)
                    .addVariant(firstRouteShortText)
                    .build()
            }

            1 -> {
                val lessBusyRouteText =
                    SpannableString(
                        "   \u00b7 " + carContext.getString(R.string.less_busy)
                    )
                lessBusyRouteText.setSpan(
                    DurationSpan.create(TimeUnit.HOURS.toSeconds(24)), 0, 1,
                    0
                )
                return CarText.Builder(lessBusyRouteText).build()
            }

            2 -> {
                val hovRouteText =
                    SpannableString(
                        "   \u00b7 " + carContext.getString(R.string.hov_friendly)
                    )
                hovRouteText.setSpan(DurationSpan.create(TimeUnit.MINUTES.toSeconds(867)), 0, 1, 0)
                return CarText.Builder(hovRouteText).build()
            }

            else -> {
                val routeText =
                    SpannableString(
                        "   \u00b7 " + carContext.getString(R.string.long_route)
                    )
                routeText.setSpan(
                    DurationSpan.create(TimeUnit.MINUTES.toSeconds(867L + index)),
                    0, 1, 0
                )
                return CarText.Builder(routeText).build()
            }
        }
    }

    private fun createRow(index: Int): Row {
        val route = createRouteText(index)
        val titleText = "Via NE " + (index + 4) + "th Street"

        return Row.Builder()
            .setTitle(route)
            .addText(titleText)
            .build()
    }

    override fun onGetTemplate(): Template {
        var itemLimit = 3
        // Adjust the item limit according to the car constrains.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            itemLimit = carContext.getCarService(ConstraintManager::class.java).getContentLimit(
                ConstraintManager.CONTENT_LIMIT_TYPE_ROUTE_LIST
            )
        }

        val itemListBuilder = ItemList.Builder()
            .setOnSelectedListener { index: Int -> this.onRouteSelected(index) }
            .setOnItemsVisibilityChangedListener { startIndex: Int, endIndex: Int ->
                this.onRoutesVisible(
                    startIndex,
                    endIndex
                )
            }

        for (i in 0 until itemLimit) {
            itemListBuilder.addItem(createRow(i))
        }

        // Set text variants for the navigate action text.
        val navigateActionText =
            CarText.Builder(carContext.getString(R.string.continue_start_nav))
                .addVariant(carContext.getString(R.string.continue_route))
                .build()

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
                        mIsFavorite = !mIsFavorite
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
            .setTitle(carContext.getString(R.string.route_preview_template_demo_title))
            .build()

        return RoutePreviewNavigationTemplate.Builder()
            .setItemList(itemListBuilder.build())
            .setNavigateAction(
                Action.Builder()
                    .setTitle(navigateActionText)
                    .setOnClickListener { this.onNavigate() }
                    .build())
            .setMapActionStrip(RoutingDemoModels.getMapActionStrip(carContext))
            .setHeader(header)
            .build()
    }

    private fun onNavigate() {
        CarToast.makeText(
            carContext,
            carContext.getString(R.string.nav_requested_toast_msg),
            CarToast.LENGTH_LONG * 2
        ).show()
    }

    private fun onRouteSelected(index: Int) {
        CarToast.makeText(
            carContext,
            carContext.getString(R.string.selected_route_toast_msg) + ": " + index,
            CarToast.LENGTH_LONG
        ).show()
    }

    private fun onRoutesVisible(startIndex: Int, endIndex: Int) {
        CarToast.makeText(
            carContext,
            carContext.getString(R.string.visible_routes_toast_msg)
                    + ": [" + startIndex + "," + endIndex + "]",
            CarToast.LENGTH_LONG
        )
            .show()
    }
}
