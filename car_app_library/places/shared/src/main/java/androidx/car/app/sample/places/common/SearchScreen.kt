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
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.Row
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.SearchTemplate.SearchCallback
import androidx.car.app.model.Template
import androidx.car.app.sample.places.common.places.PlaceFinder
import androidx.car.app.sample.places.common.places.PlaceInfo
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.Futures

/** A screen that displays a search edit text and search results.  */
class SearchScreen internal constructor(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    var mIsSearchComplete: Boolean = false

    private var mGeocoder: Geocoder? = null

    private lateinit var mPlaceFinder: PlaceFinder

    private lateinit var mSearchLocation: Location

    private var mItemList = withNoResults(ItemList.Builder()).build()

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        mPlaceFinder =
            PlaceFinder(carContext.resources.getString(R.string.PLACES_API_KEY))
        mGeocoder = Geocoder(carContext)
        mSearchLocation = Constants.INITIAL_SEARCH_LOCATION
    }

    override fun onGetTemplate(): Template {
        return SearchTemplate.Builder(
            object : SearchCallback {
                override fun onSearchTextChanged(searchText: String) {
                    doSearch(searchText)
                }

                override fun onSearchSubmitted(searchTerm: String) {
                    mIsSearchComplete = true
                    doSearch(searchTerm)
                }
            })
            .setHeaderAction(Action.BACK)
            .setShowKeyboardByDefault(false)
            .setItemList(mItemList)
            .build()
    }

    fun doSearch(searchText: String) {
        val builder = ItemList.Builder()
        if (searchText.isEmpty()) {
            mItemList = withNoResults(builder).build()
        }

        Executors.UI_EXECUTOR?.let {
            Futures.transformAsync<List<PlaceInfo?>, Any?>( // Run the query in the background thread, and update with the results in
                // the UI thread.
                Futures.submitAsync<List<PlaceInfo?>>(
                    {
                        Futures.immediateFuture<List<PlaceInfo?>>(
                            mPlaceFinder.getPlacesByName(
                                mSearchLocation,
                                Constants.LOCATION_SEARCH_RADIUS_METERS.toDouble(),
                                Constants.LOCATION_SEARCH_MAX_RESULTS,
                                searchText
                            )
                        )
                    },
                    Executors.BACKGROUND_EXECUTOR
                ),
                { places: List<PlaceInfo?> ->
                    if (mIsSearchComplete) {
                        if (places.isNotEmpty()) {
                            setResult(places[0]?.location)
                        }
                    } else {
                        if (places.isEmpty()) {
                            mItemList = withNoResults(builder).build()
                        } else {
                            for (place in places) {
                                val rowBuilder = Row.Builder()
                                place?.name?.let {
                                    rowBuilder.setTitle(it)
                                }
    //                            val addressLine =
                                place?.getAddress(mGeocoder!!)?.getAddressLine(0)?.let {
                                    rowBuilder.addText(it)
                                }
                                rowBuilder.setOnClickListener {
                                    setResult(place!!.location)
                                    finish()
                                }
                                builder.addItem(rowBuilder.build())
                            }
                        }
                        mItemList = builder.build()
                    }
                    null
                },
                it
            )
                .addListener({ this.searchCompleted() }, Executors.UI_EXECUTOR)
        }
    }

    private fun searchCompleted() {
        if (mIsSearchComplete) {
            finish()
        } else {
            invalidate()
        }
    }

    companion object {
        private const val TAG = "PlacesDemo"

        private fun withNoResults(builder: ItemList.Builder): ItemList.Builder {
            return builder.setNoItemsMessage("No Results")
        }
    }
}
