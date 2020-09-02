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

import static com.google.android.libraries.car.app.CarToast.LENGTH_LONG;
import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.ParkedOnlyOnClickListener;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/**
 * Creates a screen that demonstrates usage of the full screen {@link ListTemplate} to display a
 * full-screen list.
 */
public final class ListTemplateDemoScreen extends Screen implements DefaultLifecycleObserver {
  public ListTemplateDemoScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(
        Row.builder()
            .setOnClickListener(ParkedOnlyOnClickListener.create(() -> onClick("Parked action")))
            .setTitle("Parked Only Title")
            .addText("More Parked only text.")
            .build());

    for (int i = 2; i <= 6; ++i) {
      final String onClickText = "Clicked row: " + i;
      listBuilder.addItem(
          Row.builder()
              .setOnClickListener(() -> onClick(onClickText))
              .setTitle("Title " + i)
              .addText("First line of text")
              .addText("Second line of text")
              .build());
    }

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("List Template Demo")
        .setHeaderAction(BACK)
        .setActionStrip(
            ActionStrip.builder()
                .addAction(
                    Action.builder().setTitle("Settings").setOnClickListener(() -> {}).build())
                .build())
        .build();
  }

  private void onClick(String text) {
    CarToast.makeText(getCarContext(), text, LENGTH_LONG).show();
  }
}
