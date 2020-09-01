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

import androidx.test.core.app.ApplicationProvider;
import com.google.android.libraries.car.app.model.Item;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.testing.TestAppManager;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.model.ItemController;
import com.google.android.libraries.car.app.testing.model.ListTemplateController;
import com.google.android.libraries.car.app.testing.model.RowController;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Tests for the {@link ListTemplateDemoScreen}. */
@RunWith(RobolectricTestRunner.class)
public final class ListTemplateDemoScreenTest {
  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  @Test
  public void getTemplate_clickOnRowsShowsExpectedToasts() {
    ListTemplateDemoScreen screen = new ListTemplateDemoScreen(testCarContext);

    ListTemplateController controller =
        ListTemplateController.of((ListTemplate) screen.getTemplate());

    for (ItemController<? extends Item> item : controller.getSingleList().getItems()) {
      ((RowController) item).performClick();
    }

    List<CharSequence> toastsShown =
        testCarContext.getCarService(TestAppManager.class).getToastsShown();
    assertThat(toastsShown.get(0).toString()).isEqualTo("Parked action");
    for (int i = 1; i < toastsShown.size(); i++) {
      int row = i + 1;
      assertThat(toastsShown.get(i).toString()).isEqualTo("Clicked row: " + row);
    }
  }
}
