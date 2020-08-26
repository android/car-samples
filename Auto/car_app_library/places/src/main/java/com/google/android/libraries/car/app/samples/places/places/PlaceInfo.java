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

package com.google.android.libraries.car.app.samples.places.places;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.Nullable;

/** Contains information about a place returned by the Places API. */
public class PlaceInfo {
  private final String mId;
  private final String mName;
  private final Location mLocation;

  @Nullable private Address mAddress; // lazily written

  PlaceInfo(String id, String name, Location location) {
    mId = id;
    mName = name;
    mLocation = location;
  }

  public String getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }

  public Location getLocation() {
    return mLocation;
  }

  public Address getAddress(Geocoder geocoder) {
    if (mAddress == null) {
      mAddress = LocationUtil.getAddressForLocation(geocoder, mLocation);
    }
    return mAddress;
  }

  @Override
  public String toString() {
    return "[" + mName + ", " + mLocation + "]";
  }
}
