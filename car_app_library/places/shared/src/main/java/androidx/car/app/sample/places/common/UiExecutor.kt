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

import android.os.Handler
import android.os.Looper
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

/** An [ExecutorService] that wraps the main looper.  */
internal object UiExecutor {
    private val INSTANCE: ExecutorService = HandlerExecutorService(Handler(Looper.getMainLooper()))

    fun get(): ExecutorService {
        return INSTANCE
    }

    /**
     * A single-threaded [ExecutorService] that runs jobs on the thread represented by the
     * given [Handler]. If we're on the Handler's thread already, it will run the job
     * immediately. It does not support shutdowns, and will throw if they are invoked.
     */
    private class HandlerExecutorService(private val mHandler: Handler) :
        AbstractExecutorService() {
        override fun submit(task: Runnable): Future<*> {
            return submit<Any?>(task, null)
        }

        override fun <T> submit(task: Runnable, result: T): Future<T> {
            return submit(Executors.callable(task, result))
        }

        override fun <T> submit(task: Callable<T>): Future<T> {
            val futureTask = FutureTask(task)
            if (Looper.myLooper() == mHandler.looper) {
                // Already on the right thread, so execute normally.
                futureTask.run()
            } else {
                mHandler.post(futureTask)
            }
            return futureTask
        }

        override fun execute(runnable: Runnable) {
            mHandler.post(runnable)
        }

        override fun shutdown() {
            throw UnsupportedOperationException()
        }

        override fun shutdownNow(): List<Runnable> {
            throw UnsupportedOperationException()
        }

        override fun isShutdown(): Boolean {
            return false
        }

        override fun isTerminated(): Boolean {
            return false
        }

        @Throws(InterruptedException::class)
        override fun awaitTermination(l: Long, timeUnit: TimeUnit): Boolean {
            throw UnsupportedOperationException()
        }
    }
}
