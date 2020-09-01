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
import static com.google.android.libraries.car.app.model.CarColor.BLUE;
import static com.google.android.libraries.car.app.model.CarColor.GREEN;
import static com.google.android.libraries.car.app.model.CarColor.PRIMARY;
import static com.google.android.libraries.car.app.model.CarColor.RED;
import static com.google.android.libraries.car.app.model.CarColor.SECONDARY;
import static com.google.android.libraries.car.app.model.CarColor.YELLOW;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.common.Utils;

/** Creates a screen that demonstrate the usage of colored text in the library. */
public final class ColoredTextDemoScreen extends Screen {
  public ColoredTextDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 1")
            .addText(Utils.colorize("This text has a red color", RED, 16, 3))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 2")
            .addText(Utils.colorize("This text has a green color", GREEN, 16, 5))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 3")
            .addText(Utils.colorize("This text has a blue color", BLUE, 16, 4))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 4")
            .addText(Utils.colorize("This text has a yellow color", YELLOW, 16, 6))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 5")
            .addText(Utils.colorize("This text uses the primary color", PRIMARY, 19, 7))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Example 6")
            .addText(Utils.colorize("This text uses the secondary color", SECONDARY, 19, 9))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Colored Text Demo")
        .setHeaderAction(BACK)
        .build();
  }
}
