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
package androidx.car.app.sample.showcase.common.common

import android.text.Spannable
import android.text.SpannableString
import androidx.car.app.model.CarColor
import androidx.car.app.model.ClickableSpan
import androidx.car.app.model.ForegroundCarColorSpan

/** Assorted utilities.  */
object Utils {
    /** Colorize the given string.  */
    fun colorize(
        s: SpannableString, color: CarColor, index: Int,
        length: Int
    ) {
        s.setSpan(
            ForegroundCarColorSpan.create(color),
            index,
            index + length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    /** Colorize the given string.  */
    @JvmStatic
    fun colorize(
        s: String, color: CarColor, index: Int,
        length: Int
    ): CharSequence {
        val ss = SpannableString(s)
        ss.setSpan(
            ForegroundCarColorSpan.create(color),
            index,
            index + length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ss
    }

    /** Make the given string clickable.  */
    fun clickable(
        s: String, index: Int, length: Int,
        action: Runnable
    ): CharSequence {
        val ss = SpannableString(s)
        ss.setSpan(
            ClickableSpan.create { action.run() },
            index,
            index + length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ss
    }
}
