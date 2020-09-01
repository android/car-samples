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

package com.google.android.libraries.car.app.samples.helloworld;

import static com.google.common.truth.Truth.assertThat;

import androidx.test.core.app.ApplicationProvider;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.testing.CarAppServiceController;
import com.google.android.libraries.car.app.testing.TestCarContext;
import com.google.android.libraries.car.app.testing.TestScreenManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/** Tests for the {@link HelloWorldService}. */
@RunWith(RobolectricTestRunner.class)
public final class HelloWorldServiceTest {

  private final TestCarContext testCarContext =
      TestCarContext.createCarContext(ApplicationProvider.getApplicationContext());

  @Test
  public void onCreateScreen_returnsExpectedScreen() {
    HelloWorldService service = Robolectric.setupService(HelloWorldService.class);
    CarAppServiceController controller = CarAppServiceController.of(testCarContext, service);

    controller.create();

    Screen screenCreated =
        testCarContext.getCarService(TestScreenManager.class).getScreensPushed().get(0);
    assertThat(screenCreated).isInstanceOf(HelloWorldScreen.class);
  }
}
