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

package com.google.android.libraries.car.app.samples.showcase.misc;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.MessageTemplate;
import com.google.android.libraries.car.app.model.ParkedOnlyOnClickListener;
import com.google.android.libraries.car.app.model.Template;
import java.util.Arrays;

/**
 * A {@link Screen} to be used in a preseeding flow, which adds screens to the back stack on
 * startup.
 */
public class PreSeedingFlowScreen extends Screen {

  public PreSeedingFlowScreen(@NonNull CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return MessageTemplate.builder(
            "This screen is displayed before the main screen to allow the app to perform tasks"
                + " such as granting permissions.")
        .setHeaderAction(Action.APP_ICON)
        .setActions(
            Arrays.asList(
                Action.builder()
                    .setBackgroundColor(CarColor.BLUE)
                    .setOnClickListener(
                        ParkedOnlyOnClickListener.create(
                            () -> {
                              // Finish the screen to go back to "home" but this is where the
                              // application start an on phone activity that will request a needed
                              // permission, or login.
                              finish();
                            }))
                    .setTitle("Open On Phone")
                    .build(),
                Action.builder()
                    .setOnClickListener(getCarContext()::finishCarApp)
                    .setBackgroundColor(CarColor.RED)
                    .setTitle("Exit App")
                    .build()))
        .build();
  }
}
