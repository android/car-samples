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
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;
import com.google.android.libraries.car.app.samples.showcase.navigation.routing.RoutingTemplateDemoScreen;

/** A screen showing a list of navigation demos */
public final class NavigationDemosScreen extends Screen {
  public NavigationDemosScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.of(
                    IconCompat.createWithResource(
                        getCarContext(), R.drawable.ic_explore_white_24dp)))
            .setTitle("Routing Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new RoutingTemplateDemoScreen(getCarContext())))
            .setIsBrowsable(true)
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Place List Navigation Template Demo")
            .setOnClickListener(
                () ->
                    getScreenManager()
                        .push(new PlaceListNavigationTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Route Preview Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new RoutePreviewDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Notification Template Demo")
            .setOnClickListener(
                () ->
                    getScreenManager().push(new NavigationNotificationsDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Navigation Template with map only Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new NavigationMapOnlyScreen(getCarContext())))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Navigation Demos")
        .setHeaderAction(Action.BACK)
        .build();
  }
}
