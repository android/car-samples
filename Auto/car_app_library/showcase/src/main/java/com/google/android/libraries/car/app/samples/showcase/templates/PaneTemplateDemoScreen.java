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

import static com.google.android.libraries.car.app.CarToast.LENGTH_SHORT;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.Pane;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;
import java.util.Arrays;

/**
 * Creates a screen that demonstrates usage of the full screen {@link PaneTemplate} to display a
 * details screen.
 */
public final class PaneTemplateDemoScreen extends Screen implements DefaultLifecycleObserver {
  @Nullable private IconCompat mImage;

  public PaneTemplateDemoScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
  }

  @Override
  public void onCreate(@NonNull LifecycleOwner owner) {
    Resources resources = getCarContext().getResources();
    Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_image_square);
    mImage = IconCompat.createWithBitmap(bitmap);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    Pane.Builder paneBuilder = Pane.builder();

    // Add a non-clickable rows.
    paneBuilder.addRow(
        Row.builder().setTitle("Row title").addText("Row text 1").addText("Row text 2").build());

    // Add a row with a large image.
    paneBuilder.addRow(
        Row.builder()
            .setTitle("Row with a large image")
            .addText("Text text text")
            .setImage(CarIcon.of(mImage), Row.IMAGE_TYPE_LARGE)
            .build());

    paneBuilder.setActions(
        Arrays.asList(
            Action.builder()
                .setTitle("Search")
                .setBackgroundColor(CarColor.BLUE)
                .setOnClickListener(
                    () -> {
                      CarToast.makeText(getCarContext(), "Search button pressed", LENGTH_SHORT)
                          .show();
                    })
                .build(),
            Action.builder()
                .setTitle("Options")
                .setBackgroundColor(CarColor.YELLOW)
                .setOnClickListener(
                    () -> {
                      CarToast.makeText(getCarContext(), "Options button pressed", LENGTH_SHORT)
                          .show();
                    })
                .build()));

    return PaneTemplate.builder(paneBuilder.build())
        .setHeaderAction(Action.BACK)
        .setActionStrip(
            ActionStrip.builder()
                .addAction(
                    Action.builder()
                        .setTitle("Settings")
                        .setOnClickListener(
                            () -> {
                              CarToast.makeText(
                                      getCarContext(), "Settings button pressed", LENGTH_SHORT)
                                  .show();
                            })
                        .build())
                .build())
        .setTitle("Pane Template Demo")
        .build();
  }
}
