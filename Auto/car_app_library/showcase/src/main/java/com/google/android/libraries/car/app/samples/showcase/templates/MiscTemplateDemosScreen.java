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

package com.google.android.libraries.car.app.samples.showcase.templates;

import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** An assortment of demos for different templates. */
public final class MiscTemplateDemosScreen extends Screen {
  public MiscTemplateDemosScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Pane Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new PaneTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("List Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new ListTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Place List Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new PlaceListTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Search Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new SearchTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Message Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new MessageTemplateDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Grid Template Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new GridTemplateDemoScreen(getCarContext())))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Misc Templates Demos")
        .setHeaderAction(BACK)
        .build();
  }
}
