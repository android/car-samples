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
package androidx.car.app.sample.showcase.common.navigation

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Header
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.PlaceListNavigationTemplate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.common.SamplePlaces.Companion.create
import androidx.car.app.sample.showcase.common.navigation.routing.RoutingDemoModels
import androidx.core.graphics.drawable.IconCompat

/** Creates a screen using the [PlaceListNavigationTemplate]  */
class PlaceListNavigationTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    private val mPlaces = create(this)

    private var mIsFavorite = false

    override fun onGetTemplate(): Template {
        val header = Header.Builder()
            .setStartHeaderAction(Action.BACK)
            .addEndHeaderAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                if (mIsFavorite
                                ) R.drawable.ic_favorite_filled_white_24dp
                                else R.drawable.ic_favorite_white_24dp
                            )
                        )
                            .build()
                    )
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            if (mIsFavorite
                            ) carContext
                                .getString(R.string.favorite_toast_msg)
                            else carContext.getString(
                                R.string.not_favorite_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                        mIsFavorite = !mIsFavorite
                        invalidate()
                    }
                    .build())
            .addEndHeaderAction(
                Action.Builder()
                    .setOnClickListener { finish() }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_close_white_24dp
                            )
                        )
                            .build()
                    )
                    .build())
            .setTitle(carContext.getString(R.string.place_list_nav_template_demo_title))
            .build()

        return PlaceListNavigationTemplate.Builder()
            .setItemList(mPlaces.placeList)
            .setHeader(header)
            .setMapActionStrip(RoutingDemoModels.getMapActionStrip(carContext))
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(
                                carContext.getString(
                                    R.string.search_action_title
                                )
                            )
                            .setOnClickListener {}
                            .build())
                    .build())
            .build()
    }
}
