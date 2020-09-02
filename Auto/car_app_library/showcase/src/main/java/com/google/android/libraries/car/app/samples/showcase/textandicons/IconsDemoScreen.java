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
import static com.google.android.libraries.car.app.model.CarColor.GREEN;

import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;

/** Creates a screen that demonstrate the usage of icons in the library. */
public final class IconsDemoScreen extends Screen {
  public IconsDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setImage(CarIcon.APP_ICON.newBuilder().build())
            .setTitle("The app icon")
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.of(
                    IconCompat.createWithResource(
                        getCarContext(), R.drawable.ic_fastfood_white_48dp)))
            .setTitle("A vector drawable, without a tint")
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.builder(
                        IconCompat.createWithResource(
                            getCarContext(), R.drawable.ic_fastfood_white_48dp))
                    .setTint(GREEN)
                    .build())
            .setTitle("A vector drawable, with a tint")
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.builder(
                        IconCompat.createWithResource(
                            getCarContext(), R.drawable.ic_themed_icon_48dp))
                    .build())
            .setTitle("A vector drawable, with an app's theme attribute for its color")
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.builder(IconCompat.createWithResource(getCarContext(), R.drawable.banana))
                    .build())
            .setTitle("A PNG, sent as a resource")
            .build());

    listBuilder.addItem(
        Row.builder()
            .setImage(
                CarIcon.builder(
                        IconCompat.createWithBitmap(
                            BitmapFactory.decodeResource(
                                getCarContext().getResources(), R.drawable.banana)))
                    .build())
            .setTitle("A PNG, sent as a bitmap")
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Icons Demo")
        .setHeaderAction(BACK)
        .build();
  }
}
