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
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** A screen showing a demos for the routing template in different states. */
public final class RoutingTemplateDemoScreen extends Screen {

  public RoutingTemplateDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Re-routing Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new ReroutingDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Navigating Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new NavigatingDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Arrived Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new ArrivedDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Junction Image Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new JunctionImageDemoScreen(getCarContext())))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Routing Template Demos")
        .setHeaderAction(Action.BACK)
        .build();
  }
}
