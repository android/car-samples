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

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.car.app.notification.CarAppExtender
import androidx.car.app.notification.CarNotificationManager
import androidx.car.app.notification.CarPendingIntent
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.sample.showcase.common.ShowcaseService
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.TimeUnit

/** A simple screen that demonstrates how to use notifications in a car app.  */
class NotificationDemoScreen(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    val mHandler: Handler = Handler(Looper.getMainLooper(), HandlerCallback())

    private val mIcon = IconCompat.createWithResource(
        getCarContext(),
        R.drawable.ic_face_24px
    )

    /** A broadcast receiver that can show a toast message upon receiving a broadcast.  */
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            CarToast.makeText(
                getCarContext(),
                getCarContext().getString(R.string.triggered_toast_msg) + ": "
                        + intent.action,
                CarToast.LENGTH_SHORT
            )
                .show()
        }
    }
    private var mImportance = NotificationManager.IMPORTANCE_DEFAULT
    private var mIsNavCategory = false
    private var mSetOngoing = false
    private var mNotificationCount = 0

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        registerBroadcastReceiver()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mHandler.removeMessages(MSG_SEND_NOTIFICATION)
        CarNotificationManager.from(carContext).cancelAll()
        unregisterBroadcastReceiver()
    }

    override fun onGetTemplate(): Template {
        val listBuilder = ItemList.Builder()

        // Send a single notification with the settings configured by other buttons.
        listBuilder.addItem(
            GridItem.Builder()
                .setTitle(carContext.getString(R.string.send_notification_title))
                .setImage(CarIcon.Builder(mIcon).build())
                .setOnClickListener { this.sendNotification() }
                .build())

        // Start a repeating notification with the settings configured by other buttons.
        listBuilder.addItem(
            GridItem.Builder()
                .setTitle(carContext.getString(R.string.start_notifications_title))
                .setImage(CarIcon.Builder(mIcon).build())
                .setOnClickListener {
                    mHandler.sendMessage(
                        mHandler.obtainMessage(MSG_SEND_NOTIFICATION)
                    )
                }
                .build())

        // Stop the repeating notification and reset the count.
        listBuilder.addItem(
            GridItem.Builder()
                .setTitle(carContext.getString(R.string.stop_notifications_title))
                .setImage(CarIcon.Builder(mIcon).build())
                .setOnClickListener {
                    mHandler.removeMessages(MSG_SEND_NOTIFICATION)
                    CarNotificationManager.from(carContext).cancelAll()
                    mNotificationCount = 0
                }
                .build())

        // Configure the notification importance.
        listBuilder.addItem(
            GridItem.Builder()
                .setImage(CarIcon.Builder(mIcon).build())
                .setTitle(carContext.getString(R.string.importance_title))
                .setText(importanceString)
                .setOnClickListener {
                    setImportance()
                    invalidate()
                }
                .build())

        // Configure whether the notification's category is navigation.
        listBuilder.addItem(
            GridItem.Builder()
                .setImage(CarIcon.Builder(mIcon).build())
                .setTitle(carContext.getString(R.string.category_title))
                .setText(categoryString)
                .setOnClickListener {
                    mIsNavCategory = !mIsNavCategory
                    invalidate()
                }
                .build())

        // Configure whether the notification is an ongoing notification.
        listBuilder.addItem(
            GridItem.Builder()
                .setImage(CarIcon.Builder(mIcon).build())
                .setTitle(carContext.getString(R.string.ongoing_title))
                .setText(mSetOngoing.toString())
                .setOnClickListener {
                    mSetOngoing = !mSetOngoing
                    invalidate()
                }
                .build())

        return GridTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(carContext.getString(R.string.notification_demo))
            .setHeaderAction(Action.BACK)
            .build()
    }

    fun sendNotification() {
        mNotificationCount++
        val title =
            (carContext.getString(R.string.notification_title) + ": "
                    + importanceString + ", " + mNotificationCount)
        val text = (carContext.getString(R.string.category_title) + ": "
                + categoryString + ", "
                + carContext.getString(R.string.ongoing_title) + ": " + mSetOngoing)

        when (mImportance) {
            NotificationManager.IMPORTANCE_HIGH -> sendNotification(
                title, text, NOTIFICATION_CHANNEL_HIGH_ID,
                NOTIFICATION_CHANNEL_HIGH_NAME, NOTIFICATION_ID,
                NotificationManager.IMPORTANCE_HIGH
            )

            NotificationManager.IMPORTANCE_DEFAULT -> sendNotification(
                title, text, NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                NOTIFICATION_ID, NotificationManager.IMPORTANCE_DEFAULT
            )

            NotificationManager.IMPORTANCE_LOW -> sendNotification(
                title, text, NOTIFICATION_CHANNEL_LOW_ID,
                NOTIFICATION_CHANNEL_LOW_NAME, NOTIFICATION_ID,
                NotificationManager.IMPORTANCE_LOW
            )

            else -> {}
        }
    }

    // Suppressing 'ObsoleteSdkInt' as this code is shared between APKs with different min SDK
    // levels
    @SuppressLint("ObsoleteSdkInt")
    private fun sendNotification(
        title: CharSequence, text: CharSequence, channelId: String,
        channelName: CharSequence, notificationId: Int, importance: Int
    ) {
        val carNotificationManager =
            CarNotificationManager.from(carContext)

        val channel = NotificationChannelCompat.Builder(
            channelId,
            importance
        ).setName(channelName).build()
        carNotificationManager.createNotificationChannel(channel)
        val builder = NotificationCompat.Builder(carContext, channelId)
        if (mIsNavCategory) {
            builder.setCategory(NotificationCompat.CATEGORY_NAVIGATION)
        }
        builder.setOngoing(mSetOngoing)

        builder.setSmallIcon(R.drawable.ic_bug_report_24px)
            .setContentTitle("$title (phone)")
            .setContentText("$text (phone)")
            .setColor(carContext.getColor(androidx.car.app.R.color.carColorGreen))
            .setColorized(true)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    carContext.resources, R.drawable.ic_hi
                )
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_face_24px,
                    "Action1 (phone)",
                    createPendingIntent(INTENT_ACTION_PRIMARY_PHONE)
                )
                    .build()
            )
            .addAction(
                R.drawable.ic_commute_24px,
                "Action2 (phone)",
                createPendingIntent(INTENT_ACTION_SECONDARY_PHONE)
            )
            .extend(
                CarAppExtender.Builder()
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(
                        CarPendingIntent.getCarApp(
                            carContext, 0,
                            Intent(Intent.ACTION_VIEW).setComponent(
                                ComponentName(
                                    carContext,
                                    ShowcaseService::class.java
                                )
                            ), 0
                        )
                    )
                    .setColor(CarColor.PRIMARY)
                    .setSmallIcon(R.drawable.ic_bug_report_24px)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            carContext.resources,
                            R.drawable.ic_hi
                        )
                    )
                    .addAction(
                        R.drawable.ic_commute_24px,
                        carContext.getString(R.string.navigate),
                        pendingIntentForNavigation
                    )
                    .addAction(
                        R.drawable.ic_face_24px,
                        carContext.getString(R.string.call_action_title),
                        createPendingIntentForCall()
                    )
                    .build()
            )

        carNotificationManager.notify(notificationId, builder)
    }

    private fun createPendingIntentForCall(): PendingIntent {
        val intent = Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:+14257232350"))
        return CarPendingIntent.getCarApp(carContext, intent.hashCode(), intent, 0)
    }

    private val pendingIntentForNavigation: PendingIntent
        get() {
            val intent = Intent(CarContext.ACTION_NAVIGATE).setData(Uri.parse("geo:0,0?q=Home"))
            return CarPendingIntent.getCarApp(carContext, intent.hashCode(), intent, 0)
        }

    private val importanceString: String
        get() = when (mImportance) {
            NotificationManager.IMPORTANCE_HIGH -> carContext.getString(R.string.high_importance)
            NotificationManager.IMPORTANCE_DEFAULT -> carContext.getString(R.string.default_importance)
            NotificationManager.IMPORTANCE_LOW -> carContext.getString(R.string.low_importance)
            else -> carContext.getString(R.string.unknown_importance)
        }

    private val categoryString: String
        get() = if (mIsNavCategory) "Navigation" else "None"

    /**
     * Change the notification importance in a rotating sequence:
     * Low -> Default -> High -> Low...
     */
    private fun setImportance() {
        when (mImportance) {
            NotificationManager.IMPORTANCE_HIGH -> mImportance = NotificationManager.IMPORTANCE_LOW
            NotificationManager.IMPORTANCE_DEFAULT -> mImportance =
                NotificationManager.IMPORTANCE_HIGH

            NotificationManager.IMPORTANCE_LOW -> mImportance =
                NotificationManager.IMPORTANCE_DEFAULT

            else -> {}
        }
    }

    private fun registerBroadcastReceiver() {
        val filter = IntentFilter()
        filter.addAction(INTENT_ACTION_PRIMARY_PHONE)
        filter.addAction(INTENT_ACTION_SECONDARY_PHONE)

        carContext.registerReceiver(mBroadcastReceiver, filter)
    }

    private fun unregisterBroadcastReceiver() {
        carContext.unregisterReceiver(mBroadcastReceiver)
    }

    /** Returns a pending intent with the provided intent action.  */
    private fun createPendingIntent(intentAction: String): PendingIntent {
        val intent = Intent(intentAction)
        return PendingIntent.getBroadcast(
            carContext, intentAction.hashCode(), intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    internal inner class HandlerCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            if (msg.what == MSG_SEND_NOTIFICATION) {
                sendNotification()
                mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MSG_SEND_NOTIFICATION),
                    NOTIFICATION_DELAY_IN_MILLIS
                )
                return true
            }
            return false
        }
    }

    companion object {
        val NOTIFICATION_DELAY_IN_MILLIS: Long = TimeUnit.SECONDS.toMillis(1)
        private const val NOTIFICATION_CHANNEL_ID = "channel_00"
        private val NOTIFICATION_CHANNEL_NAME: CharSequence = "Default Channel"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_CHANNEL_HIGH_ID = "channel_01"
        private val NOTIFICATION_CHANNEL_HIGH_NAME: CharSequence = "High Channel"
        private const val NOTIFICATION_CHANNEL_LOW_ID = "channel_02"
        private val NOTIFICATION_CHANNEL_LOW_NAME: CharSequence = "Low Channel"
        private const val INTENT_ACTION_PRIMARY_PHONE =
            "androidx.car.app.sample.showcase.common.INTENT_ACTION_PRIMARY_PHONE"
        private const val INTENT_ACTION_SECONDARY_PHONE =
            "androidx.car.app.sample.showcase.common.INTENT_ACTION_SECONDARY_PHONE"
        private const val MSG_SEND_NOTIFICATION = 1
    }
}
