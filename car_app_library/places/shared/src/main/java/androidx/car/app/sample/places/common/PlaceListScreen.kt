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

import android.location.Geocoder
import android.location.Location
import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.places.common.places.PlaceCategory
import androidx.car.app.sample.places.common.places.PlaceFinder
import androidx.car.app.sample.places.common.places.PlaceInfo
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/** A screen that displays a list of places for a given category, around a given location.  */
class PlaceListScreen private constructor(
    carContext: CarContext,
    private val mSearchCenter: Location,
    private val mRadiusMeters: Int,
    private val mMaxSearchResults: Int,
    private val mCategory: PlaceCategory?,
    private val mAnchor: Location?
) : Screen(carContext), DefaultLifecycleObserver {
    private var mGeocoder: Geocoder? = null

    private var mPlaces: List<PlaceInfo>? = null

    private lateinit var mPlaceFinder: PlaceFinder

    override fun onCreate(owner: LifecycleOwner) {
        mGeocoder = Geocoder(carContext)
        mPlaceFinder =
            PlaceFinder(carContext.resources.getString(R.string.PLACES_API_KEY))
    }

    override fun onStart(owner: LifecycleOwner) {
        update()
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        var hasPlaces = false

        // If we don't have any places yet, show a loading progress indicator.
        if (mPlaces != null) {
            // Add one row per place in the results.
            for (i in mPlaces!!.indices) {
                if (i >= 6) {
                    // only 6 rows allowed.
                    break
                }

                val place = mPlaces!![i]
                val location = place.location
                val distanceMeters = getDistanceFromSearchCenter(location)
                val distanceKm = distanceMeters / METERS_TO_KMS

                val address =
                    SpannableString(
                        "   \u00b7 " + place.getAddress(mGeocoder!!).getAddressLine(0)
                    )
                val distanceSpan =
                    DistanceSpan.create(
                        Distance.create(
                            distanceKm.toDouble(),
                            Distance.UNIT_KILOMETERS
                        )
                    )
                address.setSpan(distanceSpan, 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                listBuilder.addItem(
                    Row.Builder() // Clicking on the place shows a toast with the full place address.
                        .setOnClickListener { onClickPlace(place) }
                        .setTitle(place.name)
                        .addText(address)
                        .setMetadata(
                            Metadata.Builder()
                                .setPlace(
                                    Place.Builder(
                                        CarLocation.create(
                                            location
                                        )
                                    )
                                        .setMarker(
                                            PlaceMarker.Builder()
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build())

                hasPlaces = true
            }
        }

        // Anchor the map around the search center if there is no place results, or if the anchor
        // location has been explicitly set.
        var anchor: Place? = null
        if (mAnchor != null) {
            anchor =
                Place.Builder(
                    CarLocation.create(
                        mAnchor.latitude, mAnchor.longitude
                    )
                )
                    .setMarker(PlaceMarker.Builder().setColor(CarColor.BLUE).build())
                    .build()
        } else if (!hasPlaces) {
            anchor =
                Place.Builder(
                    CarLocation.create(
                        mSearchCenter.latitude,
                        mSearchCenter.longitude
                    )
                )
                    .build()
        }

        val builder =
            PlaceListMapTemplate.Builder()
                .setTitle(mCategory!!.displayName)
                .setHeaderAction(Action.BACK)
                .setAnchor(anchor!!)
                .setCurrentLocationEnabled(true)
        return if (mPlaces == null) {
            builder.setLoading(true).build()
        } else {
            builder.setItemList(listBuilder.build()).build()
        }
    }

    private fun onClickPlace(place: PlaceInfo) {
        screenManager.push(
            PlaceDetailsScreen.Companion.create(
                carContext, place
            )
        )
    }

    private fun update(): ListenableFuture<Void> {
        return Futures.transformAsync( // Run the query in the background thread, and update with the results in the UI
            // thread.
            Futures.submitAsync(
                {
                    Futures.immediateFuture(
                        mPlaceFinder.getPlacesByCategory(
                            mSearchCenter,
                            mRadiusMeters.toDouble(),
                            mMaxSearchResults,
                            mCategory!!.category
                        )
                    )
                },
                Executors.BACKGROUND_EXECUTOR
            ),
            { places: List<PlaceInfo>? ->
                mPlaces = places
                invalidate()
                null
            },
            Executors.UI_EXECUTOR
        )
    }

    /** Returns the disntance in meters of the `location` from the [.mSearchCenter].  */
    private fun getDistanceFromSearchCenter(location: Location): Int {
        return mSearchCenter.distanceTo(location).toInt()
    }

    init {
        lifecycle.addObserver(this)
    }

    companion object {
        private const val METERS_TO_KMS = 1000

        /**
         * Returns a screen showing the places that result by querying around the given location and
         * radius (in meters), for the given category.
         */
        fun create(
            carContext: CarContext,
            searchCenter: Location,
            radiusMeters: Int,
            maxSearchResults: Int,
            category: PlaceCategory?,
            anchor: Location?
        ): PlaceListScreen {
            return PlaceListScreen(
                carContext, searchCenter, radiusMeters, maxSearchResults, category, anchor
            )
        }
    }
}
