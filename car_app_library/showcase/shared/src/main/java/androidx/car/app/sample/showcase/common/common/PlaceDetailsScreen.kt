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

import android.content.Intent
import android.net.Uri
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.HostException
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/** A screen that displays the details of a place.  */
class PlaceDetailsScreen private constructor(
    carContext: CarContext,
    private val mPlace: PlaceInfo
) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val paneBuilder =
            Pane.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.navigate))
                        .setBackgroundColor(CarColor.BLUE)
                        .setOnClickListener { this.onClickNavigate() }
                        .build())
                .addAction(
                    Action.Builder()
                        .setTitle(carContext.getString(R.string.dial))
                        .setOnClickListener { this.onClickDial() }
                        .build())
                .addRow(
                    Row.Builder()
                        .setTitle(carContext.getString(R.string.address))
                        .addText(mPlace.address)
                        .build()
                )
                .addRow(
                    Row.Builder()
                        .setTitle(carContext.getString(R.string.phone))
                        .addText(mPlace.phoneNumber)
                        .build()
                )

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle(mPlace.title)
            .setHeaderAction(Action.BACK)
            .build()
    }

    private fun onClickNavigate() {
        val uri = Uri.parse("geo:0,0?q=" + mPlace.address)
        val intent = Intent(CarContext.ACTION_NAVIGATE, uri)

        try {
            carContext.startCarApp(intent)
        } catch (e: HostException) {
            CarToast.makeText(
                carContext,
                carContext.getString(R.string.fail_start_nav),
                CarToast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun onClickDial() {
        val uri = Uri.parse("tel:" + mPlace.phoneNumber)
        val intent = Intent(Intent.ACTION_DIAL, uri)

        try {
            carContext.startCarApp(intent)
        } catch (e: HostException) {
            CarToast.makeText(
                carContext,
                carContext.getString(R.string.fail_start_dialer),
                CarToast.LENGTH_LONG
            )
                .show()
        }
    }

    companion object {
        /** Creates an instance of [PlaceDetailsScreen].  */
        fun create(
            carContext: CarContext, place: PlaceInfo
        ): PlaceDetailsScreen {
            return PlaceDetailsScreen(carContext, place)
        }
    }
}
