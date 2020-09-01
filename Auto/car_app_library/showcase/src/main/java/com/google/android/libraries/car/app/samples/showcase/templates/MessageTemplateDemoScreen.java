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
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.MessageTemplate;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;
import java.util.Arrays;

/** A screen that demonstrates the message template. */
public class MessageTemplateDemoScreen extends Screen {

  public MessageTemplateDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return MessageTemplate.builder("Message goes here.\nMore text on second line.")
        .setTitle("Message Template Demo")
        .setIcon(
            CarIcon.of(
                    IconCompat.createWithResource(
                        getCarContext(), R.drawable.ic_emoji_food_beverage_white_48dp))
                .newBuilder()
                .setTint(CarColor.GREEN)
                .build())
        .setHeaderAction(BACK)
        .setActions(
            Arrays.asList(
                Action.builder().setOnClickListener(() -> {}).setTitle("OK").build(),
                Action.builder()
                    .setBackgroundColor(CarColor.RED)
                    .setTitle("Throw")
                    .setOnClickListener(
                        () -> {
                          throw new RuntimeException("Error");
                        })
                    .build()))
        .build();
  }
}
