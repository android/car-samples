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

package com.google.android.libraries.car.app.samples.showcase.common;

import android.text.Spannable;
import android.text.SpannableString;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.ForegroundCarColorSpan;
import com.google.android.libraries.car.app.model.ItemList;
import com.google.android.libraries.car.app.model.OnClickListener;
import com.google.android.libraries.car.app.model.Row;

/** Assorted utilities. */
public abstract class Utils {
  public static void colorize(SpannableString s, CarColor color, int index, int length) {
    s.setSpan(
        ForegroundCarColorSpan.create(color),
        index,
        index + length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence colorize(String s, CarColor color, int index, int length) {
    SpannableString ss = new SpannableString(s);
    ss.setSpan(
        ForegroundCarColorSpan.create(color),
        index,
        index + length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return ss;
  }

  public static void addToListBrowsable(
      ItemList.Builder listBuilder, String title, OnClickListener onClickListener) {
    addToList(listBuilder, title, onClickListener, true);
  }

  public static void addToList(
      ItemList.Builder listBuilder,
      String title,
      OnClickListener onClickListener,
      boolean isBrowsable) {
    listBuilder.addItem(
        Row.builder()
            .setTitle(title)
            .setOnClickListener(onClickListener)
            .setIsBrowsable(isBrowsable)
            .build());
  }

  private Utils() {}
}
