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
package androidx.car.app.sample.navigation.common.model

import androidx.car.app.CarContext
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.DateTimeWithZone
import androidx.car.app.model.Distance
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Lane
import androidx.car.app.navigation.model.LaneDirection
import androidx.car.app.navigation.model.Maneuver
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.sample.navigation.common.R
import androidx.core.graphics.drawable.IconCompat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit

/**
 * Example scripts to "control" navigation within the app.
 *
 *
 * The script takes the form of a list of instructions which can be passed to different parts of
 * the app. This is the central location where scripts are stored.
 *
 *
 * The scripts start with a setup phase where all destinations and steps are added to the
 * instruction list. Then navigation updates are added for each step to simulate driving.
 *
 *
 * >The setup phases consists of:
 *
 *
 *  * Start Navigation
 *  * Add the destination information.
 *  * Add 4 intermediate steps to the destination.
 *
 *
 *
 * The navigation phase consists of
 *
 *
 *  * Add positions along the route getting closer to the step.
 *  * Once the step is reached, pop the step. If more steps remain go back to adding more
 * positions.
 *  * When no more steps remain set the arrived state.
 *  * End Navigation
 *
 *
 *
 * There are several helper functions including [.generateTripUpdateSequence] which
 * interpolates a straight path and generates all the updates for a step.
 */
object DemoScripts {
    private const val INSTRUCTION_NO_ELAPSED_TIME: Long = 0
    private const val SPEED_METERS_PER_SEC = 5
    private const val DISTANCE_METERS = 450

    /** Create instructions for home.  */
    @JvmStatic
    fun getNavigateHome(carContext: CarContext): List<Instruction> {
        val instructions = ArrayList<Instruction>()

        val arrivalTimeAtDestination = getCurrentDateTimeZoneWithOffset(30)

        val lanesImage =
            CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.lanes))
                .build()
        val junctionImage =
            CarIcon.Builder(
                IconCompat.createWithResource(
                    carContext,
                    R.drawable.junction_image
                )
            )
                .build()

        val straightNormal =
            Lane.Builder()
                .addDirection(LaneDirection.create(LaneDirection.SHAPE_STRAIGHT, false))
                .build()
        val rightHighlighted =
            Lane.Builder()
                .addDirection(LaneDirection.create(LaneDirection.SHAPE_NORMAL_RIGHT, true))
                .build()

        val step1IconResourceId =
            getTurnIconResourceId(Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE)
        val step1 =
            Step.Builder("State Street")
                .setManeuver(
                    getManeuverWithExitNumberAndAngle(
                        carContext,
                        Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE,
                        step1IconResourceId,
                        2,
                        270
                    )
                )
                .setRoad("State Street")
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(rightHighlighted)
                .setLanesImage(lanesImage)
                .build()
        val step2IconResourceId = getTurnIconResourceId(Maneuver.TYPE_TURN_NORMAL_LEFT)
        val step2 =
            Step.Builder("Kirkland Way")
                .setManeuver(
                    getManeuver(
                        carContext,
                        Maneuver.TYPE_TURN_NORMAL_LEFT,
                        step2IconResourceId
                    )
                )
                .setRoad("Kirkland Way")
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(rightHighlighted)
                .setLanesImage(lanesImage)
                .build()
        val step3IconResourceId = getTurnIconResourceId(Maneuver.TYPE_TURN_NORMAL_RIGHT)
        val step3 =
            Step.Builder("6th Street.")
                .setManeuver(
                    getManeuver(
                        carContext,
                        Maneuver.TYPE_TURN_NORMAL_RIGHT,
                        step3IconResourceId
                    )
                )
                .setRoad("6th Street.")
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(straightNormal)
                .addLane(rightHighlighted)
                .setLanesImage(lanesImage)
                .build()
        val step4IconResourceId = getTurnIconResourceId(Maneuver.TYPE_DESTINATION_RIGHT)
        val step4 =
            Step.Builder("Google Kirkland.")
                .setManeuver(
                    getManeuver(
                        carContext, Maneuver.TYPE_DESTINATION_RIGHT, step4IconResourceId
                    )
                )
                .setRoad("Google Kirkland.")
                .build()

        // Start the navigation and add destination and steps.
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.START_NAVIGATION,
                INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )

        val destination =
            Destination.Builder().setName("Work").setAddress("747 6th St.").build()
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.ADD_DESTINATION_NAVIGATION,
                INSTRUCTION_NO_ELAPSED_TIME
            )
                .setDestination(destination)
                .build()
        )

        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.SET_REROUTING,
                TimeUnit.SECONDS.toMillis(5)
            )
                .setDestinationTravelEstimate(
                    TravelEstimate.Builder(
                        Distance.create(350.0, Distance.UNIT_METERS),
                        arrivalTimeAtDestination
                    )
                        .setRemainingTimeSeconds( /* remainingTimeSeconds= */
                            (DISTANCE_METERS
                                    / SPEED_METERS_PER_SEC).toLong()
                        )
                        .build()
                )
                .setNotification(
                    true,
                    carContext.getString(R.string.navigation_rerouting),
                    null,
                    R.drawable.ic_launcher
                )
                .build()
        )

        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .setStep(step1)
                .build()
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .setStep(step2)
                .build()
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .setStep(step3)
                .build()
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.ADD_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .setStep(step4)
                .build()
        )

        // Add trip positions for each step.
        var updateDistanceRemaining = DISTANCE_METERS
        instructions.addAll(
            generateTripUpdateSequence( /* count= */
                4,  /* startDestinationDistanceRemaining= */
                updateDistanceRemaining,  /* startStepDistanceRemaining= */
                100,
                arrivalTimeAtDestination,
                "3rd Street",
                junctionImage,  /* showLanes= */
                true,
                "onto State Street",
                SPEED_METERS_PER_SEC,
                step1IconResourceId
            )
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.POP_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )
        updateDistanceRemaining -= 100

        instructions.addAll(
            generateTripUpdateSequence( /* count= */
                6,  /* startDestinationDistanceRemaining= */
                updateDistanceRemaining,  /* startStepDistanceRemaining= */
                150,
                arrivalTimeAtDestination,
                "State Street",
                junctionImage,  /* showLanes= */
                true,
                "onto Kirkland Way",
                SPEED_METERS_PER_SEC,
                step2IconResourceId
            )
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.POP_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )
        updateDistanceRemaining -= 150

        instructions.addAll(
            generateTripUpdateSequence( /* count= */
                4,  /* startDestinationDistanceRemaining= */
                updateDistanceRemaining,  /* startStepDistanceRemaining= */
                100,
                arrivalTimeAtDestination,
                "Kirkland Way",
                junctionImage,  /* showLanes= */
                true,
                "onto 6th Street",
                SPEED_METERS_PER_SEC,
                step3IconResourceId
            )
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.POP_STEP_NAVIGATION, INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )
        updateDistanceRemaining -= 100

        instructions.addAll(
            generateTripUpdateSequence( /* count= */
                4,  /* startDestinationDistanceRemaining= */
                updateDistanceRemaining,  /* startStepDistanceRemaining= */
                100,
                arrivalTimeAtDestination,
                "6th Street",  /* junctionImage= */
                null,  /* showLanes= */
                false,
                "to Google Kirkland on right",
                SPEED_METERS_PER_SEC,
                step4IconResourceId
            )
        )

        // Set arrived state and then stop navigation.
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.SET_ARRIVED,
                TimeUnit.SECONDS.toMillis(5)
            )
                .build()
        )

        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.POP_DESTINATION_NAVIGATION,
                INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )
        instructions.add(
            Instruction.Companion.builder(
                Instruction.Type.END_NAVIGATION,
                INSTRUCTION_NO_ELAPSED_TIME
            )
                .build()
        )
        return instructions
    }

    private fun getCurrentDateTimeZoneWithOffset(offsetSeconds: Int): DateTimeWithZone {
        val startTime = GregorianCalendar()
        val destinationETA = startTime.clone() as GregorianCalendar
        destinationETA.add(Calendar.SECOND, offsetSeconds)
        return getDateTimeZone(destinationETA)
    }

    /** Convenience function to create the date formmat.  */
    private fun getDateTimeZone(calendar: GregorianCalendar): DateTimeWithZone {
        val date = calendar.time
        val timeZone = calendar.timeZone

        val timeSinceEpochMillis = date.time
        val timeZoneOffsetSeconds =
            TimeUnit.MILLISECONDS.toSeconds(timeZone.getOffset(timeSinceEpochMillis).toLong())
        val zoneShortName = "PST"

        return DateTimeWithZone.create(
            timeSinceEpochMillis, timeZoneOffsetSeconds.toInt(), zoneShortName
        )
    }

    /**
     * Generates all the updates for a particular step interpolating along a straight line.
     *
     * @param count                             number of instructions to generate until the next
     * step
     * @param startDestinationDistanceRemaining the distance until the final destination at the
     * start of the sequence
     * @param startStepDistanceRemaining        the distance until the next step at the start of the
     * sequence
     * @param arrivalTimeAtDestination          the arrival time at the destination
     * @param currentRoad                       the name of the road currently being travelled
     * @param junctionImage                     photo realistic image of upcoming turn
     * @param showLanes                         indicates if the lane info should be shown for
     * this maneuver
     * @param speed                             meters/second being traveled
     * @return sequence of instructions until the next step
     */
    private fun generateTripUpdateSequence(
        count: Int,
        startDestinationDistanceRemaining: Int,
        startStepDistanceRemaining: Int,
        arrivalTimeAtDestination: DateTimeWithZone,
        currentRoad: String,
        junctionImage: CarIcon?,
        showLanes: Boolean,
        nextInstruction: String,
        speed: Int,
        notificationIcon: Int
    ): List<Instruction> {
        val sequence: MutableList<Instruction> = ArrayList(count)
        var destinationDistanceRemaining = startDestinationDistanceRemaining
        var stepDistanceRemaining = startStepDistanceRemaining
        val distanceIncrement = startStepDistanceRemaining / count
        var notify = true

        for (i in 0 until count) {
            val remainingDistance =
                Distance.create(stepDistanceRemaining.toDouble(), Distance.UNIT_METERS)
            val destinationTravelEstimate =
                TravelEstimate.Builder(
                    Distance.create(
                        destinationDistanceRemaining.toDouble(), Distance.UNIT_METERS
                    ),
                    arrivalTimeAtDestination
                )
                    .setRemainingTimeSeconds((destinationDistanceRemaining / speed).toLong())
                    .setRemainingTimeColor(CarColor.YELLOW)
                    .setRemainingDistanceColor(CarColor.GREEN)
                    .build()
            val stepTravelEstimate =
                TravelEstimate.Builder(
                    remainingDistance,
                    getCurrentDateTimeZoneWithOffset(distanceIncrement)
                )
                    .setRemainingTimeSeconds( /* remainingTimeSeconds= */distanceIncrement.toLong())
                    .build()
            val notificationTitle = String.format("%dm", stepDistanceRemaining)
            val instruction: Instruction.Builder =
                Instruction.Companion.builder(
                    Instruction.Type.SET_TRIP_POSITION_NAVIGATION,
                    TimeUnit.SECONDS.toMillis((distanceIncrement / speed).toLong())
                )
                    .setStepRemainingDistance(remainingDistance)
                    .setStepTravelEstimate(stepTravelEstimate)
                    .setDestinationTravelEstimate(destinationTravelEstimate)
                    .setRoad(currentRoad)
                    .setNotification(
                        notify, notificationTitle, nextInstruction, notificationIcon
                    )
            // Don't show lanes in the first and last part of the maneuver. In the middle part of
            // the
            // maneuver use the passed parameter to determine if lanes should be shown.
            if (i == 0) {
                instruction.setShouldShowLanes(false).setShouldShowNextStep(true)
            } else if (i == 1) {
                instruction.setShouldShowLanes(showLanes).setShouldShowNextStep(true)
            } else if (i == 2) {
                instruction.setShouldShowLanes(showLanes).setShouldShowNextStep(false)
            } else {
                instruction
                    .setShouldShowLanes(false)
                    .setShouldShowNextStep(false)
                    .setJunctionImage(junctionImage)
            }
            sequence.add(instruction.build())

            destinationDistanceRemaining -= distanceIncrement
            stepDistanceRemaining -= distanceIncrement
            notify = false
        }
        return sequence
    }

    /** Returns a maneuver with image selected from resources.  */
    private fun getManeuver(
        carContext: CarContext, type: Int, iconResourceId: Int
    ): Maneuver {
        return Maneuver.Builder(type).setIcon(getCarIcon(carContext, iconResourceId)).build()
    }

    /**
     * Returns a maneuver that includes an exit number and angle with image selected from resources.
     */
    private fun getManeuverWithExitNumberAndAngle(
        carContext: CarContext,
        type: Int,
        iconResourceId: Int,
        exitNumber: Int,
        exitAngle: Int
    ): Maneuver {
        return Maneuver.Builder(type)
            .setIcon(getCarIcon(carContext, iconResourceId))
            .setRoundaboutExitNumber(exitNumber)
            .setRoundaboutExitAngle(exitAngle)
            .build()
    }

    /** Generates a [CarIcon] representing the turn.  */
    private fun getCarIcon(carContext: CarContext, resourceId: Int): CarIcon {
        return CarIcon.Builder(IconCompat.createWithResource(carContext, resourceId)).build()
    }

    private fun getTurnIconResourceId(type: Int): Int {
//        var resourceId = R.drawable.ic_launcher
        val resourceId = when (type) {
            Maneuver.TYPE_TURN_NORMAL_LEFT -> R.drawable.ic_turn_normal_left
            Maneuver.TYPE_TURN_NORMAL_RIGHT -> R.drawable.ic_turn_normal_right
            Maneuver.TYPE_UNKNOWN, Maneuver.TYPE_DEPART, Maneuver.TYPE_STRAIGHT -> R.drawable.ic_turn_name_change
            Maneuver.TYPE_DESTINATION, Maneuver.TYPE_DESTINATION_STRAIGHT, Maneuver.TYPE_DESTINATION_RIGHT, Maneuver.TYPE_DESTINATION_LEFT -> R.drawable.ic_turn_destination
            Maneuver.TYPE_NAME_CHANGE -> R.drawable.ic_turn_name_change
            Maneuver.TYPE_KEEP_LEFT, Maneuver.TYPE_TURN_SLIGHT_LEFT -> R.drawable.ic_turn_slight_left
            Maneuver.TYPE_KEEP_RIGHT, Maneuver.TYPE_TURN_SLIGHT_RIGHT -> R.drawable.ic_turn_slight_right
            Maneuver.TYPE_TURN_SHARP_LEFT -> R.drawable.ic_turn_sharp_left
            Maneuver.TYPE_TURN_SHARP_RIGHT -> R.drawable.ic_turn_sharp_right
            Maneuver.TYPE_U_TURN_LEFT -> R.drawable.ic_turn_u_turn_left
            Maneuver.TYPE_U_TURN_RIGHT -> R.drawable.ic_turn_u_turn_right
            Maneuver.TYPE_ON_RAMP_SLIGHT_LEFT, Maneuver.TYPE_ON_RAMP_NORMAL_LEFT, Maneuver.TYPE_ON_RAMP_SHARP_LEFT, Maneuver.TYPE_ON_RAMP_U_TURN_LEFT, Maneuver.TYPE_OFF_RAMP_SLIGHT_LEFT, Maneuver.TYPE_OFF_RAMP_NORMAL_LEFT, Maneuver.TYPE_FORK_LEFT -> R.drawable.ic_turn_fork_left
            Maneuver.TYPE_ON_RAMP_SLIGHT_RIGHT, Maneuver.TYPE_ON_RAMP_NORMAL_RIGHT, Maneuver.TYPE_ON_RAMP_SHARP_RIGHT, Maneuver.TYPE_ON_RAMP_U_TURN_RIGHT, Maneuver.TYPE_OFF_RAMP_SLIGHT_RIGHT, Maneuver.TYPE_OFF_RAMP_NORMAL_RIGHT, Maneuver.TYPE_FORK_RIGHT -> R.drawable.ic_turn_fork_right
            Maneuver.TYPE_MERGE_LEFT, Maneuver.TYPE_MERGE_RIGHT, Maneuver.TYPE_MERGE_SIDE_UNSPECIFIED -> R.drawable.ic_turn_merge_symmetrical
            Maneuver.TYPE_ROUNDABOUT_ENTER_CW, Maneuver.TYPE_ROUNDABOUT_ENTER_CCW, Maneuver.TYPE_ROUNDABOUT_EXIT_CW, Maneuver.TYPE_ROUNDABOUT_EXIT_CCW -> R.drawable.ic_turn_name_change
            Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW, Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CW_WITH_ANGLE -> R.drawable.ic_roundabout_cw
            Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW, Maneuver.TYPE_ROUNDABOUT_ENTER_AND_EXIT_CCW_WITH_ANGLE -> R.drawable.ic_roundabout_ccw
            Maneuver.TYPE_FERRY_BOAT, Maneuver.TYPE_FERRY_TRAIN -> R.drawable.ic_turn_name_change
            else -> throw IllegalStateException("Unexpected maneuver type: $type")
        }
        return resourceId
    }
}
