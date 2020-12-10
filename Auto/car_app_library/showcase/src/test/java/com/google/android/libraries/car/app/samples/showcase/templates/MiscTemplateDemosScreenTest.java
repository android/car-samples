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
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.TestScreenManager;
import com.google.android.libraries.car.app.testing.model.ListTemplateController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** Tests for the {@link MiscTemplateDemosScreen}. */
@RunWith(RobolectricTestRunner.class)
public final class MiscTemplateDemosScreenTest {
  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  @Test
  public void getTemplate_hasExpectedRows() {
    MiscTemplateDemosScreen screen = new MiscTemplateDemosScreen(testCarContext);

    ListTemplateController controller =
        ListTemplateController.of((ListTemplate) screen.getTemplate());

    assertThat(controller.getSingleList().getItems()).hasSize(6);
  }

  @Test
  public void getTemplate_clickOnRowPushesExpectedScreen() {
    MiscTemplateDemosScreen screen = new MiscTemplateDemosScreen(testCarContext);

    ListTemplateController controller =
        ListTemplateController.of((ListTemplate) screen.getTemplate());

    controller.getRowByTitle("List Template Demo").performClick();

    Screen screenPushed =
        testCarContext.getCarService(TestScreenManager.class).getScreensPushed().get(0);

    assertThat(screenPushed).isInstanceOf(ListTemplateDemoScreen.class);
  }
}
