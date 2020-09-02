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

package com.google.android.libraries.car.app.samples.navigation.car;

import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.SearchListener;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.SearchTemplate;
import com.google.android.libraries.car.app.model.Template;

/** Screen for showing entering a search and showing initial results. */
public final class SearchScreen extends Screen {

  private boolean isSearchComplete;
  private ItemList mItemList = withNoResults(ItemList.builder()).build();

  public SearchScreen(@NonNull CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return SearchTemplate.builder(
            new SearchListener() {
              @Override
              public void onSearchTextChanged(@NonNull String searchText) {
                doSearch(searchText);
              }

              @Override
              public void onSearchSubmitted(@NonNull String searchTerm) {
                isSearchComplete = true;
                doSearch(searchTerm);
              }
            })
        .setHeaderAction(Action.BACK)
        .setShowKeyboardByDefault(false)
        .setItemList(mItemList)
        .build();
  }

  private void doSearch(String searchText) {
    ItemList.Builder builder = ItemList.builder();
    if (searchText.isEmpty()) {
      mItemList = withNoResults(builder).build();
    } else if ("home".startsWith(searchText.toLowerCase())) {
      builder.addItem(
          Row.builder()
              .setTitle("Home")
              .setOnClickListener(
                  () -> {
                    setResult("Home");
                    finish();
                  })
              .build());
    } else if ("work".startsWith(searchText.toLowerCase())) {
      builder.addItem(
          Row.builder()
              .setTitle("Work")
              .setOnClickListener(
                  () -> {
                    setResult("Work");
                    finish();
                  })
              .build());
    }
    mItemList = builder.build();
    invalidate();
    return;
  }

  private static ItemList.Builder withNoResults(ItemList.Builder builder) {
    return builder.setNoItemsMessage("No Results");
  }
}
