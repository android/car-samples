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

import static com.google.android.libraries.car.app.CarToast.LENGTH_LONG;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** A simple screen that demonstrates how to use navigation notifications in a car app. */
public final class NavigationNotificationsDemoScreen extends Screen {

  public NavigationNotificationsDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setTitle("Start Notification")
            .setOnClickListener(
                () -> {
                  Context context = getCarContext();
                  Intent intent = new Intent(context, NavigationNotificationService.class);
                  if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    context.startForegroundService(intent);
                  } else {
                    context.startService(intent);
                  }
                })
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Stop Notification")
            .setOnClickListener(
                () ->
                    getCarContext()
                        .stopService(
                            new Intent(getCarContext(), NavigationNotificationService.class)))
            .build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Navigation Notification Demo")
        .setHeaderAction(Action.BACK)
        .build();
  }
}
