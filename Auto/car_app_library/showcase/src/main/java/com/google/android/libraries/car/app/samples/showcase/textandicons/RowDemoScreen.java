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

package com.google.android.libraries.car.app.samples.showcase.textandicons;

import static com.google.android.libraries.car.app.model.Action.BACK;
import static com.google.android.libraries.car.app.model.CarColor.YELLOW;

import android.text.SpannableString;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.showcase.R;
import com.google.android.libraries.car.app.samples.showcase.common.Utils;

/** Creates a screen that shows different types of rows in a list */
public final class RowDemoScreen extends Screen implements DefaultLifecycleObserver {
  private static final String FULL_STAR = "\u2605";
  private static final String HALF_STAR = "\u00BD";

  public RowDemoScreen(CarContext carContext) {
    super(carContext);
    getLifecycle().addObserver(this);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ItemList.Builder listBuilder = ItemList.builder();

    listBuilder.addItem(Row.builder().setTitle("Just a title").build());
    listBuilder.addItem(
        Row.builder().setTitle("Title with app icon").setImage(CarIcon.APP_ICON).build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Title with resource ID image")
            .setImage(
                CarIcon.of(
                    IconCompat.createWithResource(
                        getCarContext(), R.drawable.ic_fastfood_white_48dp)))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Title with SVG image")
            .setImage(
                CarIcon.of(
                    IconCompat.createWithResource(
                        getCarContext(), R.drawable.ic_emoji_food_beverage_white_48dp)))
            .build());

    listBuilder.addItem(
        Row.builder()
            .setTitle("Title with multiple secondary text lines")
            .addText("Err and err and err again, but less and less and less.")
            .addText("- Piet Hein")
            .build());

    listBuilder.addItem(
        Row.builder().setTitle("Colored secondary text").addText(getRatingsString(3.5)).build());

    return ListTemplate.builder()
        .setSingleList(listBuilder.build())
        .setTitle("Rows Demo")
        .setHeaderAction(BACK)
        .build();
  }

  private static CharSequence getRatingsString(Double ratings) {
    String s;
    double r;
    for (s = "", r = ratings; r > 0; --r) {
      s += r < 1 ? HALF_STAR : FULL_STAR;
    }
    SpannableString ss = new SpannableString(s + " ratings: " + ratings);
    if (!s.isEmpty()) {
      Utils.colorize(ss, YELLOW, 0, s.length());
    }
    return ss;
  }
}
