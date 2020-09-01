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

package com.google.android.libraries.car.app.samples.showcase.textandicons;

import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** Creates a screen that shows different types of texts and icons. */
public final class TextAndIconsDemosScreen extends Screen {
  public TextAndIconsDemosScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Colored Text Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new ColoredTextDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Icons Demo")
            .setOnClickListener(() -> getScreenManager().push(new IconsDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Content Provider Icons Demo")
            .setOnClickListener(
                () -> getScreenManager().push(new ContentProviderIconsDemoScreen(getCarContext())))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Rows with Text and Icons Demo")
            .setOnClickListener(() -> getScreenManager().push(new RowDemoScreen(getCarContext())))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Text and Icons Demos")
        .setHeaderAction(BACK)
        .build();
  }
}
