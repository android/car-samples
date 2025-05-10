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

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.ThreadFactoryBuilder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** Holder for executors used in the app.  */
internal object Executors {
    /** An executor used to run the queries to fetch place data outside of the UI thread.  */
    val BACKGROUND_EXECUTOR: ListeningExecutorService = MoreExecutors.listeningDecorator(
        Executors.newSingleThreadExecutor(
            ThreadFactoryBuilder().setNameFormat("places-demo-%d").build()
        )
    )

    /** An executor that runs its tasks in the UI thread.  */
    val UI_EXECUTOR: ExecutorService? = UiExecutor.get()
}
