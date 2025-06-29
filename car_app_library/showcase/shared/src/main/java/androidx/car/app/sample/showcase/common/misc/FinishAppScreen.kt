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

import android.content.Context
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.ShowcaseService

/** A [Screen] that provides an action to exit the car app.  */
class FinishAppScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder(carContext.getString(R.string.finish_app_msg))
            .setTitle(carContext.getString(R.string.finish_app_title))
            .setHeaderAction(Action.BACK)
            .addAction(
                Action.Builder()
                    .setOnClickListener {
                        carContext
                            .getSharedPreferences(
                                ShowcaseService.SHARED_PREF_KEY,
                                Context.MODE_PRIVATE
                            )
                            .edit()
                            .putBoolean(
                                ShowcaseService.PRE_SEED_KEY, true
                            )
                            .apply()
                        carContext.finishCarApp()
                    }
                    .setTitle(carContext.getString(R.string.exit_action_title))
                    .build())
            .build()
    }
}
