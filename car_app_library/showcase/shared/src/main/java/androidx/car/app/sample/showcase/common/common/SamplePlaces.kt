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
package androidx.car.app.sample.showcase.common.common

import android.graphics.BitmapFactory
import android.location.Location
import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarIconSpan
import androidx.car.app.model.CarLocation
import androidx.car.app.model.Distance
import androidx.car.app.model.DistanceSpan
import androidx.car.app.model.ForegroundCarColorSpan
import androidx.car.app.model.ItemList
import androidx.car.app.model.Metadata
import androidx.car.app.model.Place
import androidx.car.app.model.PlaceMarker
import androidx.car.app.model.Row
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import kotlin.math.max
import kotlin.math.min

/** Provides sample place data used in the demos.  */
class SamplePlaces private constructor(private val mDemoScreen: Screen) {
    /** The location to use as an anchor for calculating distances.  */
    private val mAnchorLocation: Location

    private val mPlaces: List<PlaceInfo>

    init {
        val carContext = mDemoScreen.carContext

        mAnchorLocation = Location("ShowcaseDemo")
        mAnchorLocation.latitude = 47.6204588
        mAnchorLocation.longitude = -122.1918818

        mPlaces = getSamplePlaces(carContext)
    }

    val placeList: ItemList
        /** Return the [ItemList] of the sample places.  */
        get() {
            val listBuilder = ItemList.Builder()

            var listLimit = 6
            val carContext = mDemoScreen.carContext
            if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
                // Some hosts may allow more items in the grid than others, so put more items if
                // possible
                listLimit =
                    max(
                        listLimit.toDouble(),
                        carContext.getCarService(ConstraintManager::class.java).getContentLimit(
                            ConstraintManager.CONTENT_LIMIT_TYPE_LIST
                        ).toDouble()
                    ).toInt()
            }
            listLimit = min(listLimit.toDouble(), mPlaces.size.toDouble()).toInt()

            for (index in 0 until listLimit) {
                val place = mPlaces[index]

                // Build a description string that includes the required distance span.
                val distanceKm = getDistanceFromCurrentLocation(place.location) / 1000
                val description = SpannableString("   \u00b7 " + place.description)
                description.setSpan(
                    DistanceSpan.create(
                        Distance.create(
                            distanceKm.toDouble(),
                            Distance.UNIT_KILOMETERS
                        )
                    ),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                description.setSpan(
                    ForegroundCarColorSpan.create(CarColor.BLUE),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (index == 4) {
                    description.setSpan(
                        CarIconSpan.create(
                            CarIcon.Builder(
                                IconCompat.createWithBitmap(
                                    BitmapFactory.decodeResource(
                                        carContext.resources,
                                        R.drawable.ic_hi
                                    )
                                )
                            )
                                .build(), CarIconSpan.ALIGN_CENTER
                        ),
                        5,
                        6,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    )
                }

                val isBrowsable = index > mPlaces.size / 2

                // Add the row for this place to the list.
                listBuilder.addItem(
                    Row.Builder()
                        .setTitle(place.title)
                        .addText(description)
                        .setOnClickListener { onClickPlace(place) }
                        .setBrowsable(isBrowsable)
                        .setMetadata(
                            Metadata.Builder()
                                .setPlace(
                                    Place.Builder(
                                        CarLocation.create(
                                            place.location
                                        )
                                    )
                                        .setMarker(place.marker)
                                        .build()
                                )
                                .build()
                        )
                        .build())
            }

            return listBuilder.build()
        }

    /** Returns the distance in meters of the `location` from the current location.  */
    private fun getDistanceFromCurrentLocation(location: Location?): Int {
        return mAnchorLocation.distanceTo(location!!).toInt()
    }

    private fun onClickPlace(place: PlaceInfo) {
        mDemoScreen
            .screenManager
            .push(PlaceDetailsScreen.Companion.create(mDemoScreen.carContext, place))
    }

    companion object {
        /** Create an instance of [SamplePlaces].  */
        @JvmStatic
        fun create(demoScreen: Screen): SamplePlaces {
            return SamplePlaces(demoScreen)
        }

        /**
         * Returns the list of sample places.
         *
         *
         * We use a few Google locations around the Seattle area, using different types of markers to
         * showcase those options. The "description" field of each place describes the type of marker
         * itself.
         */
        private fun getSamplePlaces(carContext: CarContext): List<PlaceInfo> {
            val places: MutableList<PlaceInfo> = ArrayList()

            val location1 = Location(SamplePlaces::class.java.simpleName)
            location1.latitude = 47.6696482
            location1.longitude = -122.19950278
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_1_title),
                    carContext.getString(R.string.location_1_address),
                    carContext.getString(R.string.location_1_description),
                    carContext.getString(R.string.location_1_phone),
                    location1,
                    PlaceMarker.Builder()
                        .setIcon(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable.ic_commute_24px
                                )
                            )
                                .setTint(CarColor.BLUE)
                                .build(),
                            PlaceMarker.TYPE_ICON
                        )
                        .build()
                )
            )

            val location2 = Location(SamplePlaces::class.java.simpleName)
            location2.latitude = 47.6204588
            location2.longitude = -122.1918818
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_2_title),
                    carContext.getString(R.string.location_2_address),
                    carContext.getString(R.string.location_2_description),
                    carContext.getString(R.string.location_2_phone),
                    location2,
                    PlaceMarker.Builder()
                        .setIcon(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext, R.drawable.ic_520
                                )
                            )
                                .build(),
                            PlaceMarker.TYPE_IMAGE
                        )
                        .build()
                )
            )

            val location3 = Location(SamplePlaces::class.java.simpleName)
            location3.latitude = 47.625567
            location3.longitude = -122.336427
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_3_title),
                    carContext.getString(R.string.location_3_address),
                    carContext.getString(R.string.location_3_description),
                    carContext.getString(R.string.location_3_phone),
                    location3,
                    PlaceMarker.Builder().setLabel("SLU").setColor(CarColor.RED).build()
                )
            )

            val location4 = Location(SamplePlaces::class.java.simpleName)
            location4.latitude = 47.6490374
            location4.longitude = -122.3527127
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_4_title),
                    carContext.getString(R.string.location_4_address),
                    carContext.getString(R.string.location_4_description),
                    carContext.getString(R.string.location_4_phone),
                    location4,
                    PlaceMarker.Builder()
                        .setIcon(
                            CarIcon.Builder(
                                IconCompat.createWithBitmap(
                                    BitmapFactory.decodeResource(
                                        carContext.resources,
                                        R.drawable.banana
                                    )
                                )
                            )
                                .build(),
                            PlaceMarker.TYPE_IMAGE
                        )
                        .build()
                )
            )

            val location5 = Location(SamplePlaces::class.java.simpleName)
            location5.latitude = 37.422014
            location5.longitude = -122.084776
            val title5 = SpannableString("  Googleplex")
            title5.setSpan(
                CarIconSpan.create(
                    CarIcon.Builder(
                        IconCompat.createWithBitmap(
                            BitmapFactory.decodeResource(
                                carContext.resources,
                                R.drawable.ic_hi
                            )
                        )
                    )
                        .build(), CarIconSpan.ALIGN_CENTER
                ),
                0,
                1,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            val description5 = SpannableString(" ")
            places.add(
                PlaceInfo(
                    title5,
                    carContext.getString(R.string.location_5_address),
                    description5,
                    carContext.getString(R.string.location_5_phone),
                    location5,
                    PlaceMarker.Builder()
                        .setIcon(
                            CarIcon.Builder(
                                IconCompat.createWithBitmap(
                                    BitmapFactory.decodeResource(
                                        carContext.resources,
                                        R.drawable.test_image_square
                                    )
                                )
                            )
                                .build(),
                            PlaceMarker.TYPE_IMAGE
                        )
                        .build()
                )
            )

            val location6 = Location(SamplePlaces::class.java.simpleName)
            location6.latitude = 47.6490374
            location6.longitude = -122.3527127
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_6_title),
                    carContext.getString(R.string.location_6_address),
                    carContext.getString(R.string.location_description_text_label),
                    carContext.getString(R.string.location_phone_not_available),
                    location6,
                    PlaceMarker.Builder().build()
                )
            )

            // Some hosts may display more items in the list than others, so create 3 more items.
            val location7 = Location(SamplePlaces::class.java.simpleName)
            location7.latitude = 47.5496056
            location7.longitude = -122.2571713
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_7_title),
                    carContext.getString(R.string.location_7_address),
                    carContext.getString(R.string.location_description_text_label),
                    carContext.getString(R.string.location_phone_not_available),
                    location7,
                    PlaceMarker.Builder().build()
                )
            )

            val location8 = Location(SamplePlaces::class.java.simpleName)
            location8.latitude = 47.5911456
            location8.longitude = -122.2256602
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_8_title),
                    carContext.getString(R.string.location_8_address),
                    carContext.getString(R.string.location_description_text_label),
                    carContext.getString(R.string.location_phone_not_available),
                    location8,
                    PlaceMarker.Builder().build()
                )
            )

            val location9 = Location(SamplePlaces::class.java.simpleName)
            location9.latitude = 47.6785932
            location9.longitude = -122.2113821
            places.add(
                PlaceInfo(
                    carContext.getString(R.string.location_9_title),
                    carContext.getString(R.string.location_9_address),
                    carContext.getString(R.string.location_description_text_label),
                    carContext.getString(R.string.location_phone_not_available),
                    location9,
                    PlaceMarker.Builder().build()
                )
            )

            return places
        }
    }
}
