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

import android.location.Location
import android.util.Log
import android.util.Xml
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/** Implements methods to access the Places API.  */
class PlaceFinder(private val mApiKey: String) {
    /** Queries the details for a place give its id.  */
    fun getPlaceDetails(placeId: String): PlaceDetails? {
        try {
            val jsonResult = getResult(makeDetailsURL(placeId))
            val root = throwIfError(JSONObject(jsonResult))
            val result = root.getJSONObject("result")

            return PlaceDetails(
                placeFromJson(result),
                safeGetString(result, "formatted_phone_number"),
                safeGetDouble(result, "rating"),
                safePhotosFromJson(result),
                safeGetString(result, "icon")!!
            )
        } catch (e: IOException) {
            Log.e(TAG, "Error getting place details.", e)
            return null
        } catch (e: JSONException) {
            Log.e(TAG, "Error getting place details.", e)
            return null
        }
    }

    /**
     * Queries the map server and obtains a list of places within the radius of the given location,
     * for the given category.
     *
     * @param location the location to search around of
     * @param radius   the radius around location to search for (in m)
     * @param maxCount the maximum number of places to return in the list
     */
    fun getPlacesByCategory(
        location: Location, radius: Double, maxCount: Int, category: String
    ): List<PlaceInfo> {
        return getPlacesInternal(location, radius, maxCount, category, true)
    }

    /**
     * Queries the map server and obtains a list of places within the radius of the given location,
     * that match the given name.
     *
     * @param location the location to search around of
     * @param radius   the radius around location to search for (in m)
     * @param maxCount the maximum number of places to return in the list
     */
    fun getPlacesByName(
        location: Location, radius: Double, maxCount: Int, name: String
    ): List<PlaceInfo> {
        return getPlacesInternal(location, radius, maxCount, name, false)
    }

    private fun getPlacesInternal(
        location: Location, radius: Double, maxCount: Int, searchTerm: String, isCategory: Boolean
    ): List<PlaceInfo> {
        val places: MutableList<PlaceInfo> = ArrayList()
        try {
            val url = makeSearchURL(location, radius, searchTerm, isCategory)
            Log.i(TAG, "Searching with URL: $url")
            val jsonResult = getResult(url)

            val root = throwIfError(JSONObject(jsonResult))
            val jArray = root.getJSONArray("results")

            Log.i(TAG, "Search returned " + jArray.length() + " results")
            var i = 0
            while (i < jArray.length() && i < maxCount) {
                places.add(placeFromJson(jArray.getJSONObject(i)))
                i++
            }
            return places
        } catch (e: IOException) {
            Log.e(TAG, "Error getting locations.", e)
        } catch (e: JSONException) {
            Log.e(TAG, "Error getting locations.", e)
        }

        return places
    }

    @Throws(IOException::class)
    private fun throwIfError(root: JSONObject): JSONObject {
        val error = root.optString("error_message")
        if (!error.isEmpty()) {
            throw IOException(error)
        }
        return root
    }

    /**
     * Prepares the URL to connect to the Places server from the specified location coordinates.
     *
     * @param location the location to search around of
     * @param radius   Radius in meters around the location to search through
     * @return URL The Places URL created based on the given lat/lon/radius
     */
    @Throws(MalformedURLException::class)
    private fun makeSearchURL(
        location: Location, radius: Double, searchTerm: String, isCategory: Boolean
    ): URL {
        var url =
            (SEARCH_URL
                    + "location="
                    + location.latitude
                    + ","
                    + location.longitude
                    + "&radius="
                    + radius
                    + "&sensor=true"
                    + "&key="
                    + mApiKey)

        url += if (isCategory) {
            "&type=$searchTerm"
        } else {
            "&name=$searchTerm"
        }

        return URL(url)
    }

    @Throws(MalformedURLException::class)
    private fun makePhotoURL(photoReference: String): String {
        return URL(
            PHOTO_URL
                    + "maxwidth=400"
                    + "&photoreference="
                    + photoReference
                    + "&key="
                    + mApiKey
        )
            .toString()
    }

    @Throws(MalformedURLException::class)
    private fun makeDetailsURL(placeId: String): URL {
        return URL(
            DETAILS_URL
                    + "place_id="
                    + placeId
                    + "&fields=name,rating,formatted_phone_number,geometry,place_id,geometry,"
                    + "photo,icon"
                    + "&key="
                    + mApiKey
        )
    }

    @Throws(JSONException::class)
    private fun safePhotosFromJson(json: JSONObject): List<String> {
        val photos: MutableList<String> = ArrayList()
        val jsonPhotos: JSONArray
        try {
            jsonPhotos = json.getJSONArray("photos")
        } catch (e: JSONException) {
            return ArrayList()
        }

        for (i in 0 until jsonPhotos.length()) {
            val jsonPhoto = jsonPhotos[i] as JSONObject
            val photoReference = jsonPhoto.getString("photo_reference")
            try {
                photos.add(makePhotoURL(photoReference))
            } catch (e: MalformedURLException) {
                Log.e(TAG, "Failed to make URL for photo reference: $photoReference")
            }
        }
        return photos
    }

    companion object {
        private const val TAG = "PlacesDemo"
        private const val PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place"
        private const val SEARCH_URL = PLACES_BASE_URL + "/nearbysearch/json?"
        private const val DETAILS_URL = PLACES_BASE_URL + "/details/json?"
        private const val PHOTO_URL = PLACES_BASE_URL + "/photo?"

        @Throws(IOException::class)
        private fun getResult(url: URL): String {
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.doOutput = true
            return streamToString(connection.inputStream)
        }

        @Throws(IOException::class)
        private fun streamToString(inputStream: InputStream?): String {
            val outputBuilder = StringBuilder()
            var string: String?
            if (inputStream != null) {
                val reader =
                    BufferedReader(
                        InputStreamReader(inputStream, Xml.Encoding.UTF_8.toString())
                    )
                while (null != (reader.readLine().also { string = it })) {
                    outputBuilder.append(string).append('\n')
                }
            }
            return outputBuilder.toString()
        }

        @Throws(JSONException::class)
        private fun placeFromJson(json: JSONObject): PlaceInfo {
            val jsonLocation = json.getJSONObject("geometry").getJSONObject("location")
            val placeLocation = Location(json.javaClass.toString())
            placeLocation.latitude = jsonLocation.getDouble("lat")
            placeLocation.longitude = jsonLocation.getDouble("lng")

            return PlaceInfo(json.getString("place_id"), json.getString("name"), placeLocation)
        }

        private fun safeGetString(json: JSONObject, key: String): String? {
            return try {
                json.getString(key)
            } catch (e: JSONException) {
                null
            }
        }

        private fun safeGetDouble(json: JSONObject, key: String): Double {
            return try {
                json.getDouble(key)
            } catch (e: JSONException) {
                (-1).toDouble()
            }
        }
    }
}
