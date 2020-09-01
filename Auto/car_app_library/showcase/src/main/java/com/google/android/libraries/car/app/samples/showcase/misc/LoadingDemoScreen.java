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

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Pane;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** Creates a screen that shows loading states in a pane. */
public final class LoadingDemoScreen extends Screen implements DefaultLifecycleObserver {
  private static final int LOADING_TIME_MILLIS = 2000;
  private boolean isFinishedLoading = false;
  private final Handler handler = new Handler();

  public LoadingDemoScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
  }

  @Override
  @SuppressWarnings({"FutureReturnValueIgnored"})
  public void onStart(@NonNull LifecycleOwner owner) {
    // Post a message that finishes loading the the template after some time.
    handler.postDelayed(
        () -> {
          isFinishedLoading = true;
          invalidate();
        },
        LOADING_TIME_MILLIS);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    Pane.Builder paneBuilder = Pane.builder();

    if (!isFinishedLoading) {
      paneBuilder.setIsLoading(true);
    } else {
      paneBuilder.addRow(Row.builder().setTitle("Loading Complete!").build());
    }

    return PaneTemplate.builder(paneBuilder.build())
        .setTitle("Loading Demo")
        .setHeaderAction(BACK)
        .build();
  }
}
