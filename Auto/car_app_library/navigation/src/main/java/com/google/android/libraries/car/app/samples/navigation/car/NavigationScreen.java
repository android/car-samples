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

package com.google.android.libraries.car.app.samples.navigation.car;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.ActionStrip;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.navigation.model.Destination;
import com.google.android.libraries.car.app.navigation.model.MessageInfo;
import com.google.android.libraries.car.app.navigation.model.NavigationTemplate;
import com.google.android.libraries.car.app.navigation.model.RoutingInfo;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;
import com.google.android.libraries.car.app.samples.navigation.R;
import com.google.android.libraries.car.app.samples.navigation.model.Instruction;
import java.util.ArrayList;
import java.util.List;

/** Simple demo of how to present a trip on the routing screen. */
public final class NavigationScreen extends Screen {

  public interface Listener {
    void executeScript(List<Instruction> instructions);

    void stopNavigation();
  }

  @NonNull private final Listener mListener;
  @NonNull private final Action mSettingsAction;
  @NonNull private final SurfaceRenderer mSurfaceRenderer;

  private boolean mIsNavigating;
  private boolean mIsRerouting;
  private boolean mHasArrived;
  @Nullable private List<Destination> mDestinations;
  @Nullable private List<Step> mSteps;
  @Nullable private Distance mStepRemainingDistance;
  @Nullable private TravelEstimate mDestinationTravelEstimate;
  private boolean mShouldShowNextStep;
  private boolean mShouldShowLanes;
  @Nullable CarIcon mJunctionImage;

  public NavigationScreen(
      @NonNull CarContext carContext,
      @NonNull Action settingsAction,
      @NonNull Listener listener,
      SurfaceRenderer surfaceRenderer) {
    super(carContext);
    mListener = listener;
    mSettingsAction = settingsAction;
    mSurfaceRenderer = surfaceRenderer;
  }

  public void updateTrip(
      boolean isNavigating,
      boolean isRerouting,
      boolean hasArrived,
      @Nullable List<Destination> destinations,
      @Nullable List<Step> steps,
      @Nullable TravelEstimate nextDestinationTravelEstimate,
      @Nullable Distance nextStepRemainingDistance,
      boolean shouldShowNextStep,
      boolean shouldShowLanes,
      @Nullable CarIcon junctionImage) {
    mIsNavigating = isNavigating;
    mIsRerouting = isRerouting;
    mHasArrived = hasArrived;
    mDestinations = destinations;
    mSteps = steps;
    mStepRemainingDistance = nextStepRemainingDistance;
    mDestinationTravelEstimate = nextDestinationTravelEstimate;
    mShouldShowNextStep = shouldShowNextStep;
    mShouldShowLanes = shouldShowLanes;
    mJunctionImage = junctionImage;
    invalidate();
  }

  @NonNull
  @Override
  public Template getTemplate() {
    mSurfaceRenderer.updateMarkerVisibility(
        /* showMarkers=*/ false, /* numMarkers=*/ 0, /* activeMarker=*/ -1);

    NavigationTemplate.Builder builder = NavigationTemplate.builder();
    builder.setBackgroundColor(CarColor.SECONDARY);

    ActionStrip.Builder actionStripBuilder = ActionStrip.builder();
    actionStripBuilder.addAction(mSettingsAction);
    if (mIsNavigating) {
      actionStripBuilder.addAction(
          Action.builder().setTitle("Stop").setOnClickListener(this::stopNavigation).build());
    } else {
      actionStripBuilder.addAction(
          Action.builder()
              .setIcon(
                  CarIcon.of(
                      IconCompat.createWithResource(
                          getCarContext(), R.drawable.ic_search_black36dp)))
              .setOnClickListener(this::openSearch)
              .build());
      actionStripBuilder.addAction(
          Action.builder().setTitle("Favorites").setOnClickListener(this::openFavorites).build());
    }
    builder.setActionStrip(actionStripBuilder.build());

    if (mIsNavigating) {
      builder.setDestinationTravelEstimate(mDestinationTravelEstimate);

      if (isRerouting()) {
        builder.setNavigationInfo(RoutingInfo.builder().setIsLoading(true).build());
      } else if (mHasArrived) {
        builder.setNavigationInfo(MessageInfo.builder("Arrived!").build());
      } else {
        RoutingInfo.Builder info = RoutingInfo.builder();
        Step tmp = mSteps.get(0);
        Step.Builder currentStep = tmp.newBuilder();
        if (!mShouldShowLanes) {
          currentStep.clearLanes();
          currentStep.setLanesImage(null);
        }
        info.setCurrentStep(currentStep.build(), mStepRemainingDistance);
        if (mShouldShowNextStep && mSteps.size() > 1) {
          info.setNextStep(mSteps.get(1));
        }
        info.setJunctionImage(mJunctionImage);
        builder.setNavigationInfo(info.build());
      }
    }

    return builder.build();
  }

  private boolean isRerouting() {
    return mIsRerouting || mDestinations == null;
  }

  private void stopNavigation() {
    mListener.stopNavigation();
  }

  private void openFavorites() {
    getScreenManager()
        .pushForResult(
            new FavoritesScreen(getCarContext(), mSettingsAction, mSurfaceRenderer),
            (obj) -> {
              if (obj != null) {
                // Need to copy over each element to satisfy Java type safety.
                List<?> results = (List<?>) obj;
                List<Instruction> instructions = new ArrayList<Instruction>();
                for (Object result : results) {
                  instructions.add((Instruction) result);
                }
                mListener.executeScript(instructions);
              }
            });
  }

  private void openSearch() {
    getScreenManager()
        .pushForResult(
            new SearchScreen(getCarContext(), mSettingsAction, mSurfaceRenderer),
            (obj) -> {
              if (obj != null) {
                // Need to copy over each element to satisfy Java type safety.
                List<?> results = (List<?>) obj;
                List<Instruction> instructions = new ArrayList<Instruction>();
                for (Object result : results) {
                  instructions.add((Instruction) result);
                }
                mListener.executeScript(instructions);
              }
            });
  }
}
