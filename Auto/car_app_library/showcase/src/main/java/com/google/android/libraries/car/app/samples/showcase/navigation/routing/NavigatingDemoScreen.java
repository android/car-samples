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

package com.google.android.libraries.car.app.samples.showcase.navigation.routing;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.NavigationTemplate;
import com.google.android.libraries.car.app.navigation.model.RoutingInfo;

/** A screen that shows the navigation template in routing state. */
public final class NavigatingDemoScreen extends Screen implements DefaultLifecycleObserver {
  public NavigatingDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    CarContext carContext = getCarContext();
    return NavigationTemplate.builder()
        .setNavigationInfo(
            RoutingInfo.builder()
                .setCurrentStep(
                    RoutingDemoModels.getCurrentStep(carContext),
                    Distance.create(200, Distance.UNIT_METERS))
                .setNextStep(RoutingDemoModels.getNextStep(carContext))
                .build())
        .setDestinationTravelEstimate(RoutingDemoModels.getTravelEstimate())
        .setActionStrip(RoutingDemoModels.getActionStrip(getCarContext(), this::finish))
        .setBackgroundColor(CarColor.SECONDARY)
        .build();
  }
}
