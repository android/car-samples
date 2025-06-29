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
import androidx.car.app.sample.places.common.places.PlaceCategory

/** App-wide constants  */
internal object Constants {
    /** The initial location to use as an anchor for searches.  */
    val INITIAL_SEARCH_LOCATION: Location = Location("PlacesDemo")

    /** The radius around the current anchor location to to search for POIs.  */
    const val POI_SEARCH_RADIUS_METERS: Int = 2000 // 2 km ~ 1.2 miles.

    /** The maximum number of location search results when searching for POIs.  */
    const val POI_SEARCH_MAX_RESULTS: Int = 12

    /** The radius around the current anchor location to search for other anchor locations.  */
    const val LOCATION_SEARCH_RADIUS_METERS: Int = 100000 // 100 km ~ 62 miles.

    /** The maximum number of location search results when searching for anchors.  */
    const val LOCATION_SEARCH_MAX_RESULTS: Int = 5

    val CATEGORIES: Array<PlaceCategory> = arrayOf<PlaceCategory>(
        PlaceCategory.Companion.create("Banks", "bank"),
        PlaceCategory.Companion.create("Bars", "bar"),
        PlaceCategory.Companion.create("Parking", "parking"),
        PlaceCategory.Companion.create("Restaurants", "restaurant"),
        PlaceCategory.Companion.create("Gas stations", "gas_station"),
        PlaceCategory.Companion.create("Transit stations", "transit_station")
    )

    init {
        // Googleplex
        INITIAL_SEARCH_LOCATION.latitude = 37.422255
        INITIAL_SEARCH_LOCATION.longitude = -122.084047
    }
}
