/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.google.android.libraries.car.app.samples.showcase.navigation.routing;

import static com.google.android.libraries.car.app.navigation.model.LaneDirection.SHAPE_NORMAL_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.LaneDirection.SHAPE_STRAIGHT;

import android.text.SpannableString;
import android.text.Spanned;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.CarIconSpan;
import com.google.android.libraries.car.app.model.DateTimeWithZone;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.model.OnClickListener;
import com.google.android.libraries.car.app.navigation.model.Lane;
import com.google.android.libraries.car.app.navigation.model.LaneDirection;
import com.google.android.libraries.car.app.navigation.model.Maneuver;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;
import com.google.android.libraries.car.app.samples.showcase.R;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/** A class that provides models for the routing demos. */
public abstract class RoutingDemoModels {

  /** Returns the current {@link Step} with information such as the cue text and images. */
  public static Step getCurrentStep(CarContext carContext) {
    // Create the cue text, and span the "520" text with a highway sign image.
    String currentStepCue = "Roy st 520";
    SpannableString currentStepCueWithImage = new SpannableString(currentStepCue);
    CarIconSpan highwaySign =
        CarIconSpan.create(
            CarIcon.of(IconCompat.createWithResource(carContext, R.drawable.ic_520)),
            CarIconSpan.ALIGN_CENTER);
    currentStepCueWithImage.setSpan(highwaySign, 7, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    CarIcon currentTurnIcon =
        CarIcon.of(IconCompat.createWithResource(carContext, R.drawable.arrow_right_turn));
    Maneuver currentManeuver =
        Maneuver.builder(Maneuver.TYPE_TURN_NORMAL_RIGHT).setIcon(currentTurnIcon).build();

    CarIcon lanesImage = CarIcon.of(IconCompat.createWithResource(carContext, R.drawable.lanes));

    Lane straightNormal =
        Lane.builder().addDirection(LaneDirection.create(SHAPE_STRAIGHT, false)).build();
    Lane rightHighlighted =
        Lane.builder().addDirection(LaneDirection.create(SHAPE_NORMAL_RIGHT, true)).build();

    return Step.builder(currentStepCueWithImage)
        .setManeuver(currentManeuver)
        .setLanesImage(lanesImage)
        .addLane(straightNormal)
        .addLane(straightNormal)
        .addLane(straightNormal)
        .addLane(straightNormal)
        .addLane(rightHighlighted)
        .build();
  }

  /** Returns the next {@link Step} with information such as the cue text and images. */
  public static Step getNextStep(CarContext carContext) {
    // Create the cue text, and span the "I5" text with an image.
    String nextStepCue = "I5 Aurora Ave N";
    SpannableString nextStepCueWithImage = new SpannableString(nextStepCue);
    CarIconSpan highwaySign =
        CarIconSpan.create(
            CarIcon.of(IconCompat.createWithResource(carContext, R.drawable.ic_i5)),
            CarIconSpan.ALIGN_CENTER);
    nextStepCueWithImage.setSpan(highwaySign, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    CarIcon nextTurnIcon =
        CarIcon.of(IconCompat.createWithResource(carContext, R.drawable.arrow_straight));
    Maneuver nextManeuver = Maneuver.builder(Maneuver.TYPE_STRAIGHT).setIcon(nextTurnIcon).build();

    return Step.builder(nextStepCueWithImage).setManeuver(nextManeuver).build();
  }

  /** Returns the action strip that contains a "bug report" button and "stop navigation" button. */
  public static ActionStrip getActionStrip(
      CarContext carContext, OnClickListener onStopNavigation) {
    return ActionStrip.builder()
        .addAction(
            Action.builder()
                .setOnClickListener(
                    () -> {
                      CarToast.makeText(carContext, "Bug reported!", CarToast.LENGTH_SHORT).show();
                    })
                .setIcon(
                    CarIcon.of(
                        IconCompat.createWithResource(carContext, R.drawable.ic_bug_report_24px)))
                .build())
        .addAction(Action.builder().setTitle("Stop").setOnClickListener(onStopNavigation).build())
        .build();
  }

  /** Returns the {@link TravelEstimate} with time and distance information. */
  public static TravelEstimate getTravelEstimate() {
    // Calculate the time to destination from the current time.
    long nowUtcMillis = System.currentTimeMillis();
    long timeToDestinationMillis = TimeUnit.HOURS.toMillis(1) + TimeUnit.MINUTES.toMillis(55);

    return TravelEstimate.builder(
            // The estimated distance to the destination.
            Distance.create(112, Distance.UNIT_KILOMETERS),

            // The estimated time until arriving at the destination.
            TimeUnit.MILLISECONDS.toSeconds(timeToDestinationMillis),

            // Arrival time at the destination with the destination time zone.
            DateTimeWithZone.create(
                nowUtcMillis + timeToDestinationMillis, TimeZone.getTimeZone("US/Eastern")))
        .setRemainingTimeColor(CarColor.YELLOW)
        .setRemainingDistanceColor(CarColor.RED)
        .build();
  }

  private RoutingDemoModels() {}
}
