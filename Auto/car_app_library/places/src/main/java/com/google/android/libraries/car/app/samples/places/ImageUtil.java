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

package com.google.android.libraries.car.app.samples.places;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.IOException;

/** Image-related utilities. */
class ImageUtil {
  static ListenableFuture<Bitmap> loadBitmapFromUrl(Context context, String url) {
    SettableFuture<Bitmap> future = SettableFuture.create();
    Glide.with(context)
        .asBitmap()
        .load(url)
        .into(
            new CustomTarget<Bitmap>() {
              @Override
              public void onResourceReady(
                  Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                future.set(resource);
              }

              @Override
              public void onLoadCleared(@Nullable Drawable placeholder) {
                future.setException(new IOException("Load of bitmap cancelled for: " + url));
              }
            });
    return future;
  }

  private ImageUtil() {}
}
