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
package androidx.car.app.sample.showcase.common.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R

/** A simple screen that demonstrates how to use navigation notifications in a car app.  */
class NavigationNotificationsDemoScreen(carContext: CarContext) : Screen(carContext) {
    // Suppressing 'ObsoleteSdkInt' as this code is shared between APKs with different min SDK
    // levels
    @SuppressLint("ObsoleteSdkInt")
    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.start_notification_title))
                .setOnClickListener {
                    val context: Context = carContext
                    val intent =
                        Intent(
                            context, NavigationNotificationService::class.java
                        )
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
                .build())

        listBuilder.addItem(
            Row.Builder()
                .setTitle(carContext.getString(R.string.stop_notification_title))
                .setOnClickListener {
                    carContext
                        .stopService(
                            Intent(
                                carContext,
                                NavigationNotificationService::class.java
                            )
                        )
                }
                .build())

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.nav_notification_demo_title))
            .setHeaderAction(Action.BACK)
            .build()
    }
}
