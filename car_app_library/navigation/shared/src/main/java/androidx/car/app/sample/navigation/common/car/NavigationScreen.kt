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
package androidx.car.app.sample.navigation.common.car

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.Distance
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.Destination
import androidx.car.app.navigation.model.MessageInfo
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step
import androidx.car.app.navigation.model.TravelEstimate
import androidx.car.app.sample.navigation.common.R
import androidx.car.app.sample.navigation.common.model.Instruction
import androidx.core.graphics.drawable.IconCompat

/** Simple demo of how to present a trip on the routing screen.  */
class NavigationScreen(
    carContext: CarContext,
    private val mSettingsAction: Action,
    private val mListener: Listener,
    private val mSurfaceRenderer: SurfaceRenderer
) : Screen(carContext) {
    /** A listener for navigation start and stop signals.  */
    interface Listener {
        /** Executes the given instructions.  */
        fun executeScript(instructions: List<Instruction>)

        /** Stops navigation.  */
        fun stopNavigation()
    }

    private val mMicrophoneRecorder = MicrophoneRecorder(carContext)

    private var mIsNavigating = false
    private var mIsRerouting = false
    private var mHasArrived = false

    private var mDestinations: List<Destination?>? = null

    private var mSteps: List<Step?>? = null

    private var mStepRemainingDistance: Distance? = null

    private var mDestinationTravelEstimate: TravelEstimate? = null
    private var mShouldShowNextStep = false
    private var mShouldShowLanes = false

    var mJunctionImage: CarIcon? = null

    private var mIsInPanMode = false

    /** Updates the navigation screen with the next instruction.  */
    fun updateTrip(
        isNavigating: Boolean,
        isRerouting: Boolean,
        hasArrived: Boolean,
        destinations: List<Destination?>?,
        steps: List<Step?>?,
        nextDestinationTravelEstimate: TravelEstimate?,
        nextStepRemainingDistance: Distance?,
        shouldShowNextStep: Boolean,
        shouldShowLanes: Boolean,
        junctionImage: CarIcon?
    ) {
        mIsNavigating = isNavigating
        mIsRerouting = isRerouting
        mHasArrived = hasArrived
        mDestinations = destinations
        mSteps = steps
        mStepRemainingDistance = nextStepRemainingDistance
        mDestinationTravelEstimate = nextDestinationTravelEstimate
        mShouldShowNextStep = shouldShowNextStep
        mShouldShowLanes = shouldShowLanes
        mJunctionImage = junctionImage
        invalidate()
    }

    override fun onGetTemplate(): Template {
        mSurfaceRenderer.updateMarkerVisibility( /* showMarkers=*/
            false,  /* numMarkers=*/0,  /* activeMarker=*/-1
        )

        val builder = NavigationTemplate.Builder()
        builder.setBackgroundColor(CarColor.SECONDARY)

        // Set the action strip.
        val actionStripBuilder = ActionStrip.Builder()
        if (mIsNavigating) {
            actionStripBuilder.addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_add_stop
                            )
                        )
                            .build()
                    )
                    .setOnClickListener { this.openFavorites() }
                    .build())
        }

        actionStripBuilder.addAction(mSettingsAction)
        actionStripBuilder.addAction(
            Action.Builder()
                .setTitle("Voice")
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            R.drawable.ic_mic
                        )
                    ).build()
                ).setOnClickListener { mMicrophoneRecorder.record() }
                .build())
        if (mIsNavigating) {
            actionStripBuilder.addAction(
                Action.Builder()
                    .setTitle("Stop")
                    .setOnClickListener { this.stopNavigation() }
                    .build())
        } else {
            actionStripBuilder.addAction(
                Action.Builder()
                    .setTitle("Search")
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_search_black36dp
                            )
                        )
                            .build()
                    )
                    .setOnClickListener { this.openSearch() }
                    .build())
            actionStripBuilder.addAction(
                Action.Builder()
                    .setTitle("Favorites")
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_favorite_white_24dp
                            )
                        )
                            .build()
                    )
                    .setOnClickListener { this.openFavorites() }
                    .build())
        }
        builder.setActionStrip(actionStripBuilder.build())

        // Set the map action strip with the pan and zoom buttons.
        val panIconBuilder = CarIcon.Builder(
            IconCompat.createWithResource(
                carContext,
                R.drawable.ic_pan_24
            )
        )
        if (mIsInPanMode) {
            panIconBuilder.setTint(CarColor.BLUE)
        }

        builder.setMapActionStrip(ActionStrip.Builder()
            .addAction(
                Action.Builder(Action.PAN)
                    .setIcon(panIconBuilder.build())
                    .build()
            )
            .addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_recenter_24
                            )
                        )
                            .build()
                    )
                    .setOnClickListener { mSurfaceRenderer.handleRecenter() }
                    .build())
            .addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_zoom_out_24
                            )
                        )
                            .build()
                    )
                    .setOnClickListener {
                        mSurfaceRenderer.handleScale(
                            INVALID_FOCAL_POINT_VAL,
                            INVALID_FOCAL_POINT_VAL,
                            ZOOM_OUT_BUTTON_SCALE_FACTOR
                        )
                    }
                    .build())
            .addAction(
                Action.Builder()
                    .setIcon(
                        CarIcon.Builder(
                            IconCompat.createWithResource(
                                carContext,
                                R.drawable.ic_zoom_in_24
                            )
                        )
                            .build()
                    )
                    .setOnClickListener {
                        mSurfaceRenderer.handleScale(
                            INVALID_FOCAL_POINT_VAL,
                            INVALID_FOCAL_POINT_VAL,
                            ZOOM_IN_BUTTON_SCALE_FACTOR
                        )
                    }
                    .build())
            .build())

        // When the user enters the pan mode, remind the user that they can exit the pan mode by
        // pressing the select button again.
        builder.setPanModeListener { isInPanMode: Boolean ->
            if (isInPanMode) {
                CarToast.makeText(
                    carContext,
                    "Press Select to exit the pan mode",
                    CarToast.LENGTH_LONG
                ).show()
            }
            mIsInPanMode = isInPanMode
            invalidate()
        }

        if (mIsNavigating) {
            if (mDestinationTravelEstimate != null) {
                builder.setDestinationTravelEstimate(mDestinationTravelEstimate!!)
            }

            if (isRerouting) {
                builder.setNavigationInfo(RoutingInfo.Builder().setLoading(true).build())
            } else if (mHasArrived) {
                val messageInfo = MessageInfo.Builder(
                    carContext.getString(R.string.navigation_arrived)
                ).build()
                builder.setNavigationInfo(messageInfo)
            } else {
                val info = RoutingInfo.Builder()
                val tmp = mSteps!![0]
                val currentStep =
                    Step.Builder(tmp!!.cue!!.toCharSequence())
                        .setManeuver(tmp.maneuver!!)
                        .setRoad(tmp.road!!.toCharSequence())
                if (mShouldShowLanes) {
                    for (lane in tmp.lanes) {
                        currentStep.addLane(lane!!)
                    }
                    currentStep.setLanesImage(tmp.lanesImage!!)
                }
                info.setCurrentStep(currentStep.build(), mStepRemainingDistance!!)
                if (mShouldShowNextStep && mSteps!!.size > 1) {
                    info.setNextStep(mSteps!![1]!!)
                }
                if (mJunctionImage != null) {
                    info.setJunctionImage(mJunctionImage!!)
                }
                builder.setNavigationInfo(info.build())
            }
        }

        return builder.build()
    }

    private val isRerouting: Boolean
        get() = mIsRerouting || mDestinations == null

    private fun stopNavigation() {
        mListener.stopNavigation()
    }

    private fun openFavorites() {
        screenManager
            .pushForResult(
                FavoritesScreen(carContext, mSettingsAction, mSurfaceRenderer)
            ) { obj: Any? ->
                if (obj == null || mIsNavigating) {
                    return@pushForResult
                }
                // Need to copy over each element to satisfy Java type safety.
                val results = obj as List<*>
                val instructions: MutableList<Instruction> = ArrayList()
                for (result in results) {
                    instructions.add(result as Instruction)
                }
                mListener.executeScript(instructions)
            }
    }

    private fun openSearch() {
        screenManager
            .pushForResult(
                SearchScreen(carContext, mSettingsAction, mSurfaceRenderer)
            ) { obj: Any? ->
                if (obj != null) {
                    // Need to copy over each element to satisfy Java type safety.
                    val results = obj as List<*>
                    val instructions: MutableList<Instruction> = ArrayList()
                    for (result in results) {
                        instructions.add(result as Instruction)
                    }
                    mListener.executeScript(instructions)
                }
            }
    }

    companion object {
        /** Invalid zoom focal point value, used for the zoom buttons.  */
        private const val INVALID_FOCAL_POINT_VAL = -1f

        /** Zoom-in scale factor, used for the zoom-in button.  */
        private const val ZOOM_IN_BUTTON_SCALE_FACTOR = 1.1f

        /** Zoom-out scale factor, used for the zoom-out button.  */
        private const val ZOOM_OUT_BUTTON_SCALE_FACTOR = 0.9f
    }
}
