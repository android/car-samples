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
package androidx.car.app.sample.showcase.common

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.OnClickListener
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.model.Toggle
import androidx.core.graphics.drawable.IconCompat

/** Screen for demonstrating task flow limitations.  */
class TaskRestrictionDemoScreen(private val mStep: Int, carContext: CarContext) :
    Screen(carContext) {
    private var mIsBackOperation = false
    private var mFirstToggleState = false
    private var mSecondToggleState = false
    private var mSecondToggleEnabled = false
    private var mImageType = Row.IMAGE_TYPE_ICON

    override fun onGetTemplate(): Template {
        // Last step must either be a PaneTemplate, MessageTemplate or NavigationTemplate.
        if (mStep == MAX_STEPS_ALLOWED) {
            val onClickListener = OnClickListener {
                screenManager
                    .pushForResult(
                        TaskRestrictionDemoScreen(
                            mStep + 1,
                            carContext
                        )
                    ) { _: Any? -> mIsBackOperation = true }
            }

            return MessageTemplate.Builder(
                carContext.getString(R.string.task_limit_reached_msg)
            )
                .setHeaderAction(Action.BACK)
                .addAction(
                    Action.Builder()
                        .setTitle(
                            carContext.getString(
                                R.string.try_anyway_action_title
                            )
                        )
                        .setOnClickListener(onClickListener)
                        .build()
                )
                .build()
        }

        val mFirstToggle = Toggle.Builder { checked: Boolean ->
            mSecondToggleEnabled = checked
            if (checked) {
                CarToast.makeText(
                    carContext, R.string.toggle_test_enabled,
                    CarToast.LENGTH_LONG
                ).show()
            } else {
                CarToast.makeText(
                    carContext, R.string.toggle_test_disabled,
                    CarToast.LENGTH_LONG
                ).show()
            }
            mFirstToggleState = !mFirstToggleState
            invalidate()
        }.setChecked(mFirstToggleState).build()

        val mSecondToggle = Toggle.Builder { _: Boolean ->
            mSecondToggleState = !mSecondToggleState
            invalidate()
        }.setChecked(mSecondToggleState).setEnabled(mSecondToggleEnabled).build()

        val builder = ItemList.Builder()
        builder.addItem(
            Row.Builder()
                .setTitle(
                    carContext.getString(
                        R.string.task_step_of_title,
                        mStep,
                        MAX_STEPS_ALLOWED
                    )
                )
                .addText(carContext.getString(R.string.task_step_of_text))
                .setOnClickListener {
                    screenManager
                        .pushForResult(
                            TaskRestrictionDemoScreen(
                                mStep + 1, carContext
                            )
                        ) { mIsBackOperation = true }
                }
                .build())
            .addItem(
                Row.Builder()
                    .setTitle(
                        carContext.getString(
                            R.string.toggle_test_first_toggle_title
                        )
                    )
                    .addText(
                        carContext.getString(
                            R.string.toggle_test_first_toggle_text
                        )
                    )
                    .setToggle(mFirstToggle)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(
                        carContext.getString(
                            R.string.toggle_test_second_toggle_title
                        )
                    )
                    .addText(
                        carContext.getString(
                            R.string.toggle_test_second_toggle_text
                        )
                    )
                    .setToggle(mSecondToggle)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(carContext.getString(R.string.image_test_title))
                    .addText(carContext.getString(R.string.image_test_text))
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_fastfood_yellow_48dp
                            )
                        )
                            .build(),
                        mImageType
                    )
                    .setOnClickListener {
                        mImageType =
                            if (mImageType == Row.IMAGE_TYPE_ICON
                            ) Row.IMAGE_TYPE_LARGE
                            else Row.IMAGE_TYPE_ICON
                        invalidate()
                    }
                    .build())

        if (mIsBackOperation) {
            builder.addItem(
                Row.Builder()
                    .setTitle(carContext.getString(R.string.additional_data_title))
                    .addText(carContext.getString(R.string.additional_data_text))
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setSingleList(builder.build())
            .setTitle(carContext.getString(R.string.task_restriction_demo_title))
            .setHeaderAction(Action.BACK)
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle(
                                carContext.getString(
                                    R.string.home_caps_action_title
                                )
                            )
                            .setOnClickListener { screenManager.popToRoot() }
                            .build())
                    .build())
            .build()
    }

    companion object {
        private const val MAX_STEPS_ALLOWED = 4
    }
}
