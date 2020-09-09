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

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.MessageTemplate;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.ShowcaseService;
import java.util.Collections;

/**
 * A {@link Screen} that provides an action to exit the car app.
 */
public class FinishAppScreen extends Screen {
  private final boolean mWillPreseed;

  protected FinishAppScreen(@NonNull CarContext carContext, boolean willPreseed) {
    super(carContext);
    mWillPreseed = willPreseed;
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return MessageTemplate.builder(
            mWillPreseed
                ? "This will finish the app, and when you return it will pre-seed a permission"
                    + " screen"
                : "This will finish the app")
        .setTitle("Finish App Demo")
        .setHeaderAction(BACK)
        .setActions(
            Collections.singletonList(
                Action.builder()
                    .setOnClickListener(
                        () -> {
                          getCarContext()
                              .getSharedPreferences(
                                  ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
                              .edit()
                              .putBoolean(ShowcaseService.PRE_SEED_KEY, true)
                              .apply();
                          getCarContext().finishCarApp();
                        })
                    .setTitle("Exit")
                    .build()))
        .build();
  }
}
