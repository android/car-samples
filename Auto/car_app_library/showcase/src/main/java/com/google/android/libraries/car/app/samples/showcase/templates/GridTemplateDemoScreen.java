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
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.GridItem;
import com.google.android.libraries.car.app.model.GridTemplate;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;

/** Creates a screen that demonstrates usage of the full screen {@link GridTemplate}. */
public final class GridTemplateDemoScreen extends Screen implements DefaultLifecycleObserver {
  @Nullable private IconCompat mImage;
  @Nullable private IconCompat mIcon;
  private boolean thirdItemToggleState;
  private boolean fourthItemToggleState;
  private boolean fifthItemToggleState;

  public GridTemplateDemoScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
    thirdItemToggleState = false;
    fourthItemToggleState = true;
    fifthItemToggleState = false;
  }

  @Override
  public void onCreate(@NonNull LifecycleOwner owner) {
    Resources resources = getCarContext().getResources();
    Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_image_square);
    mImage = IconCompat.createWithBitmap(bitmap);
    mIcon = IconCompat.createWithResource(getCarContext(), R.drawable.ic_fastfood_white_48dp);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder gridItemlistBuilder = ItemList.builder();

    // Grid item with an icon and a title.
    gridItemlistBuilder.addItem(
        GridItem.builder().setImage(CarIcon.of(mIcon)).setTitle("Non-actionable").build());

    // Grid item with a large icon, a title, onClickListener and no text.
    gridItemlistBuilder.addItem(
        GridItem.builder()
            .setImage(CarIcon.of(mIcon), GridItem.IMAGE_TYPE_LARGE)
            .setTitle("Second Item")
            .setOnClickListener(
                () -> CarToast.makeText(getCarContext(), "Clicked second item", LENGTH_LONG).show())
            .build());

    // Grid item with an icon marked as icon, a title, a text and a toggle in unchecked state.
    gridItemlistBuilder.addItem(
        GridItem.builder()
            .setImage(CarIcon.of(mIcon), GridItem.IMAGE_TYPE_ICON)
            .setTitle("Third Item")
            .setText(thirdItemToggleState ? "Checked" : "Unchecked")
            .setOnClickListener(
                () -> {
                  thirdItemToggleState = !thirdItemToggleState;
                  CarToast.makeText(
                          getCarContext(),
                          "Third item checked: " + thirdItemToggleState,
                          LENGTH_LONG)
                      .show();
                  invalidate();
                })
            .build());

    // Grid item with an image, a title, a long text and a toggle in checked state.
    gridItemlistBuilder.addItem(
        GridItem.builder()
            .setImage(CarIcon.of(mImage))
            .setTitle("Fourth")
            .setText(fourthItemToggleState ? "On" : "Off")
            .setOnClickListener(
                () -> {
                  fourthItemToggleState = !fourthItemToggleState;
                  CarToast.makeText(
                          getCarContext(),
                          "Fourth item checked: " + fourthItemToggleState,
                          LENGTH_LONG)
                      .show();
                  invalidate();
                })
            .build());

    // Grid item with a large image, a long title, no text and a toggle in unchecked state.
    gridItemlistBuilder.addItem(
        GridItem.builder()
            .setImage(CarIcon.of(mImage), GridItem.IMAGE_TYPE_LARGE)
            .setTitle("Fifth Item has a long title set")
            .setOnClickListener(
                () -> {
                  fifthItemToggleState = !fifthItemToggleState;
                  CarToast.makeText(
                          getCarContext(),
                          "Fifth item checked: " + fifthItemToggleState,
                          LENGTH_LONG)
                      .show();
                  invalidate();
                })
            .build());

    // Grid item with an image marked as an icon, a long title, a long text and onClickListener.
    gridItemlistBuilder.addItem(
        GridItem.builder()
            .setImage(CarIcon.of(mImage), GridItem.IMAGE_TYPE_ICON)
            .setTitle("Sixth Item has a long title set")
            .setText("Sixth Item has a long text set")
            .setOnClickListener(
                () -> CarToast.makeText(getCarContext(), "Clicked sixth item", LENGTH_LONG).show())
            .build());

    return GridTemplate.builder()
        .setHeaderAction(Action.APP_ICON)
        .setSingleList(gridItemlistBuilder.build())
        .setTitle("Grid Template Demo")
        .setActionStrip(
            ActionStrip.builder()
                .addAction(
                    Action.builder()
                        .setTitle("Settings")
                        .setOnClickListener(
                            () ->
                                CarToast.makeText(getCarContext(), "Clicked Settings", LENGTH_LONG)
                                    .show())
                        .build())
                .build())
        .setHeaderAction(BACK)
        .build();
  }
}
