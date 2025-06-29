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
package androidx.car.app.sample.places.common

import android.location.Location
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.ItemList
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** A screen that displays a list of place categories.  */
class PlaceCategoryListScreen private constructor(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    private var mAnchorLocation: Location? = null

    private var mSearchLocation: Location = Constants.INITIAL_SEARCH_LOCATION

    override fun onCreate(owner: LifecycleOwner) {
        setSearchLocation(Constants.INITIAL_SEARCH_LOCATION)
    }

    override fun onGetTemplate(): Template {
        // Build a list of rows for each category.
        val listBuilder = ItemList.Builder()
        for (category in Constants.CATEGORIES) {
            val screen: PlaceListScreen = PlaceListScreen.Companion.create(
                carContext,
                mSearchLocation,
                Constants.POI_SEARCH_RADIUS_METERS,
                Constants.POI_SEARCH_MAX_RESULTS,
                category,
                mAnchorLocation
            )

            listBuilder.addItem(
                Row.Builder()
                    .setTitle(category.displayName) // Clicking on the row pushes a screen that shows the list of places of
                    // that
                    // category around the center location.
                    .setOnClickListener {
                        screenManager
                            .push(screen)
                    }
                    .setBrowsable(true)
                    .build())
        }

        // If we have an anchor explicitly set, display it in the map. Otherwise, use the current
        // search location.
        val anchorBuilder = if (mAnchorLocation != null) {
            Place.Builder(CarLocation.create(mAnchorLocation!!))
                .setMarker(PlaceMarker.Builder().setColor(CarColor.BLUE).build())
        } else {
            Place.Builder(CarLocation.create(mSearchLocation))
        }

        val actionStrip =
            ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle("Search")
                        .setOnClickListener {
                            screenManager
                                .pushForResult(
                                    SearchScreen(
                                        carContext
                                    )
                                ) { location: Any? -> this.setSearchLocation(location) }
                        }
                        .build())
                .build()

        return PlaceListMapTemplate.Builder()
            .setItemList(listBuilder.build())
            .setHeaderAction(Action.APP_ICON)
            .setActionStrip(actionStrip)
            .setTitle("AndroidX Categories")
            .setCurrentLocationEnabled(true)
            .setAnchor(anchorBuilder.build())
            .build()
    }

    private fun setSearchLocation(location: Any?) {
        if (location != null) {
            mAnchorLocation = location as Location?
            mSearchLocation = mAnchorLocation!!
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    companion object {
        fun create(carContext: CarContext): PlaceCategoryListScreen {
            return PlaceCategoryListScreen(carContext)
        }
    }
}
