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

import static com.google.android.libraries.car.app.model.DurationSpan.create;

import android.text.SpannableString;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.RoutePreviewNavigationTemplate;
import com.google.android.libraries.car.app.samples.navigation.R;
import java.util.concurrent.TimeUnit;

/** The route preview screen for the app. */
public final class RoutePreviewScreen extends Screen {
  private static final String TAG = "NavigationDemo";

  @NonNull private final Action mSettingsAction;

  int mLastSelectedIndex = -1;

  public RoutePreviewScreen(@NonNull CarContext carContext, @NonNull Action settingsAction) {
    super(carContext);
    mSettingsAction = settingsAction;
  }

  @NonNull
  @Override
  public Template getTemplate() {
    Log.i(TAG, "In RoutePreviewScreen.getTemplate()");
    SpannableString firstRoute = new SpannableString("   \u00b7 Shortest route");
    firstRoute.setSpan(create(TimeUnit.HOURS.toSeconds(26)), 0, 1, 0);
    SpannableString secondRoute = new SpannableString("   \u00b7 Less busy");
    secondRoute.setSpan(create(TimeUnit.HOURS.toSeconds(24)), 0, 1, 0);
    SpannableString thirdRoute = new SpannableString("   \u00b7 HOV friendly");
    thirdRoute.setSpan(create(TimeUnit.MINUTES.toSeconds(867)), 0, 1, 0);

    ItemList.Builder listBuilder = ItemList.builder();
    listBuilder
        .setSelectable(this::onRouteSelected)
        .addItem(Row.builder().setTitle(firstRoute).addText("Via NE 8th Street").build())
        .addItem(Row.builder().setTitle(secondRoute).addText("Via NE 1st Ave").build())
        .addItem(Row.builder().setTitle(thirdRoute).addText("Via NE 4th Street").build())
        .setOnItemsVisibilityChangeListener(this::onRoutesVisible);

    return RoutePreviewNavigationTemplate.builder()
        .setItemList(listBuilder.build())
        .setTitle(getCarContext().getString(R.string.route_preview))
        .setActionStrip(ActionStrip.builder().addAction(mSettingsAction).build())
        .setHeaderAction(Action.BACK)
        .setNavigateAction(
            Action.builder()
                .setTitle("Continue to route")
                .setOnClickListener(this::onNavigate)
                .build())
        .build();
  }

  private void onRouteSelected(int index) {
    mLastSelectedIndex = index;
  }

  private void onRoutesVisible(int startIndex, int endIndex) {
    if (Log.isLoggable(TAG, Log.INFO)) {
      Log.i(
          TAG,
          String.format(
              "In RoutePreviewScreen.onRoutesVisible start:%d end:%d", startIndex, endIndex));
    }
  }

  private void onNavigate() {
    setResult(mLastSelectedIndex);
    finish();
  }
}
