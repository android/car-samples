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
import com.google.android.libraries.car.app.model.SearchTemplate;
import com.google.android.libraries.car.app.testing.TestAppManager;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.model.SearchTemplateController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Tests for the {@link SearchTemplateDemoScreen}. */
@RunWith(RobolectricTestRunner.class)
public final class SearchTemplateDemoScreenTest {
  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  @Test
  public void changeSearchText_expectedCallbackBehavior() {
    SearchTemplateDemoScreen screen = new SearchTemplateDemoScreen(testCarContext);

    SearchTemplateController controller =
        SearchTemplateController.of((SearchTemplate) screen.getTemplate());

    controller.performSearchTextChanged("Space Needle");

    assertThat(
            testCarContext.getCarService(TestAppManager.class).getToastsShown().get(0).toString())
        .isEqualTo("Search changed: Space Needle");
  }

  @Test
  public void changeSearchSubmitted_expectedCallbackBehavior() {
    SearchTemplateDemoScreen screen = new SearchTemplateDemoScreen(testCarContext);

    SearchTemplateController controller =
        SearchTemplateController.of((SearchTemplate) screen.getTemplate());

    controller.performSearchSubmitted("Seattle");

    assertThat(
            testCarContext.getCarService(TestAppManager.class).getToastsShown().get(0).toString())
        .isEqualTo("Search submitted: Seattle");
  }
}
