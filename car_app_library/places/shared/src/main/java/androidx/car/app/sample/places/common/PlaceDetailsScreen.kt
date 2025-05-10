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

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.HostException
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ForegroundCarColorSpan
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.places.common.places.PlaceDetails
import androidx.car.app.sample.places.common.places.PlaceFinder
import androidx.car.app.sample.places.common.places.PlaceInfo
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.Locale

/** A screen that displays a the details for a given place.  */
class PlaceDetailsScreen private constructor(
    carContext: CarContext,
    private val mPlace: PlaceInfo
) : Screen(carContext), DefaultLifecycleObserver {
    // Loaded asynchronously from the network.
    private var mPhoto: Bitmap? = null

    private var mGeocoder: Geocoder? = null
    private var mDetails: PlaceDetails? = null
    private var mPlaceFinder: PlaceFinder? = null

    override fun onCreate(owner: LifecycleOwner) {
        mGeocoder = Geocoder(carContext)
        mPlaceFinder =
            PlaceFinder(carContext.resources.getString(R.string.PLACES_API_KEY))
    }

    override fun onStart(owner: LifecycleOwner) {
        update(carContext)
    }

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()

        // If we don't have any places yet, show a loading progress indicator.
        if (mDetails == null) {
            paneBuilder.setLoading(true)
        } else {
            val row1Builder = Row.Builder().setTitle("Address")

            // Add the address, split in multiple lines.
            val addressLines = getAddressLines(
                mPlace.getAddress(
                    mGeocoder!!
                )
            )
            for (line in addressLines) {
                row1Builder.addText(line)
            }

            if (mPhoto != null) {
                row1Builder.setImage(
                    CarIcon.Builder(IconCompat.createWithBitmap(mPhoto!!)).build(),
                    Row.IMAGE_TYPE_LARGE
                )
            }

            paneBuilder.addRow(row1Builder.build())

            var hasSecondRow = false
            val row2Builder = Row.Builder().setTitle("Phone Number and Rating")

            // Add the phone number.
            val phoneNumber = mDetails?.phoneNumber
            if (phoneNumber != null) {
                hasSecondRow = true
                row2Builder.addText(phoneNumber)
            }

            // Add the place's ratings.
            val ratings = mDetails!!.ratings
            if (ratings >= 0) {
                hasSecondRow = true
                row2Builder.addText(getRatingsString(ratings))
            }

            if (hasSecondRow) {
                paneBuilder.addRow(row2Builder.build())
            }

            // Add a button with a navigate action.
            paneBuilder.addAction(
                Action.Builder()
                    .setTitle("Navigate")
                    .setOnClickListener { this.onClickNavigate() }
                    .build())
        }

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle(mPlace.name)
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun onClickNavigate() {
        val uri = Uri.parse("geo:0,0?q=" + mPlace.getAddress(mGeocoder!!).getAddressLine(0))
        val intent = Intent(CarContext.ACTION_NAVIGATE, uri)

        try {
            carContext.startCarApp(intent)
        } catch (e: HostException) {
            CarToast.makeText(
                carContext,
                "Failure starting navigation",
                CarToast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun update(context: Context): ListenableFuture<Void?> {
        // Load details first, then the icon, then the photo.
        return Futures.transformAsync(
            loadDetails(),
            { details1: PlaceDetails? ->
                Futures.transformAsync(
                    loadPhoto(context, details1),
                    { details2: Void? ->
                        invalidate()
                        Futures.immediateFuture(null)
                    },
                    Executors.UI_EXECUTOR
                )
            },
            Executors.UI_EXECUTOR
        )
    }

    private fun loadPhoto(context: Context, details: PlaceDetails?): ListenableFuture<Void?> {
        if (details == null) {
            return Futures.immediateFuture(null)
        }

        val photos = details.photoUrls
        if (photos.isEmpty()) {
            return Futures.immediateFuture(null)
        }
        return Futures.transformAsync(
            ImageUtil.loadBitmapFromUrl(context, photos[0]!!),
            { bitmap: Bitmap? ->
                mPhoto = bitmap
                invalidate()
                Futures.immediateFuture(null)
            },
            Executors.UI_EXECUTOR
        )
    }

    private fun loadDetails(): ListenableFuture<PlaceDetails?> {
        return Futures.transformAsync( // Run the query in the background thread, and update with the results in the UI
            // thread.
            Futures.submitAsync(
                { Futures.immediateFuture(mPlaceFinder!!.getPlaceDetails(mPlace.id)) },
                Executors.BACKGROUND_EXECUTOR
            ),
            { place: PlaceDetails? ->
                mDetails = place
                Futures.immediateFuture(place)
            },
            Executors.UI_EXECUTOR
        )
    }

    init {
        lifecycle.addObserver(this)
    }

    companion object {
        private const val FULL_STAR = "\u2605"
        private const val HALF_STAR = "\u00BD"

        /** Returns a screen showing the details of the given [PlaceInfo].  */
        fun create(carContext: CarContext, place: PlaceInfo): PlaceDetailsScreen {
            return PlaceDetailsScreen(carContext, place)
        }

        private fun getAddressLines(address: Address): List<CharSequence> {
            val list: MutableList<CharSequence> = ArrayList()

            // First line: [street number address].
            var separator = " "
            var s = append(address.subThoroughfare, address.thoroughfare, separator)
            if (s != null) {
                list.add(s)
            }

            // Second line: [city, state, postal code].
            separator = ", "
            s =
                append(
                    append(address.locality, address.adminArea, separator),
                    address.postalCode,
                    separator
                )
            if (s != null) {
                list.add(s)
            }

            return list
        }

        private fun append(a: String?, b: String?, separator: String): String? {
            return if (a == null) b else if (b == null) a else a + (if (a.isEmpty()) b else separator + b)
        }

        private fun getRatingsString(ratings: Double): CharSequence {
            var s = ""
            var r = ratings
            while (r > 0) {
                s += if (r < 1) HALF_STAR else FULL_STAR
                --r
            }
            val ss =
                SpannableString(s + " ratings: " + String.format(Locale.US, "%.2f", ratings))
            if (!s.isEmpty()) {
                colorize(ss, CarColor.YELLOW, 0, s.length)
            }
            return ss
        }

        fun colorize(s: SpannableString, color: CarColor?, index: Int, length: Int) {
            s.setSpan(
                ForegroundCarColorSpan.create(color!!),
                index,
                index + length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}
