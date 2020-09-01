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
import static com.google.android.libraries.car.app.CarToast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.SearchListener;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.SearchTemplate;
import com.google.android.libraries.car.app.model.Template;

/** A screen that demonstrates the search template. */
public class SearchTemplateDemoScreen extends Screen {

  public SearchTemplateDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();
    for (int i = 1; i <= 6; ++i) {
      listBuilder.addItem(
          Row.builder()
              .setTitle("Title " + i)
              .addText("First line of text")
              .addText("Second line of text")
              .build());
    }

    SearchListener searchListener =
        new SearchListener() {
          @Override
          public void onSearchTextChanged(@NonNull String searchText) {
            CarToast.makeText(getCarContext(), "Search changed: " + searchText, LENGTH_SHORT)
                .show();
          }

          @Override
          public void onSearchSubmitted(@NonNull String searchText) {
            CarToast.makeText(getCarContext(), "Search submitted: " + searchText, LENGTH_LONG)
                .show();
          }
        };

    return SearchTemplate.builder(searchListener)
        .setSearchHint("Search here")
        .setHeaderAction(Action.BACK)
        .setShowKeyboardByDefault(false)
        .setItemList(listBuilder.build())
        .build();
  }
}
