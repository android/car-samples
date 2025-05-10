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
import android.text.Spanned
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.PlaceListNavigationTemplate
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.sample.navigation.common.model.DemoScripts.getNavigateHome
import androidx.car.app.sample.navigation.common.model.PlaceInfo

/** Screen for showing a list of favorite places.  */
class FavoritesScreen(
    carContext: CarContext,
    private val mSettingsAction: Action,
    private val mSurfaceRenderer: SurfaceRenderer
) : Screen(carContext) {
    private var mFavorites: List<PlaceInfo>? = null

    override fun onGetTemplate(): Template {
        Log.i(TAG, "In FavoritesScreen.onGetTemplate()")
        mSurfaceRenderer.updateMarkerVisibility( /* showMarkers=*/
            false,  /* numMarkers=*/0,  /* activeMarker=*/-1
        )
        val listBuilder = ItemList.Builder()

        for (place in favorites) {
            val address = SpannableString("   \u00b7 " + place.displayAddress)
            val distanceSpan =
                DistanceSpan.create(
                    Distance.create( /* displayDistance= */1.0, Distance.UNIT_KILOMETERS_P1)
                )
            address.setSpan(distanceSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(place.name)
                    .addText(address)
                    .setOnClickListener { onClickFavorite() }
                    .setMetadata(
                        Metadata.Builder()
                            .setPlace(
                                Place.Builder(CarLocation.create(1.0, 1.0))
                                    .build()
                            )
                            .build()
                    )
                    .build())
        }

        val header = Header.Builder()
            .setStartHeaderAction(Action.BACK)
            .setTitle(carContext.getString(R.string.app_name))
            .build()

        return PlaceListNavigationTemplate.Builder()
            .setItemList(listBuilder.build())
            .setActionStrip(ActionStrip.Builder().addAction(mSettingsAction).build())
            .setHeader(header)
            .build()
    }

    private fun onClickFavorite() {
        screenManager
            .pushForResult(
                RoutePreviewScreen(carContext, mSettingsAction, mSurfaceRenderer)
            ) { previewResult: Any? -> this.onRoutePreviewResult(previewResult) }
    }

    private fun onRoutePreviewResult(previewResult: Any?) {
        val previewIndex = if (previewResult == null) -1 else previewResult as Int
        if (previewIndex < 0) {
            return
        }
        // Start the same demo instructions. More will be added in the future.
        setResult(getNavigateHome(carContext))
        finish()
    }

    private val favorites: List<PlaceInfo>
        get() {
            // Lazy initialize mFavorites.
            if (mFavorites != null) {
                return mFavorites!!
            }
            val favorites = ArrayList<PlaceInfo>()
            val home =
                PlaceInfo(
                    carContext.getString(R.string.home_destination_label),
                    "9 10th Street."
                )
            favorites.add(home)
            val work =
                PlaceInfo(
                    carContext.getString(R.string.work_destination_label),
                    "2 3rd Street."
                )
            favorites.add(work)
            mFavorites = favorites
            return mFavorites!!
        }

    companion object {
        private const val TAG = "NavigationDemo"
    }
}
