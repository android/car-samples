/*
 * Copyright 2021 The Android Open Source Project
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

import android.app.Activity
import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/**
 * A screen to demo the use of [CarContext.setCarAppResult]
 */
class ResultDemoScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val callingComponent = carContext.callingComponent
            ?: return MessageTemplate.Builder(
                carContext.getString(R.string.not_started_for_result_msg)
            )
                .setTitle(carContext.getString(R.string.result_demo_title))
                .setHeaderAction(Action.BACK)
                .build()

        return MessageTemplate.Builder(
            carContext.getString(
                R.string.started_for_result_msg,
                callingComponent.packageName
            )
        )
            .setTitle(carContext.getString(R.string.result_demo_title))
            .setHeaderAction(Action.BACK)
            .addAction(
                Action.Builder()
                    .setTitle("Okay (action = 'foo')")
                    .setOnClickListener {
                        carContext.setCarAppResult(
                            Activity.RESULT_OK,
                            Intent("foo")
                        )
                        carContext.finishCarApp()
                    }
                    .build())
            .addAction(
                Action.Builder()
                    .setTitle(carContext.getString(R.string.cancel_action_title))
                    .setOnClickListener {
                        carContext.setCarAppResult(Activity.RESULT_CANCELED, null)
                        carContext.finishCarApp()
                    }
                    .build())
            .build()
    }
}
