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
package androidx.car.app.sample.navigation.common.car

import android.text.SpannableString
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.DurationSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.RoutePreviewNavigationTemplate
import androidx.car.app.sample.navigation.common.R
import java.util.concurrent.TimeUnit

/** The route preview screen for the app.  */
class RoutePreviewScreen(
    carContext: CarContext,
    private val mSettingsAction: Action,
    private val mSurfaceRenderer: SurfaceRenderer
) : Screen(carContext) {
    private val mRouteRows: MutableList<Row> = ArrayList()

    var mLastSelectedIndex: Int = -1

    init {
        val firstRoute = SpannableString("   \u00b7 Shortest route")
        firstRoute.setSpan(DurationSpan.create(TimeUnit.HOURS.toSeconds(26)), 0, 1, 0)
        val secondRoute = SpannableString("   \u00b7 Less busy")
        secondRoute.setSpan(DurationSpan.create(TimeUnit.HOURS.toSeconds(24)), 0, 1, 0)
        val thirdRoute = SpannableString("   \u00b7 HOV friendly")
        thirdRoute.setSpan(DurationSpan.create(TimeUnit.MINUTES.toSeconds(867)), 0, 1, 0)

        mRouteRows.add(Row.Builder().setTitle(firstRoute).addText("Via NE 8th Street").build())
        mRouteRows.add(Row.Builder().setTitle(secondRoute).addText("Via NE 1st Ave").build())
        mRouteRows.add(Row.Builder().setTitle(thirdRoute).addText("Via NE 4th Street").build())
    }

    override fun onGetTemplate(): Template {
        Log.i(TAG, "In RoutePreviewScreen.onGetTemplate()")
        onRouteSelected(0)

        val listBuilder = ItemList.Builder()
        listBuilder
            .setOnSelectedListener { index: Int -> this.onRouteSelected(index) }
            .setOnItemsVisibilityChangedListener { startIndex: Int, endIndex: Int ->
                this.onRoutesVisible(
                    startIndex,
                    endIndex
                )
            }
        for (row in mRouteRows) {
            listBuilder.addItem(row)
        }

        val header = Header.Builder()
            .setStartHeaderAction(Action.BACK)
            .setTitle(carContext.getString(R.string.route_preview))
            .build()

        return RoutePreviewNavigationTemplate.Builder()
            .setItemList(listBuilder.build())
            .setActionStrip(ActionStrip.Builder().addAction(mSettingsAction).build())
            .setHeader(header)
            .setNavigateAction(
                Action.Builder()
                    .setTitle("Continue to route")
                    .setOnClickListener { this.onNavigate() }
                    .build())
            .build()
    }

    private fun onRouteSelected(index: Int) {
        mLastSelectedIndex = index
        mSurfaceRenderer.updateMarkerVisibility( /* showMarkers=*/
            true,  /* numMarkers=*/
            mRouteRows.size,  /* activeMarker=*/
            mLastSelectedIndex
        )
    }

    private fun onRoutesVisible(startIndex: Int, endIndex: Int) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(
                TAG, "In RoutePreviewScreen.onRoutesVisible start:" + startIndex + " end:"
                        + endIndex
            )
        }
    }

    private fun onNavigate() {
        setResult(mLastSelectedIndex)
        finish()
    }

    companion object {
        private const val TAG = "NavigationDemo"
    }
}
