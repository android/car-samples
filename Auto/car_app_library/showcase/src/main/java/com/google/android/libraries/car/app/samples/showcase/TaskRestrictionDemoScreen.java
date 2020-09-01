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

import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.model.Toggle;

/** Screen for demonstrating task flow limitations. */
public final class TaskRestrictionDemoScreen extends Screen {

  private static final int MAX_STEPS_ALLOWED = 4;

  private final int step;
  private boolean isBackOperation = false;
  private boolean toggleState = false;
  private int imageSize = Row.IMAGE_TYPE_SMALL;

  public TaskRestrictionDemoScreen(int step, CarContext carContext) {
    super(carContext);

    this.step = step;
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder builder = ItemList.builder();

    if (step == MAX_STEPS_ALLOWED) {
      builder
          .addItem(
              Row.builder()
                  .setTitle("Task limit reached")
                  .addText("Going forward will force stop the app")
                  .build())
          .addItem(
              Row.builder()
                  .setTitle("Try anyway")
                  .setOnClickListener(
                      () ->
                          getScreenManager()
                              .pushForResult(
                                  new TaskRestrictionDemoScreen(step + 1, getCarContext()),
                                  result -> isBackOperation = true))
                  .build());
    } else {
      builder
          .addItem(
              Row.builder()
                  .setTitle("Task step " + step + " of " + MAX_STEPS_ALLOWED)
                  .addText("Click to go forward")
                  .setOnClickListener(
                      () ->
                          getScreenManager()
                              .pushForResult(
                                  new TaskRestrictionDemoScreen(step + 1, getCarContext()),
                                  result -> isBackOperation = true))
                  .build())
          .addItem(
              Row.builder()
                  .setTitle("Toggle test")
                  .addText("Stateful changes are allowed")
                  .setToggle(
                      Toggle.builder(
                              checked -> {
                                toggleState = !toggleState;
                                invalidate();
                              })
                          .setChecked(toggleState)
                          .build())
                  .build())
          .addItem(
              Row.builder()
                  .setTitle("Image test")
                  .addText("Image changes are allowed")
                  .setImage(
                      CarIcon.of(
                          IconCompat.createWithResource(
                              getCarContext(), R.drawable.ic_fastfood_white_48dp)),
                      imageSize)
                  .setOnClickListener(
                      () -> {
                        imageSize =
                            imageSize == Row.IMAGE_TYPE_SMALL
                                ? Row.IMAGE_TYPE_LARGE
                                : Row.IMAGE_TYPE_SMALL;
                        invalidate();
                      })
                  .build());
    }

    if (isBackOperation) {
      builder.addItem(
          Row.builder()
              .setTitle("Additional Data")
              .addText("Updates allows on back operations.")
              .build());
    }

    return ListTemplate.builder()
        .setSingleList(builder.build())
        .setTitle("Task Restriction Demo")
        .setHeaderAction(BACK)
        .setActionStrip(
            ActionStrip.builder()
                .addAction(
                    Action.builder()
                        .setTitle("HOME")
                        .setOnClickListener(() -> getScreenManager().popTo(ROOT))
                        .build())
                .build())
        .build();
  }

  boolean isToggleChecked() {
    return toggleState;
  }
}
