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

import androidx.annotation.Nullable;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.navigation.model.Destination;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;

/**
 * Container for scripting navigation instructions.
 *
 * <p>For example things which are triggered based on a users location changing can be put into the
 * script to simulate driving without having to provide a full location mockup.
 *
 * <p>In addition to a {@link Type}, each instruction has a length. All other parameters are
 * optional and related directly to which instruction is specified.
 */
public class Instruction {

  public enum Type {
    START_NAVIGATION,
    END_NAVIGATION,
    ADD_DESTINATION_NAVIGATION,
    POP_DESTINATION_NAVIGATION,
    ADD_STEP_NAVIGATION,
    POP_STEP_NAVIGATION,
    SET_TRIP_POSITION_NAVIGATION,
  };

  private final Type mType;
  private final long mLengthMillis;

  // Only support a single destination at the moment.
  @Nullable private final Destination mDestination;

  // Only support setting a single step.
  @Nullable private final Step mStep;
  @Nullable private final Distance mStepRemainingDistance;
  @Nullable private final TravelEstimate mStepTravelEstimate;
  @Nullable private final TravelEstimate mDestinationTravelEstimate;
  @Nullable private final String mRoad;

  /** Constructs a new builder of {@link Instruction}. */
  public static Builder builder(Type type, long lengthMs) {
    return new Builder(type, lengthMs);
  }

  public long getLengthMillis() {
    return mLengthMillis;
  }

  public Type getType() {
    return mType;
  }

  @Nullable
  public Destination getDestination() {
    return mDestination;
  }

  @Nullable
  public Step getStep() {
    return mStep;
  }

  @Nullable
  public Distance getStepRemainingDistance() {
    return mStepRemainingDistance;
  }

  @Nullable
  public TravelEstimate getStepTravelEstimate() {
    return mStepTravelEstimate;
  }

  @Nullable
  public TravelEstimate getDestinationTravelEstimate() {
    return mDestinationTravelEstimate;
  }

  @Nullable
  public String getRoad() {
    return mRoad;
  }

  private Instruction(Builder builder) {
    mType = builder.mType;
    mLengthMillis = builder.mLengthMs;
    mDestination = builder.mDestination;
    mStep = builder.mStep;
    mStepRemainingDistance = builder.mStepRemainingDistance;
    mStepTravelEstimate = builder.mStepTravelEstimate;
    mDestinationTravelEstimate = builder.mDestinationTravelEstimate;
    mRoad = builder.mRoad;
  }

  /** Builder for creating an {@link Instruction}. */
  public static final class Builder {
    private Type mType;
    private long mLengthMs;
    @Nullable private Destination mDestination;
    @Nullable private Step mStep;
    @Nullable private Distance mStepRemainingDistance;
    @Nullable private TravelEstimate mStepTravelEstimate;
    @Nullable private TravelEstimate mDestinationTravelEstimate;
    @Nullable private String mRoad;

    public Builder(Type type, long lengthMs) {
      mType = type;
      mLengthMs = lengthMs;
    }

    Builder setDestination(@Nullable Destination destination) {
      mDestination = destination;
      return this;
    }

    Builder setStep(@Nullable Step step) {
      mStep = step;
      return this;
    }

    Builder setStepRemainingDistance(@Nullable Distance stepRemainingDistance) {
      mStepRemainingDistance = stepRemainingDistance;
      return this;
    }

    Builder setStepTravelEstimate(@Nullable TravelEstimate stepTravelEstimate) {
      mStepTravelEstimate = stepTravelEstimate;
      return this;
    }

    Builder setDestinationTravelEstimate(@Nullable TravelEstimate destinationTravelEstimate) {
      mDestinationTravelEstimate = destinationTravelEstimate;
      return this;
    }

    Builder setRoad(@Nullable String road) {
      mRoad = road;
      return this;
    }

    /** Constructs the {@link Instruction} defined by this builder. */
    public Instruction build() {
      return new Instruction(this);
    }
  }
}
