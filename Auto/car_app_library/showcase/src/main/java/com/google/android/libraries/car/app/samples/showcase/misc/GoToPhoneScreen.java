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

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Pane;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** A {@link Screen} for controlling UI for allowing user to go to the phone. */
public class GoToPhoneScreen extends Screen {
  public static final String PHONE_COMPLETE_ACTION = "ActionComplete";

  private boolean mIsPhoneFlowComplete;

  public GoToPhoneScreen(CarContext carContext) {
    super(carContext);
  }

  public void onPhoneFlowComplete() {
    mIsPhoneFlowComplete = true;
    invalidate();
  }

  @NonNull
  @Override
  public Template getTemplate() {
    Pane.Builder pane = Pane.builder();
    if (mIsPhoneFlowComplete) {
      pane.addRow(Row.builder().setTitle("The phone task is now complete").build());
    } else {
      getCarContext()
          .startActivity(
              new Intent(getCarContext(), OnPhoneActivity.class)
                  .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
      pane.addRow(Row.builder().setTitle("Please continue on your phone").build());
    }

    return PaneTemplate.builder(pane.build())
        .setTitle("Go-to-Phone Screen")
        .setHeaderAction(BACK)
        .build();
  }
}
