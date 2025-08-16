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
package androidx.car.app.sample.places.common.places

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.io.IOException

/** Location-related utilities.  */
internal object LocationUtil {
    /** Returns the address for a given location.  */
    @Suppress("deprecation")
    @SuppressLint("Range")
    fun getAddressForLocation(geocoder: Geocoder, location: Location): Address? {
        try {
            val addresses =
                geocoder.getFromLocation(
                    location.latitude, location.longitude, 1 /* maxResults */
                )
            return if (!addresses!!.isEmpty()) addresses[0] else null
        } catch (ex: IOException) {
            return null
        }
    }
}
