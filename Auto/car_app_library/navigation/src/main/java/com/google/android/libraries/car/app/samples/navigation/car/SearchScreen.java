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
import androidx.annotation.Nullable;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.SearchListener;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.SearchTemplate;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.navigation.model.DemoScripts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Screen for showing entering a search and showing initial results. */
public final class SearchScreen extends Screen {

  @NonNull private final Action mSettingsAction;
  @NonNull private final SurfaceRenderer mSurfaceRenderer;

  private ItemList mItemList = withNoResults(ItemList.builder()).build();
  private final List<String> mTitles = new ArrayList<>();
  @Nullable private String mSearchText;
  private final List<String> mFakeTitles =
      new ArrayList<>(Arrays.asList("Starbucks", "Shell", "Costco", "Aldi", "Safeway"));

  public SearchScreen(
      @NonNull CarContext carContext,
      @NonNull Action settingsAction,
      SurfaceRenderer surfaceRenderer) {
    super(carContext);
    mSettingsAction = settingsAction;
    mSurfaceRenderer = surfaceRenderer;
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
                // When the user presses the search key use the top item in the list as the
                // result and simulate as if the user had pressed that.
                if (mTitles.size() > 0) {
                  onClickSearch(mTitles.get(0));
                }
              }
            })
        .setHeaderAction(Action.BACK)
        .setShowKeyboardByDefault(false)
        .setItemList(mItemList)
        .setInitialSearchText(mSearchText)
        .build();
  }

  private void doSearch(String searchText) {
    mSearchText = searchText;
    mTitles.clear();
    ItemList.Builder builder = ItemList.builder();
    if (searchText.isEmpty()) {
      withNoResults(builder);
    } else {
      // Create some fake data entries.
      for (String title : mFakeTitles) {
        mTitles.add(title);
        builder.addItem(
            Row.builder().setTitle(title).setOnClickListener(() -> onClickSearch(title)).build());
      }
    }
    mItemList = builder.build();
    invalidate();
    return;
  }

  private static ItemList.Builder withNoResults(ItemList.Builder builder) {
    return builder.setNoItemsMessage("No Results");
  }

  private void onClickSearch(@NonNull String searchText) {
    getScreenManager()
        .pushForResult(
            new RoutePreviewScreen(getCarContext(), mSettingsAction, mSurfaceRenderer),
            this::onRouteSelected);
  }

  private void onRouteSelected(@Nullable Object previewResult) {
    int previewIndex = previewResult == null ? -1 : (int) previewResult;
    if (previewIndex < 0) {
      return;
    }
    // Start the same demo instructions. More will be added in the future.
    setResult(DemoScripts.getNavigateHome(getCarContext()));
    finish();
  }
}
