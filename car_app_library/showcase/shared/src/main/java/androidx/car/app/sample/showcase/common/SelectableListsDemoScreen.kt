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
package androidx.car.app.sample.showcase.common

import android.graphics.BitmapFactory
import android.text.SpannableString
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.SectionedItemList
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.common.Utils
import androidx.core.graphics.drawable.IconCompat

/** A screen demonstrating selectable lists.  */
class SelectableListsDemoScreen(carContext: CarContext) : Screen(carContext) {
    private var mIsEnabled = true

    override fun onGetTemplate(): Template {
        val templateBuilder = ListTemplate.Builder()

        // The Image to be displayed in a row
        val resources = carContext.resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_image_square)
        val mImage = IconCompat.createWithBitmap(bitmap)

        val radioList =
            ItemList.Builder()
                .addItem(
                    Row.Builder()
                        .setTitle(
                            carContext
                                .getString(R.string.option_row_radio_title)
                        )
                        .addText(
                            carContext.getString(
                                R.string.additional_text
                            )
                        )
                        .setEnabled(mIsEnabled)
                        .build()
                )
                .addItem(
                    Row.Builder()
                        .setImage(
                            CarIcon.Builder(
                                IconCompat.createWithResource(
                                    carContext,
                                    R.drawable
                                        .ic_fastfood_white_48dp
                                )
                            )
                                .build(),
                            Row.IMAGE_TYPE_ICON
                        )
                        .setTitle(
                            carContext
                                .getString(R.string.option_row_radio_icon_title)
                        )
                        .addText(
                            carContext.getString(
                                R.string.additional_text
                            )
                        )
                        .setEnabled(mIsEnabled)
                        .build()
                )
                .addItem(
                    Row.Builder()
                        .setImage(
                            CarIcon.Builder(mImage).build(),
                            Row.IMAGE_TYPE_LARGE
                        )
                        .setTitle(
                            carContext
                                .getString(
                                    R.string.option_row_radio_icon_colored_text_title
                                )
                        )
                        .addText(
                            getColoredString(
                                carContext.getString(
                                    R.string.additional_text
                                ), mIsEnabled
                            )
                        )
                        .setEnabled(mIsEnabled)
                        .build()
                )
                .setOnSelectedListener { index: Int -> this.onSelected(index) }
                .build()
        templateBuilder.addSectionedList(
            SectionedItemList.create(
                radioList,
                carContext.getString(R.string.sample_additional_list)
            )
        )

        return templateBuilder
            .setTitle(carContext.getString(R.string.selectable_lists_demo_title))
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(
                                if (mIsEnabled
                                ) carContext.getString(
                                    R.string.disable_all_rows
                                )
                                else carContext.getString(
                                    R.string.enable_all_rows
                                )
                            )
                            .setOnClickListener {
                                mIsEnabled = !mIsEnabled
                                invalidate()
                            }
                            .build())
                    .build())
            .setHeaderAction(
                Action.BACK
            ).build()
    }

    private fun onSelected(index: Int) {
        CarToast.makeText(
            carContext,
            carContext
                .getString(R.string.changes_selection_to_index_toast_msg_prefix)
                    + ":"
                    + " " + index, CarToast.LENGTH_LONG
        )
            .show()
    }

    companion object {
        // Get colorized spannable string
        private fun getColoredString(str: String, isEnabled: Boolean): CharSequence {
            if (isEnabled && !str.isEmpty()) {
                val ss = SpannableString(str)
                Utils.colorize(ss, CarColor.YELLOW, 0, str.length)
                return ss
            }
            return str
        }
    }
}
