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

import static com.google.android.libraries.car.app.CarToast.LENGTH_LONG;
import static com.google.android.libraries.car.app.model.Action.BACK;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;

/** A screen demonstrating selectable lists. */
public final class SelectableListsDemoScreen extends Screen {

  public SelectableListsDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ListTemplate.Builder templateBuilder = ListTemplate.builder();

    ItemList radioList =
        ItemList.builder()
            .addItem(Row.builder().setTitle("Option 1").addText("Some additional text").build())
            .addItem(Row.builder().setTitle("Option 2").build())
            .addItem(Row.builder().setTitle("Option 3").build())
            .setSelectable(this::onSelected)
            .build();
    templateBuilder.addList(radioList, "Sample selectable list");

    return templateBuilder.setTitle("Selectable Lists Demo").setHeaderAction(BACK).build();
  }

  private void onSelected(int index) {
    CarToast.makeText(getCarContext(), "Changed selection to index: " + index, LENGTH_LONG).show();
  }
}
