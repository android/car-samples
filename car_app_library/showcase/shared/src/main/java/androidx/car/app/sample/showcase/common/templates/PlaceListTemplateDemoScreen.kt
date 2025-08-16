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
package androidx.car.app.sample.showcase.common.templates

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.common.SamplePlaces

/** Creates a screen using the [PlaceListMapTemplate]  */
class PlaceListTemplateDemoScreen(carContext: CarContext) : Screen(carContext) {
    private val mPlaces = SamplePlaces.create(this)

    override fun onGetTemplate(): Template {
        return PlaceListMapTemplate.Builder()
            .setItemList(mPlaces.placeList)
            .setTitle(carContext.getString(R.string.place_list_template_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
