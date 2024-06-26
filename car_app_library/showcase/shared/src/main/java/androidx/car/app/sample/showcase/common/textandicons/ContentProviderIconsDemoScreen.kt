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
package androidx.car.app.sample.showcase.common.textandicons

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.core.graphics.drawable.IconCompat

/** Creates a screen that demonstrate the image loading in the library using a content provider.  */
class ContentProviderIconsDemoScreen(carContext: CarContext) : Screen(carContext) {
    private val mHostPackageName: String?

    init {
        val hostInfo = carContext.hostInfo
        mHostPackageName = hostInfo?.packageName
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        val hostPackageName = mHostPackageName
        if (hostPackageName == null) {
            // Cannot get the host package name, show an error message.
            listBuilder.setNoItemsMessage(
                carContext.getString(R.string.images_unknown_host_error)
            )
        } else {
            for (i in ICON_DRAWABLES.indices) {
                val resId = ICON_DRAWABLES[i]
                val uri = DelayedFileProvider.getUriForResource(
                    carContext, hostPackageName,
                    resId
                )
                listBuilder.addItem(
                    Row.Builder()
                        .setImage(
                            CarIcon.Builder(
                                IconCompat.createWithContentUri(uri)
                            )
                                .build()
                        )
                        .setTitle(
                            carContext.getString(R.string.icon_title_prefix) + " "
                                    + i
                        )
                        .build()
                )
            }
        }


        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.content_provider_icons_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }

    companion object {
        private val ICON_DRAWABLES = intArrayOf(
            R.drawable.arrow_right_turn, R.drawable.arrow_straight, R.drawable.ic_i5,
            R.drawable.ic_520
        )
    }
}
