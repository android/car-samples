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
package androidx.car.app.sample.places.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

/** Image-related utilities.  */
internal object ImageUtil {
    fun loadBitmapFromUrl(
        context: Context,
        urlString: String
    ): ListenableFuture<Bitmap> {
        val future = SettableFuture.create<Bitmap>()
        val url: URL
        try {
            url = URL(urlString)
        } catch (e: MalformedURLException) {
            future.setException(e)
            return future
        }

        val bmp: Bitmap
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            future.setException(e)
            return future
        }
        future.set(bmp)
        return future
    }
}
