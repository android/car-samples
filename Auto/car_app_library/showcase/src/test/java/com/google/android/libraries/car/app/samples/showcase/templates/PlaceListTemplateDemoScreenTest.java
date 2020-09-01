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
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ApplicationProvider;
import com.google.android.libraries.car.app.model.LatLng;
import com.google.android.libraries.car.app.model.PlaceListMapTemplate;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.model.PlaceListMapTemplateController;
import com.google.android.libraries.car.app.testing.model.RowController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Tests for the {@link PlaceListTemplateDemoScreen}. */
@RunWith(RobolectricTestRunner.class)
public final class PlaceListTemplateDemoScreenTest {
  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  @Test
  public void getTemplate_templateContainsExpectedRow() {
    PlaceListTemplateDemoScreen screen = new PlaceListTemplateDemoScreen(testCarContext);

    PlaceListMapTemplateController controller =
        PlaceListMapTemplateController.of((PlaceListMapTemplate) screen.getTemplate());
    assertEquals("Place List Template Demo", controller.getTitle());

    RowController kirklandRow =
        controller.getItemList().getItemByTitle("Google Kirkland", RowController.class);
    assertThat(kirklandRow.getMetadata().getPlace().getLatLng())
        .isEqualTo(LatLng.create(47.6696482, -122.19950278));
  }
}
