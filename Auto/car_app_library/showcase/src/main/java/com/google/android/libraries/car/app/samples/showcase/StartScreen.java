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

package com.google.android.libraries.car.app.samples.showcase;

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
import com.google.android.libraries.car.app.samples.showcase.misc.MiscDemoScreen;
import com.google.android.libraries.car.app.samples.showcase.navigation.NavigationDemosScreen;
import com.google.android.libraries.car.app.samples.showcase.templates.MiscTemplateDemosScreen;
import com.google.android.libraries.car.app.samples.showcase.textandicons.TextAndIconsDemosScreen;

/** The starting screen of the app. */
public final class StartScreen extends Screen {
  public StartScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Selectable Lists Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new SelectableListsDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Task Restriction Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new TaskRestrictionDemoScreen(1, getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.of(
                    IconCompat.createWithResource(getCarContext(), R.drawable.ic_map_white_48dp)))
            .setTitle("Navigation Demos")
            .setOnClickListener(
                () -> {
                  getScreenManager().push(new NavigationDemosScreen(getCarContext()));
                })
            .setIsBrowsable(true)
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Misc Templates Demos")
            .setOnClickListener(
                () -> getScreenManager().push(new MiscTemplateDemosScreen(getCarContext())))
            .setIsBrowsable(true)
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Text and Icons Demos")
            .setOnClickListener(
                () -> getScreenManager().push(new TextAndIconsDemosScreen(getCarContext())))
            .setIsBrowsable(true)
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Misc Demos")
            .setOnClickListener(() -> getScreenManager().push(new MiscDemoScreen(getCarContext())))
            .setIsBrowsable(true)
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Showcase Demos")
        .setHeaderAction(Action.APP_ICON)
        .build();
  }
}
