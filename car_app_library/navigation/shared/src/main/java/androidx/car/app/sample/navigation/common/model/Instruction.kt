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

import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.sample.navigation.common.model.Instruction.Type

/**
 * Container for scripting navigation instructions.
 *
 *
 * Each [Instruction] represents a change (delta) in the state of navigation corresponding
 * to a change in state that could occur during a real navigation session. In general a script
 * follows the following rough outline similar to a driving sessions:
 *
 *
 *  * Start Navigation
 *  * Add one or more destinations (as would be selected by a driver)
 *  * Add one or more steps (as would be provided by a routing algorithm)
 *  * Add updated positions until the next step is reached.
 *  * pop a step and send positions until the next step is reached.
 *  * Repeat until all steps are popped.
 *  * End Navigation.
 *
 *
 *
 * In addition to a [Type], each instruction specifies the duration until the next
 * instruction should be executed. A duration of zero allows multiple instructions to be stacked up
 * as if they were a single instruction. All other parameters are optional and related directly to
 * which instruction is specified.
 */
class Instruction internal constructor(builder: Builder) {
    enum class Type {
        START_NAVIGATION,
        END_NAVIGATION,
        ADD_DESTINATION_NAVIGATION,
        POP_DESTINATION_NAVIGATION,
        ADD_STEP_NAVIGATION,
        POP_STEP_NAVIGATION,
        SET_REROUTING,
        SET_ARRIVED,
        SET_TRIP_POSITION_NAVIGATION,
    }

    val type: Type = builder.mType
    val durationMillis: Long = builder.mDurationMillis

    // Only support a single destination at the moment.
    val destination: Destination? = builder.mDestination

    // Only support setting a single step.
    val step: Step? = builder.mStep
    val stepRemainingDistance: Distance? = builder.mStepRemainingDistance
    val stepTravelEstimate: TravelEstimate? = builder.mStepTravelEstimate
    val destinationTravelEstimate: TravelEstimate? = builder.mDestinationTravelEstimate
    val road: String? = builder.mRoad
    val shouldShowNextStep: Boolean = builder.mShouldShowNextStep
    val shouldShowLanes: Boolean = builder.mShouldShowLanes
    val junctionImage: CarIcon? = builder.mJunctionImage

    val notificationTitle: String? = builder.mNotificationTitle
    val notificationContent: String? = builder.mNotificationContent
    val notificationIcon: Int = builder.mNotificationIcon
    val shouldNotify: Boolean = builder.mShouldNotify

    /** Builder for creating an [Instruction].  */
    class Builder(var mType: Type, var mDurationMillis: Long) {
        var mDestination: Destination? = null
        var mStep: Step? = null
        var mStepRemainingDistance: Distance? = null
        var mStepTravelEstimate: TravelEstimate? = null
        var mDestinationTravelEstimate: TravelEstimate? = null
        var mRoad: String? = null
        var mShouldShowNextStep: Boolean = false
        var mShouldShowLanes: Boolean = false
        var mJunctionImage: CarIcon? = null

        var mNotificationTitle: String? = null
        var mNotificationContent: String? = null
        var mNotificationIcon: Int = 0
        var mShouldNotify: Boolean = false

        fun setDestination(destination: Destination?): Builder {
            mDestination = destination
            return this
        }

        fun setStep(step: Step?): Builder {
            mStep = step
            return this
        }

        fun setStepRemainingDistance(stepRemainingDistance: Distance?): Builder {
            mStepRemainingDistance = stepRemainingDistance
            return this
        }

        fun setStepTravelEstimate(stepTravelEstimate: TravelEstimate?): Builder {
            mStepTravelEstimate = stepTravelEstimate
            return this
        }

        fun setDestinationTravelEstimate(destinationTravelEstimate: TravelEstimate?): Builder {
            mDestinationTravelEstimate = destinationTravelEstimate
            return this
        }

        fun setRoad(road: String?): Builder {
            mRoad = road
            return this
        }

        fun setShouldShowNextStep(shouldShowNextStep: Boolean): Builder {
            mShouldShowNextStep = shouldShowNextStep
            return this
        }

        fun setShouldShowLanes(shouldShowLanes: Boolean): Builder {
            mShouldShowLanes = shouldShowLanes
            return this
        }

        fun setJunctionImage(junctionImage: CarIcon?): Builder {
            mJunctionImage = junctionImage
            return this
        }

        fun setNotification(
            shouldNotify: Boolean,
            notificationTitle: String?,
            notificationContent: String?,
            notificationIcon: Int
        ): Builder {
            mNotificationTitle = notificationTitle
            mNotificationContent = notificationContent
            mShouldNotify = shouldNotify
            mNotificationIcon = notificationIcon
            return this
        }

        /** Constructs the [Instruction] defined by this builder.  */
        fun build(): Instruction {
            return Instruction(this)
        }
    }

    companion object {
        /** Constructs a new builder of [Instruction].  */
        fun builder(type: Type, lengthMs: Long): Builder {
            return Builder(type, lengthMs)
        }
    }
}
