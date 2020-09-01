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

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.ListTemplate;
import com.google.android.libraries.car.app.model.Row;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.model.Toggle;
import com.google.android.libraries.car.app.samples.navigation.R;
import java.util.HashMap;
import java.util.Map;

/** Settings screen demo. */
public final class SettingsScreen extends Screen {

  @NonNull final SharedPreferences mSharedPref;

  SettingsScreen(@NonNull CarContext carContext) {
    super(carContext);
    mSharedPref = carContext.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    ListTemplate.Builder templateBuilder = ListTemplate.builder();

    // Create 2 sections with three settings each.
    ItemList.Builder sectionABuilder = ItemList.builder();
    sectionABuilder.addItem(buildRow(R.string.settings_one_label, R.string.settings_one_pref));
    sectionABuilder.addItem(buildRow(R.string.settings_two_label, R.string.settings_two_pref));
    sectionABuilder.addItem(buildRow(R.string.settings_three_label, R.string.settings_three_pref));

    templateBuilder.addList(
        sectionABuilder.build(), getCarContext().getString(R.string.settings_section_a_label));

    ItemList.Builder sectionBBuilder = ItemList.builder();
    sectionBBuilder.addItem(buildRow(R.string.settings_four_label, R.string.settings_four_pref));
    sectionBBuilder.addItem(buildRow(R.string.settings_five_label, R.string.settings_five_pref));
    sectionBBuilder.addItem(buildRow(R.string.settings_six_label, R.string.settings_six_pref));

    templateBuilder.addList(
        sectionBBuilder.build(), getCarContext().getString(R.string.settings_section_b_label));
    return templateBuilder
        .setHeaderAction(Action.BACK)
        .setTitle(getCarContext().getString(R.string.settings_title))
        .build();
  }

  @NonNull private Row buildRow(int labelResourcee, int prefKeyResource) {
    return Row.builder()
        .setTitle(getCarContext().getString(labelResourcee))
        .setToggle(Toggle.builder((value) -> {writeSharedPref(prefKeyResource, value);})
            .setChecked(readSharedPref(prefKeyResource, false)).build())
        .build();
  }

  private boolean readSharedPref(int keyResource, boolean defaultValue) {
    return mSharedPref.getBoolean(getCarContext().getString(keyResource), defaultValue);
  }

  private void writeSharedPref(int keyResource, boolean value) {
    SharedPreferences.Editor editor = mSharedPref.edit();
    editor.putBoolean(getCarContext().getString(keyResource), value);
    editor.commit();
  }
}
