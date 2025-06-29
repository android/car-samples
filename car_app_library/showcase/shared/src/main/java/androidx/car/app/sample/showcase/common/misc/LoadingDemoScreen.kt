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
package androidx.car.app.sample.showcase.common.misc

import android.os.Handler
import android.os.Looper
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Creates a screen that shows loading states in a pane.  */
class LoadingDemoScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {
    private var mIsFinishedLoading = false
    private val mHandler = Handler(Looper.getMainLooper())

    init {
        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        // Post a message that finishes loading the template after some time.
        mHandler.postDelayed(
            {
                mIsFinishedLoading = true
                invalidate()
            },
            LOADING_TIME_MILLIS.toLong()
        )
    }

    override fun onGetTemplate(): Template {
        val paneBuilder = Pane.Builder()

        if (!mIsFinishedLoading) {
            paneBuilder.setLoading(true)
        } else {
            paneBuilder.addRow(
                Row.Builder()
                    .setTitle(carContext.getString(R.string.loading_demo_row_title))
                    .build()
            )
        }

        return PaneTemplate.Builder(paneBuilder.build())
            .setTitle(carContext.getString(R.string.loading_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }

    companion object {
        private const val LOADING_TIME_MILLIS = 2000
    }
}
