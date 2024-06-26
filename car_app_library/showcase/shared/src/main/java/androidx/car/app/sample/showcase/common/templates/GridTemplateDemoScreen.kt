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
package androidx.car.app.sample.showcase.common.templates

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.min

/** Creates a screen that demonstrates usage of the full screen [GridTemplate].  */
class GridTemplateDemoScreen(carContext: CarContext) : Screen(carContext),
    DefaultLifecycleObserver {
    private val mHandler = Handler(Looper.getMainLooper())

    private var mImage: IconCompat? = null
    private var mIcon: IconCompat? = null
    private var mIsFourthItemLoading: Boolean
    private var mThirdItemToggleState: Boolean
    private var mFourthItemToggleState: Boolean
    private var mFifthItemToggleState: Boolean

    init {
        lifecycle.addObserver(this)
        mIsFourthItemLoading = false
        mThirdItemToggleState = false
        mFourthItemToggleState = true
        mFifthItemToggleState = false
    }

    override fun onCreate(owner: LifecycleOwner) {
        val resources = carContext.resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.test_image_square)
        mImage = IconCompat.createWithBitmap(bitmap)
        mIcon = IconCompat.createWithResource(carContext, R.drawable.ic_fastfood_white_48dp)
    }

    override fun onStart(owner: LifecycleOwner) {
        mIsFourthItemLoading = false

        // Post a message that starts loading the fourth item for some time.
        triggerFourthItemLoading()
    }

    private fun createGridItem(index: Int): GridItem {
        when (index) {
            0 ->                 // Grid item with an icon and a title.
                return GridItem.Builder()
                    .setImage(CarIcon.Builder(mIcon!!).build(), GridItem.IMAGE_TYPE_ICON)
                    .setTitle(carContext.getString(R.string.non_actionable))
                    .build()

            1 ->                 // Grid item with an icon, a title, onClickListener and no text.
                return GridItem.Builder()
                    .setImage(CarIcon.Builder(mIcon!!).build(), GridItem.IMAGE_TYPE_ICON)
                    .setTitle(carContext.getString(R.string.second_item))
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext
                                .getString(R.string.second_item_toast_msg),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .build()

            2 ->                 // Grid item with an icon marked as icon, a title, a text and a toggle in
                // unchecked state.
                return GridItem.Builder()
                    .setImage(CarIcon.Builder(mIcon!!).build(), GridItem.IMAGE_TYPE_ICON)
                    .setTitle(carContext.getString(R.string.third_item))
                    .setText(
                        if (mThirdItemToggleState
                        ) carContext.getString(R.string.checked_action_title)
                        else carContext.getString(R.string.unchecked_action_title)
                    )
                    .setOnClickListener {
                        mThirdItemToggleState = !mThirdItemToggleState
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.third_item_checked_toast_msg
                            )
                                    + ": " + mThirdItemToggleState,
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                        invalidate()
                    }
                    .build()

            3 ->                 // Grid item with an image, a title, a long text and a toggle that takes some
                // time to
                // update.
                return if (mIsFourthItemLoading) {
                    GridItem.Builder()
                        .setTitle(carContext.getString(R.string.fourth_item))
                        .setText(
                            if (mFourthItemToggleState
                            ) carContext.getString(R.string.on_action_title)
                            else carContext.getString(R.string.off_action_title)
                        )
                        .setLoading(true)
                        .build()
                } else {
                    GridItem.Builder()
                        .setImage(CarIcon.Builder(mImage!!).build())
                        .setTitle(carContext.getString(R.string.fourth_item))
                        .setText(
                            if (mFourthItemToggleState
                            ) carContext.getString(R.string.on_action_title)
                            else carContext.getString(R.string.off_action_title)
                        )
                        .setOnClickListener { this.triggerFourthItemLoading() }
                        .build()
                }

            4 ->                 // Grid item with a large image, a long title, no text and a toggle in unchecked
                // state.
                return GridItem.Builder()
                    .setImage(CarIcon.Builder(mImage!!).build(), GridItem.IMAGE_TYPE_LARGE)
                    .setTitle(carContext.getString(R.string.fifth_item))
                    .setOnClickListener {
                        mFifthItemToggleState = !mFifthItemToggleState
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.fifth_item_checked_toast_msg
                            )
                                    + ": "
                                    + mFifthItemToggleState,
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                        invalidate()
                    }
                    .build()

            5 ->                 // Grid item with an image marked as an icon, a long title, a long text and
                // onClickListener.
                return GridItem.Builder()
                    .setImage(
                        CarIcon.Builder(mIcon!!).build(),
                        GridItem.IMAGE_TYPE_ICON
                    )
                    .setTitle(carContext.getString(R.string.sixth_item))
                    .setText(carContext.getString(R.string.sixth_item))
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.sixth_item_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .build()

            else -> {
                val titleText = (index + 1).toString() + "th item"
                val toastText = "Clicked " + (index + 1) + "th item"

                return GridItem.Builder()
                    .setImage(
                        CarIcon.Builder(mIcon!!).build(),
                        GridItem.IMAGE_TYPE_ICON
                    )
                    .setTitle(titleText)
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            toastText,
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .build()
            }
        }
    }

    override fun onGetTemplate(): Template {
        var itemLimit = 6
        // Adjust the item limit according to the car constrains.
        if (carContext.carAppApiLevel > CarAppApiLevels.LEVEL_1) {
            itemLimit = min(
                MAX_GRID_ITEMS.toDouble(),
                carContext.getCarService(ConstraintManager::class.java)
                    .getContentLimit(
                        ConstraintManager.CONTENT_LIMIT_TYPE_GRID
                    ).toDouble()
            ).toInt()
        }

        val gridItemListBuilder = ItemList.Builder()
        for (i in 0..itemLimit) {
            gridItemListBuilder.addItem(createGridItem(i))
        }

        val settings = Action.Builder()
            .setTitle(
                carContext.getString(
                    R.string.settings_action_title
                )
            )
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.settings_toast_msg),
                    CarToast.LENGTH_SHORT
                )
                    .show()
            }
            .build()
        return GridTemplate.Builder()
            .setHeaderAction(Action.APP_ICON)
            .setSingleList(gridItemListBuilder.build())
            .setTitle(carContext.getString(R.string.grid_template_demo_title))
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(settings)
                    .build()
            )
            .setHeaderAction(Action.BACK)
            .build()
    }

    /**
     * Changes the fourth item to a loading state for some time and changes it back to the loaded
     * state.
     */
    private fun triggerFourthItemLoading() {
        mHandler.post {
            mIsFourthItemLoading = true
            invalidate()
            mHandler.postDelayed(
                {
                    mIsFourthItemLoading = false
                    mFourthItemToggleState = !mFourthItemToggleState
                    invalidate()
                },
                LOADING_TIME_MILLIS.toLong()
            )
        }
    }

    companion object {
        private const val MAX_GRID_ITEMS = 100
        private const val LOADING_TIME_MILLIS = 2000
    }
}
