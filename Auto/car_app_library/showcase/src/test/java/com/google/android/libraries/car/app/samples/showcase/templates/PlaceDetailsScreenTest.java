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

import static com.google.common.truth.Truth.assertThat;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.model.PaneTemplate;
import com.google.android.libraries.car.app.model.PlaceMarker;
import com.google.android.libraries.car.app.samples.showcase.common.PlaceDetailsScreen;
import com.google.android.libraries.car.app.samples.showcase.common.PlaceInfo;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.model.PaneTemplateController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Tests for the {@link
 * com.google.android.libraries.car.app.samples.showcase.common.PlaceDetailsScreen}.
 */
@RunWith(RobolectricTestRunner.class)
public final class PlaceDetailsScreenTest {
  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  private final PlaceInfo googleBellevue =
      new PlaceInfo(
          "Google Bellevue",
          "1120 112th Ave NE, Bellevue, WA 98004",
          "Description",
          "BVE",
          "+14252301301",
          new Location("PlaceDetailsScreenTest"),
          PlaceMarker.getDefault());

  @Test
  public void clickOnNavigateAction_sendsNavigationIntent() {
    PlaceDetailsScreen screen = PlaceDetailsScreen.create(testCarContext, googleBellevue);

    PaneTemplateController controller =
        PaneTemplateController.of((PaneTemplate) screen.getTemplate());

    controller.getPane().getActions().get(0).performClick();

    Intent navigateIntent = testCarContext.getStartCarAppIntents().get(0);

    assertThat(navigateIntent.getAction()).isEqualTo(CarContext.ACTION_NAVIGATE);
    assertThat(navigateIntent.getData())
        .isEqualTo(Uri.parse("geo:0,0?q=" + googleBellevue.address));
  }

  @Test
  public void clickOnCallAction_sendsCallIntent() {
    PlaceDetailsScreen screen = PlaceDetailsScreen.create(testCarContext, googleBellevue);

    PaneTemplateController controller =
        PaneTemplateController.of((PaneTemplate) screen.getTemplate());

    controller.getPane().getActions().get(1).performClick();

    Intent dialIntent = testCarContext.getStartCarAppIntents().get(0);

    assertThat(dialIntent.getAction()).isEqualTo(Intent.ACTION_DIAL);
    assertThat(dialIntent.getData()).isEqualTo(Uri.parse("tel:" + googleBellevue.phoneNumber));
  }
}
