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
package androidx.car.app.sample.showcase.common.navigation.routing

import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Alert
import androidx.car.app.model.AlertCallback
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarIconSpan
import androidx.car.app.model.CarText
import androidx.car.app.model.DateTimeWithZone
import androidx.car.app.model.Distance
import androidx.car.app.model.OnClickListener
import androidx.car.app.navigation.model.Lane
import androidx.car.app.navigation.model.LaneDirection
import androidx.car.app.navigation.model.Maneuver
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.sample.showcase.common.R
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/** A class that provides models for the routing demos.  */
object RoutingDemoModels {
    /** Returns a sample [Alert].  */
    private fun createAlert(carContext: CarContext): Alert {
        val title =
            CarText.create(carContext.getString(R.string.navigation_alert_title))
        val subtitle =
            CarText.create(carContext.getString(R.string.navigation_alert_subtitle))
        val icon = CarIcon.ALERT

        val yesTitle = CarText.create(carContext.getString(R.string.yes_action_title))
        val yesAction = Action.Builder().setTitle(yesTitle).setOnClickListener {
            CarToast.makeText(
                carContext,
                carContext.getString(
                    R.string.yes_action_toast_msg
                ),
                CarToast.LENGTH_SHORT
            )
                .show()
        }.setFlags(Action.FLAG_PRIMARY).build()

        val noTitle = CarText.create(carContext.getString(R.string.no_action_title))
        val noAction = Action.Builder().setTitle(noTitle).setOnClickListener {
            CarToast.makeText(
                carContext,
                carContext.getString(
                    R.string.no_action_toast_msg
                ),
                CarToast.LENGTH_SHORT
            )
                .show()
        }.setFlags(Action.FLAG_DEFAULT).build()

        return Alert.Builder( /* alertId: */0, title,  /* durationMillis: */10000)
            .setSubtitle(subtitle)
            .setIcon(icon)
            .addAction(yesAction)
            .addAction(noAction).setCallback(object : AlertCallback {
                override fun onCancel(reason: Int) {
                    if (reason == AlertCallback.REASON_TIMEOUT) {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.alert_timeout_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onDismiss() {
                }
            }).build()
    }

    /** Returns the current [Step] with information such as the cue text and images.  */
    fun getCurrentStep(carContext: CarContext): Step {
        // Create the cue text, and span the "520" text with a highway sign image.
        val currentStepCue = carContext.getString(R.string.current_step_cue)
        val currentStepCueWithImage = SpannableString(currentStepCue)
        val highwaySign =
            CarIconSpan.create(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext, R.drawable.ic_520
                    )
                )
                    .build(),
                CarIconSpan.ALIGN_CENTER
            )
        currentStepCueWithImage.setSpan(highwaySign, 7, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

        val currentTurnIcon =
            CarIcon.Builder(
                IconCompat.createWithResource(
                    carContext, R.drawable.arrow_right_turn
                )
            )
                .build()
        val currentManeuver =
            Maneuver.Builder(Maneuver.TYPE_TURN_NORMAL_RIGHT)
                .setIcon(currentTurnIcon)
                .build()

        val lanesImage =
            CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.lanes))
                .build()

        val straightNormal =
            Lane.Builder()
                .addDirection(LaneDirection.create(LaneDirection.SHAPE_STRAIGHT, false))
                .build()
        val rightHighlighted =
            Lane.Builder()
                .addDirection(LaneDirection.create(LaneDirection.SHAPE_NORMAL_RIGHT, true))
                .build()

        return Step.Builder(currentStepCueWithImage)
            .setManeuver(currentManeuver)
            .setLanesImage(lanesImage)
            .addLane(straightNormal)
            .addLane(straightNormal)
            .addLane(straightNormal)
            .addLane(straightNormal)
            .addLane(rightHighlighted)
            .build()
    }

    /** Returns the next [Step] with information such as the cue text and images.  */
    fun getNextStep(carContext: CarContext): Step {
        // Create the cue text, and span the "I5" text with an image.
        val nextStepCue = carContext.getString(R.string.next_step_cue)
        val nextStepCueWithImage = SpannableString(nextStepCue)
        val highwaySign =
            CarIconSpan.create(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, R.drawable.ic_i5)
                )
                    .build(),
                CarIconSpan.ALIGN_CENTER
            )
        nextStepCueWithImage.setSpan(highwaySign, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val nextTurnIcon =
            CarIcon.Builder(
                IconCompat.createWithResource(
                    carContext, R.drawable.arrow_straight
                )
            )
                .build()
        val nextManeuver =
            Maneuver.Builder(Maneuver.TYPE_STRAIGHT).setIcon(nextTurnIcon).build()

        return Step.Builder(nextStepCueWithImage).setManeuver(nextManeuver).build()
    }

    /**
     * Returns the action strip that contains a "bug report" button and "stop navigation" button.
     */
    fun getActionStrip(
        carContext: CarContext, onStopNavigation: OnClickListener
    ): ActionStrip {
        val builder = ActionStrip.Builder()
        if (carContext.carAppApiLevel >= CarAppApiLevels.LEVEL_5) {
            builder.addAction(
                Action.Builder()
                    .setOnClickListener {
                        carContext.getCarService(AppManager::class.java)
                            .showAlert(createAlert(carContext))
                    }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_baseline_add_alert_24
                            )
                        )
                            .build()
                    )
                    .build())
        }
        builder.addAction(
            Action.Builder()
                .setOnClickListener {
                    CarToast.makeText(
                        carContext,
                        carContext.getString(
                            R.string.bug_reported_toast_msg
                        ),
                        CarToast.LENGTH_SHORT
                    )
                        .show()
                }
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_bug_report_24px
                        )
                    )
                        .build()
                )
                .build())
        builder.addAction(
            Action.Builder()
                .setTitle(carContext.getString(R.string.stop_action_title))
                .setOnClickListener(onStopNavigation)
                .setFlags(Action.FLAG_IS_PERSISTENT)
                .build()
        )
        return builder.build()
    }

    /**
     * Returns the map action strip that contains pan and zoom buttons.
     */
    fun getMapActionStrip(
        carContext: CarContext
    ): ActionStrip {
        return ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.zoomed_in_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_zoom_in_24
                            )
                        )
                            .build()
                    )
                    .build())
            .addAction(
                Action.Builder()
                    .setOnClickListener {
                        CarToast.makeText(
                            carContext,
                            carContext.getString(
                                R.string.zoomed_out_toast_msg
                            ),
                            CarToast.LENGTH_SHORT
                        )
                            .show()
                    }
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_zoom_out_24
                            )
                        )
                            .build()
                    )
                    .build())
            .addAction(Action.PAN)
            .build()
    }

    /** Returns the [TravelEstimate] with time and distance information.  */
    fun getTravelEstimate(carContext: CarContext): TravelEstimate {
        // Calculate the time to destination from the current time.
        val nowUtcMillis = System.currentTimeMillis()
        val timeToDestinationMillis = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(55)

        return TravelEstimate.Builder( // The estimated distance to the destination.
            Distance.create(
                112.0,
                Distance.UNIT_KILOMETERS
            ),  // Arrival time at the destination with the destination time zone.

            DateTimeWithZone.create(
                nowUtcMillis + timeToDestinationMillis,
                TimeZone.getTimeZone("US/Eastern")
            )
        )
            .setRemainingTimeSeconds(TimeUnit.MILLISECONDS.toSeconds(timeToDestinationMillis))
            .setRemainingTimeColor(CarColor.YELLOW)
            .setRemainingDistanceColor(CarColor.RED)
            .setTripText(CarText.create(carContext.getString(R.string.travel_est_trip_text)))
            .setTripIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        R.drawable.ic_face_24px
                    )
                )
                    .build()
            )
            .build()
    }
}
