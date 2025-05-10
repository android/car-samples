/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.car.app.sample.navigation.common.car

import android.content.Context
import android.content.SharedPreferences
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.SectionedItemList
import androidx.car.app.model.Template
import androidx.car.app.model.Toggle
import androidx.car.app.sample.navigation.common.R

/** Settings screen demo.  */
class SettingsScreen internal constructor(carContext: CarContext) : Screen(carContext) {
    val mSharedPref: SharedPreferences =
        carContext.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

    override fun onGetTemplate(): Template {
        val templateBuilder = ListTemplate.Builder()

        // Create 2 sections with three settings each.
        val sectionABuilder = ItemList.Builder()
        sectionABuilder.addItem(buildRow(R.string.settings_one_label, R.string.settings_one_pref))
        sectionABuilder.addItem(buildRow(R.string.settings_two_label, R.string.settings_two_pref))
        sectionABuilder.addItem(
            buildRow(R.string.settings_three_label, R.string.settings_three_pref)
        )

        templateBuilder.addSectionedList(
            SectionedItemList.create(
                sectionABuilder.build(),
                carContext.getString(R.string.settings_section_a_label)
            )
        )

        val sectionBBuilder = ItemList.Builder()
        sectionBBuilder.addItem(
            buildRow(R.string.settings_four_label, R.string.settings_four_pref)
        )
        sectionBBuilder.addItem(
            buildRow(R.string.settings_five_label, R.string.settings_five_pref)
        )
        sectionBBuilder.addItem(buildRow(R.string.settings_six_label, R.string.settings_six_pref))

        templateBuilder.addSectionedList(
            SectionedItemList.create(
                sectionBBuilder.build(),
                carContext.getString(R.string.settings_section_b_label)
            )
        )
        return templateBuilder
            .setHeaderAction(Action.BACK)
            .setTitle(carContext.getString(R.string.settings_title))
            .build()
    }

    private fun buildRow(labelResourcee: Int, prefKeyResource: Int): Row {
        return Row.Builder()
            .setTitle(carContext.getString(labelResourcee))
            .setToggle(
                Toggle.Builder { value: Boolean ->
                    writeSharedPref(prefKeyResource, value)
                }
                    .setChecked(readSharedPref(prefKeyResource, false))
                    .build())
            .build()
    }

    private fun readSharedPref(keyResource: Int, defaultValue: Boolean): Boolean {
        return mSharedPref.getBoolean(carContext.getString(keyResource), defaultValue)
    }

    private fun writeSharedPref(keyResource: Int, value: Boolean) {
        val editor = mSharedPref.edit()
        editor.putBoolean(carContext.getString(keyResource), value)
        editor.commit()
    }
}
