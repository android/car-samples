/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.google.android.libraries.car.app.samples.showcase.navigation;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.PlaceListNavigationTemplate;
import com.google.android.libraries.car.app.samples.showcase.common.SamplePlaces;

/** Creates a screen using the {@link PlaceListNavigationTemplate} */
public final class PlaceListNavigationTemplateDemoScreen extends Screen {
  private SamplePlaces mPlaces;

  public PlaceListNavigationTemplateDemoScreen(CarContext carContext) {
    super(carContext);
    mPlaces = SamplePlaces.create(this);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return PlaceListNavigationTemplate.builder()
        .setItemList(mPlaces.getPlaceList())
        .setTitle("Place List Navigation Template Demo")
        .setHeaderAction(Action.BACK)
        .setActionStrip(
            ActionStrip.builder()
                .addAction(Action.builder().setTitle("Search").setOnClickListener(() -> {}).build())
                .build())
        .build();
  }
}
