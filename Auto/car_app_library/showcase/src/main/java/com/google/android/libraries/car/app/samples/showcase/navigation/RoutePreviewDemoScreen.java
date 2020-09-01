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

package com.google.android.libraries.car.app.samples.showcase.navigation;

import static com.google.android.libraries.car.app.CarToast.LENGTH_LONG;

import android.text.SpannableString;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.DurationSpan;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.RoutePreviewNavigationTemplate;
import java.util.concurrent.TimeUnit;

/** Creates a screen using the {@link RoutePreviewNavigationTemplate} */
public final class RoutePreviewDemoScreen extends Screen {
  public RoutePreviewDemoScreen(CarContext carContext) {
    super(carContext);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    SpannableString firstRoute = new SpannableString("   \u00b7 Shortest route");
    firstRoute.setSpan(DurationSpan.create(TimeUnit.HOURS.toSeconds(26)), 0, 1, 0);
    SpannableString secondRoute = new SpannableString("   \u00b7 Less busy");
    secondRoute.setSpan(DurationSpan.create(TimeUnit.HOURS.toSeconds(24)), 0, 1, 0);
    SpannableString thirdRoute = new SpannableString("   \u00b7 HOV friendly");
    thirdRoute.setSpan(DurationSpan.create(TimeUnit.MINUTES.toSeconds(867)), 0, 1, 0);

    return RoutePreviewNavigationTemplate.builder()
        .setItemList(
            ItemList.builder()
                .setSelectable(this::onRouteSelected)
                .addItem(Row.builder().setTitle(firstRoute).addText("Via NE 8th Street").build())
                .addItem(Row.builder().setTitle(secondRoute).addText("Via NE 1st Ave").build())
                .addItem(Row.builder().setTitle(thirdRoute).addText("Via NE 4th Street").build())
                .setOnItemsVisibilityChangeListener(this::onRoutesVisible)
                .build())
        .setNavigateAction(
            Action.builder()
                .setTitle("Continue to route")
                .setOnClickListener(this::onNavigate)
                .build())
        .setTitle("Routes")
        .setHeaderAction(Action.BACK)
        .build();
  }

  private void onNavigate() {
    CarToast.makeText(getCarContext(), "Navigation Requested", LENGTH_LONG * 2).show();
  }

  private void onRouteSelected(int index) {
    CarToast.makeText(getCarContext(), "Selected route: " + index, LENGTH_LONG).show();
  }

  private void onRoutesVisible(int startIndex, int endIndex) {
    CarToast.makeText(
            getCarContext(), "Visible routes: [" + startIndex + "," + endIndex + "]", LENGTH_LONG)
        .show();
  }
}
