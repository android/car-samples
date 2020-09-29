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

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.model.DistanceSpan;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.LatLng;
import com.google.android.libraries.car.app.model.Metadata;
import com.google.android.libraries.car.app.model.Place;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.PlaceListNavigationTemplate;
import com.google.android.libraries.car.app.samples.navigation.model.DemoScripts;
import com.google.android.libraries.car.app.samples.navigation.model.PlaceInfo;

/** Screen for showing a list of places from a search. */
public final class SearchResultsScreen extends Screen {
  private static final String TAG = "NavigationDemo";

  private static final int MAX_RESULTS = 6;

  @NonNull private final Action mSettingsAction;
  @NonNull private final SurfaceRenderer mSurfaceRenderer;
  @NonNull private final String mSearchText;

  public SearchResultsScreen(
      @NonNull CarContext carContext,
      @NonNull Action settingsAction,
      @NonNull SurfaceRenderer surfaceRenderer,
      @NonNull String searchText) {
    super(carContext);
    mSettingsAction = settingsAction;
    mSurfaceRenderer = surfaceRenderer;
    mSearchText = searchText;
  }

  @NonNull
  @Override
  public Template getTemplate() {
    mSurfaceRenderer.updateMarkerVisibility(
        /* showMarkers=*/ false, /* numMarkers=*/ 0, /* activeMarker=*/ -1);
    ItemList.Builder listBuilder = ItemList.builder();

    int numItems = ((int) (Math.random() * 6.0)) + 1;

    for (int i = 0; i < numItems; i++) {
      PlaceInfo place =
          new PlaceInfo(
              String.format("Result %d", i + 1), String.format("%d Main Street.", (i + 1) * 10));

      SpannableString address = new SpannableString("  \u00b7 " + place.getDisplayAddress());
      DistanceSpan distanceSpan =
          DistanceSpan.create(
              Distance.create(/* displayDistance= */ i + 1, Distance.UNIT_KILOMETERS_P1));
      address.setSpan(distanceSpan, 0, 1, SPAN_INCLUSIVE_INCLUSIVE);
      listBuilder.addItem(
          Row.builder()
              .setTitle(place.getName())
              .addText(address)
              .setOnClickListener(() -> onClickItem(place))
              .setMetadata(Metadata.ofPlace(Place.builder(LatLng.create(1, 1)).build()))
              .build());
    }

    return PlaceListNavigationTemplate.builder()
        .setItemList(listBuilder.build())
        .setTitle("Search: " + mSearchText)
        .setActionStrip(ActionStrip.builder().addAction(mSettingsAction).build())
        .setHeaderAction(Action.BACK)
        .build();
  }

  private void onClickItem(@NonNull PlaceInfo place) {
    getScreenManager()
        .pushForResult(
            new RoutePreviewScreen(getCarContext(), mSettingsAction, mSurfaceRenderer),
            this::onRoutePreviewResult);
  }

  private void onRoutePreviewResult(@Nullable Object previewResult) {
    int previewIndex = previewResult == null ? -1 : (int) previewResult;
    if (previewIndex < 0) {
      return;
    }
    // Start the same demo instructions. More will be added in the future.
    setResult(DemoScripts.getNavigateHome(getCarContext()));
    finish();
  }
}
