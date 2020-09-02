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

import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.MessageTemplate;
import com.google.android.libraries.car.app.model.Template;
import java.util.Collections;

/**
 * A {@link Screen} that provides an action to exit the car app.
 */
public class FinishAppScreen extends Screen {

  protected FinishAppScreen(@NonNull CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return MessageTemplate.builder("The app encountered an unrecoverable error")
        .setTitle("Message Template Demo")
        .setHeaderAction(BACK)
        .setActions(
            Collections.singletonList(
                Action.builder()
                    .setOnClickListener(getCarContext()::finishCarApp)
                    .setTitle("Exit")
                    .build()))
        .build();
  }
}
