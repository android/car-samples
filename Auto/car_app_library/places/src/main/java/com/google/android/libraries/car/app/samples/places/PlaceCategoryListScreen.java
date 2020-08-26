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

package com.google.android.libraries.car.app.samples.places;

import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.LatLng;
import com.google.android.libraries.car.app.model.Place;
import com.google.android.libraries.car.app.model.PlaceListMapTemplate;
import com.google.android.libraries.car.app.model.PlaceMarker;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.places.places.PlaceCategory;

/** A screen that displays a list of place categories. */
public class PlaceCategoryListScreen extends Screen implements DefaultLifecycleObserver {

  static PlaceCategoryListScreen create(CarContext carContext) {
    return new PlaceCategoryListScreen(carContext);
  }

  private Location mAnchorLocation;
  @NonNull private Location mSearchLocation = Constants.INITIAL_SEARCH_LOCATION;

  @Override
  public void onCreate(@NonNull LifecycleOwner owner) {
    setSearchLocation(Constants.INITIAL_SEARCH_LOCATION);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    // Build a list of rows for each category.
    ItemList.Builder listBuilder = ItemList.builder();
    for (PlaceCategory category : Constants.CATEGORIES) {
      listBuilder.addItem(
          Row.builder()
              .setTitle(category.getDisplayName())
              // Clicking on the row pushes a screen that shows the list of places of that
              // category around the center location.
              .setOnClickListener(
                  () ->
                      getScreenManager()
                          .push(
                              PlaceListScreen.create(
                                  getCarContext(),
                                  mSearchLocation,
                                  Constants.POI_SEARCH_RADIUS_METERS,
                                  Constants.POI_SEARCH_MAX_RESULTS,
                                  category,
                                  mAnchorLocation)))
              .setIsBrowsable(true)
              .build());
    }

    Place.Builder anchorBuilder;

    // If we have an anchor explicitly set, display it in the map. Otherwise, use the current
    // search location.
    if (mAnchorLocation != null) {
      anchorBuilder =
          Place.builder(LatLng.create(mAnchorLocation))
              .setMarker(PlaceMarker.builder().setColor(CarColor.BLUE).build());
    } else {
      anchorBuilder = Place.builder(LatLng.create(mSearchLocation));
    }

    ActionStrip actionStrip =
        ActionStrip.builder()
            .addAction(
                Action.builder()
                    .setTitle("Search")
                    .setOnClickListener(
                        () ->
                            getScreenManager()
                                .pushForResult(
                                    new SearchScreen(getCarContext()), this::setSearchLocation))
                    .build())
            .build();

    return PlaceListMapTemplate.builder()
        .setItemList(listBuilder.build())
        .setHeaderAction(Action.APP_ICON)
        .setActionStrip(actionStrip)
        .setTitle("Categories")
        .setCurrentLocationEnabled(true)
        .setAnchor(anchorBuilder.build())
        .build();
  }

  private void setSearchLocation(@Nullable Object location) {
    if (location != null) {
      mAnchorLocation = (Location) location;
      mSearchLocation = mAnchorLocation;
    }
  }

  private PlaceCategoryListScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
  }
}
