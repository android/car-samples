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

package com.google.android.libraries.car.app.samples.navigation.model;

import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_DEPART;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_DESTINATION;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_DESTINATION_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_DESTINATION_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_DESTINATION_STRAIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_FERRY_BOAT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_FERRY_TRAIN;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_FORK_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_FORK_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_KEEP_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_KEEP_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_MERGE_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_MERGE_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_MERGE_SIDE_UNSPECIFIED;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_NAME_CHANGE;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_OFF_RAMP_NORMAL_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_OFF_RAMP_NORMAL_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_OFF_RAMP_SLIGHT_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_OFF_RAMP_SLIGHT_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_NORMAL_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_NORMAL_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_SHARP_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_SHARP_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_SLIGHT_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_SLIGHT_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_U_TURN_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ON_RAMP_U_TURN_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW_WITH_ANGLE;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_ROUNDABOUT_EXIT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_STRAIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_NORMAL_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_NORMAL_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_SHARP_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_SHARP_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_SLIGHT_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_TURN_SLIGHT_RIGHT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_UNKNOWN;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_U_TURN_LEFT;
import static com.google.android.libraries.car.app.navigation.model.Maneuver.TYPE_U_TURN_RIGHT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.DateTimeWithZone;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.navigation.model.Destination;
import com.google.android.libraries.car.app.navigation.model.Maneuver;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;
import com.google.android.libraries.car.app.samples.navigation.R;
import com.google.android.libraries.car.app.samples.navigation.model.Instruction.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Example scripts to "control" navigation within the app.
 *
 * <p>The script takes the form of a list of instructions which can be passed to different parts of
 * the app. This is the central location where scripts are stored.
 *
 * <p>The scripts start with a setup phase where all destinations and steps are added to the
 * instruction list. Then navigation updates are added for each step to simulate driving.
 *
 * <p>>The setup phases consists of:
 *
 * <ul>
 *   <li>Start Navigation
 *   <li>Add the destination information.
 *   <li>Add 4 intermediate steps to the destination.
 * </ul>
 *
 * <p>The navigation phase consists of
 *
 * <ul>
 *   <li>Add positions along the route getting closer to the step.
 *   <li>Once the step is reached, pop the step. If more steps remain go back to adding more
 *       positions.
 *   <li>When no more steps remain set the arrived state.
 *   <li>End Navigation
 * </ul>
 *
 * <p>There are several helper functions including {@link #generateTripUpdateSequence} which
 * interpolates a straight path and generates all the updates for a step.
 */
public class DemoScripts {

  private static long INSTRUCTION_NO_ELAPSED_TIME = 0;
  /**
   * Create instructions for home.
   */
  public static List<Instruction> getNavigateHome(CarContext carContext) {
    ArrayList<Instruction> instructions = new ArrayList<>();

    DateTimeWithZone arrivalTimeAtDestination = getCurrentDateTimeZoneWithOffset(30);

    Step step1 =
        Step.builder("State Street")
            .setManeuver(
                getManeuverWithExitNumberAndAngle(
                    carContext, Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE, 2, 270))
            .setRoad("State Street")
            .build();
    Step step2 =
        Step.builder("Kirkland Way")
            .setManeuver(getManeuver(carContext, Maneuver.TYPE_TURN_NORMAL_LEFT))
            .setRoad("Kirkland Way")
            .build();
    Step step3 =
        Step.builder("6th Street.")
            .setManeuver(getManeuver(carContext, Maneuver.TYPE_TURN_NORMAL_RIGHT))
            .setRoad("6th Street.")
            .build();
    Step step4 =
        Step.builder("Google Kirkland.")
            .setManeuver(getManeuver(carContext, TYPE_DESTINATION_RIGHT))
            .setRoad("Google Kirkland.")
            .build();

    // Start the navigation and add destination and steps.
    instructions.add(
        Instruction.builder(Instruction.Type.START_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .build());

    Destination destination = Destination.builder("Work", "747 6th St.").build();
    instructions.add(
        Instruction.builder(
                Instruction.Type.ADD_DESTINATION_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .setDestination(destination)
            .build());

    instructions.add(
        Instruction.builder(Instruction.Type.SET_REROUTING, TimeUnit.SECONDS.toMillis(5))
            .setDestinationTravelEstimate(
                TravelEstimate.create(
                    Distance.create(350, Distance.UNIT_METERS),
                    /* remainingSeconds= */ 350 / 10,
                    arrivalTimeAtDestination))
            .build());

    instructions.add(
        Instruction.builder(Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .setStep(step1)
            .build());
    instructions.add(
        Instruction.builder(Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .setStep(step2)
            .build());
    instructions.add(
        Instruction.builder(Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .setStep(step3)
            .build());
    instructions.add(
        Instruction.builder(Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .setStep(step4)
            .build());

    // Add trip positions for each step.
    instructions.addAll(
        generateTripUpdateSequence(
            /* count= */ 4,
            /* startDestinationDistanceRemaining= */ 350,
            /* startStepDistanceRemaining= */ 100,
            arrivalTimeAtDestination,
            "3rd Street",
            /* speed= */ 10));
    instructions.add(Instruction.builder(Instruction.Type.POP_STEP_NAVIGATION, 0).build());

    instructions.addAll(
        generateTripUpdateSequence(
            /* count= */ 6,
            /* startDestinationDistanceRemaining= */ 250,
            /* startStepDistanceRemaining= */ 150,
            arrivalTimeAtDestination,
            "State Street",
            /* speed= */ 10));
    instructions.add(
        Instruction.builder(Instruction.Type.POP_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME)
            .build());
    instructions.addAll(
        generateTripUpdateSequence(
            /* count= */ 4,
            /* startDestinationDistanceRemaining= */ 100,
            /* startStepDistanceRemaining= */ 100,
            arrivalTimeAtDestination,
            "6th Street",
            /* speed= */ 10));

    // Set arrived state and then stop navigation.
    instructions.add(
        Instruction.builder(Instruction.Type.SET_ARRIVED, TimeUnit.SECONDS.toMillis(5)).build());

    instructions.add(
        Instruction.builder(Type.POP_DESTINATION_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME).build());
    instructions.add(
        Instruction.builder(Instruction.Type.END_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME).build());
    return instructions;
  }

  private static DateTimeWithZone getCurrentDateTimeZoneWithOffset(int offsetSeconds) {
    GregorianCalendar startTime = new GregorianCalendar();
    GregorianCalendar destinationETA = (GregorianCalendar) startTime.clone();
    destinationETA.add(Calendar.SECOND, offsetSeconds);
    return getDateTimeZone(destinationETA);
  }

  /** Convenience function to create the date formmat. */
  private static DateTimeWithZone getDateTimeZone(GregorianCalendar calendar) {
    Date date = calendar.getTime();
    TimeZone timeZone = calendar.getTimeZone();

    long timeSinceEpochMillis = date.getTime();
    long timeZoneOffsetSeconds = MILLISECONDS.toSeconds(timeZone.getOffset(timeSinceEpochMillis));
    String zoneShortName = "PST";

    return DateTimeWithZone.create(
        timeSinceEpochMillis, (int) timeZoneOffsetSeconds, zoneShortName);
  }

  /**
   * Generates all the updates for a particular step interpolating along a straight line.
   *
   * @param count number of instructions to generate until the next step
   * @param startDestinationDistanceRemaining the distance until the final destination at the start
   *     of the sequence
   * @param startStepDistanceRemaining the distance until the next step at the start of the sequence
   * @param arrivalTimeAtDestination the arrival time at the destination
   * @param road the name of the road currently being travelled
   * @param speed meters/second being traveled
   * @return sequence of instructions until the next step
   */
  private static List<Instruction> generateTripUpdateSequence(
      int count,
      int startDestinationDistanceRemaining,
      int startStepDistanceRemaining,
      DateTimeWithZone arrivalTimeAtDestination,
      String road,
      int speed) {
    List<Instruction> sequence = new ArrayList<>(count);
    int destinationDistanceRemaining = startDestinationDistanceRemaining;
    int stepDistanceRemaining = startStepDistanceRemaining;
    int distanceIncrement = startStepDistanceRemaining / count;

    for (int i = 0; i < count; i++) {
      Distance remainingDistance = Distance.create(stepDistanceRemaining, Distance.UNIT_METERS);
      TravelEstimate destinationTravelEstimate =
          TravelEstimate.create(
              Distance.create(destinationDistanceRemaining, Distance.UNIT_METERS),
              /* remainingSeconds= */ destinationDistanceRemaining / speed,
              arrivalTimeAtDestination);
      TravelEstimate stepTravelEstimate =
          TravelEstimate.create(
              remainingDistance,
              /* timeToStep= */ distanceIncrement,
              getCurrentDateTimeZoneWithOffset(distanceIncrement));
      sequence.add(
          Instruction.builder(
                  Instruction.Type.SET_TRIP_POSITION_NAVIGATION,
                  TimeUnit.SECONDS.toMillis(distanceIncrement / speed))
              .setStepRemainingDistance(remainingDistance)
              .setStepTravelEstimate(stepTravelEstimate)
              .setDestinationTravelEstimate(destinationTravelEstimate)
              .setRoad(road)
              .build());

      destinationDistanceRemaining -= distanceIncrement;
      stepDistanceRemaining -= distanceIncrement;
    }
    return sequence;
  }

  /** Returns a maneuver with image selected from resources. */
  private static Maneuver getManeuver(CarContext carContext, int type) {
    return Maneuver.builder(type).setIcon(getTurnIcon(carContext, type)).build();
  }

  /** Returns a maneuver that includes an exit number with image selected from resources. */
  private static Maneuver getManeuverWithExitNumber(
      CarContext carContext, int type, int exitNumber) {
    return Maneuver.builder(type)
        .setIcon(getTurnIcon(carContext, type))
        .setRoundaboutExitNumber(exitNumber)
        .build();
  }

  /**
   * Returns a maneuver that includes an exit number and angle with image selected from resources.
   */
  private static Maneuver getManeuverWithExitNumberAndAngle(
      CarContext carContext, int type, int exitNumber, int exitAngle) {
    return Maneuver.builder(type)
        .setIcon(getTurnIcon(carContext, type))
        .setRoundaboutExitNumber(exitNumber)
        .setRoundaboutExitAngle(exitAngle)
        .build();
  }

  /** Generates a {@link CarIcon} representing the turn. */
  private static CarIcon getTurnIcon(CarContext carContext, int type) {
    int resourceId = R.drawable.ic_launcher;
    switch (type) {
      case TYPE_TURN_NORMAL_LEFT:
        resourceId = R.drawable.ic_turn_normal_left;
        break;
      case TYPE_TURN_NORMAL_RIGHT:
        resourceId = R.drawable.ic_turn_normal_right;
        break;
      case TYPE_UNKNOWN:
      case TYPE_DEPART:
      case TYPE_STRAIGHT:
        resourceId = R.drawable.ic_turn_name_change;
        break;
      case TYPE_DESTINATION:
      case TYPE_DESTINATION_STRAIGHT:
      case TYPE_DESTINATION_RIGHT:
      case TYPE_DESTINATION_LEFT:
        resourceId = R.drawable.ic_turn_destination;
        break;
      case TYPE_NAME_CHANGE:
        resourceId = R.drawable.ic_turn_name_change;
        break;
      case TYPE_KEEP_LEFT:
      case TYPE_TURN_SLIGHT_LEFT:
        resourceId = R.drawable.ic_turn_slight_left;
        break;
      case TYPE_KEEP_RIGHT:
      case TYPE_TURN_SLIGHT_RIGHT:
        resourceId = R.drawable.ic_turn_slight_right;
        break;
      case TYPE_TURN_SHARP_LEFT:
        resourceId = R.drawable.ic_turn_sharp_left;
        break;
      case TYPE_TURN_SHARP_RIGHT:
        resourceId = R.drawable.ic_turn_sharp_right;
        break;
      case TYPE_U_TURN_LEFT:
        resourceId = R.drawable.ic_turn_u_turn_left;
        break;
      case TYPE_U_TURN_RIGHT:
        resourceId = R.drawable.ic_turn_u_turn_right;
        break;
      case TYPE_ON_RAMP_SLIGHT_LEFT:
      case TYPE_ON_RAMP_NORMAL_LEFT:
      case TYPE_ON_RAMP_SHARP_LEFT:
      case TYPE_ON_RAMP_U_TURN_LEFT:
      case TYPE_OFF_RAMP_SLIGHT_LEFT:
      case TYPE_OFF_RAMP_NORMAL_LEFT:
      case TYPE_FORK_LEFT:
        resourceId = R.drawable.ic_turn_fork_left;
        break;
      case TYPE_ON_RAMP_SLIGHT_RIGHT:
      case TYPE_ON_RAMP_NORMAL_RIGHT:
      case TYPE_ON_RAMP_SHARP_RIGHT:
      case TYPE_ON_RAMP_U_TURN_RIGHT:
      case TYPE_OFF_RAMP_SLIGHT_RIGHT:
      case TYPE_OFF_RAMP_NORMAL_RIGHT:
      case TYPE_FORK_RIGHT:
        resourceId = R.drawable.ic_turn_fork_right;
        break;
      case TYPE_MERGE_LEFT:
      case TYPE_MERGE_RIGHT:
      case TYPE_MERGE_SIDE_UNSPECIFIED:
        resourceId = R.drawable.ic_turn_merge_symmetrical;
        break;
      case TYPE_ROUNDABOUT_ENTER:
        resourceId = R.drawable.ic_turn_name_change;
        break;
      case TYPE_ROUNDABOUT_EXIT:
        resourceId = R.drawable.ic_turn_name_change;
        break;
      case TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW:
      case TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW_WITH_ANGLE:
        resourceId = R.drawable.ic_turn_slight_left;
        break;
      case TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW:
      case TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE:
        resourceId = R.drawable.ic_turn_slight_right;
        break;
      case TYPE_FERRY_BOAT:
      case TYPE_FERRY_TRAIN:
        resourceId = R.drawable.ic_turn_name_change;
        break;
      default:
        throw new IllegalStateException("Unexpected maneuver type: " + type);
    }
    return CarIcon.builder(IconCompat.createWithResource(carContext, resourceId)).build();
  }

  private DemoScripts() {}
}
