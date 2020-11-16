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

import static java.util.concurrent.TimeUnit.SECONDS;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarColor;
import com.google.android.libraries.car.app.model.MessageTemplate;
import com.google.android.libraries.car.app.model.ParkedOnlyOnClickListener;
import com.google.android.libraries.car.app.model.Template;
import com.google.android.libraries.car.app.samples.navigation.app.MainActivity;
import java.util.Arrays;

/** Screen for asking the user to grant location permission. */
public class RequestPermissionScreen extends Screen implements DefaultLifecycleObserver {

  /** Callback called when the location permission is granted. */
  public interface LocationPermissionCheckCallback {
    void onPermissionGranted();
  }

  /** The location permission poll message id. */
  private static final int MSG_SEND_POLL_PERMISSION = 1;

  /** The frequency of the polls to check the location permission status. */
  private static final long POLL_PERMISSION_FREQUENCY_MILLIS = SECONDS.toMillis(1);

  /** A handler for periodically checking the location permission. */
  private final Handler mHandler = new Handler(Looper.getMainLooper(), new HandlerCallback());

  private LocationPermissionCheckCallback mLocationPermissionCheckCallback;

  public RequestPermissionScreen(
      @NonNull CarContext carContext, LocationPermissionCheckCallback callback) {
    super(carContext);
    getLifecycle().addObserver(this);
    mLocationPermissionCheckCallback = callback;
  }

  @Override
  public void onStart(@NonNull LifecycleOwner owner) {
    mHandler.sendMessageDelayed(
        mHandler.obtainMessage(MSG_SEND_POLL_PERMISSION), POLL_PERMISSION_FREQUENCY_MILLIS);
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    mHandler.removeMessages(MSG_SEND_POLL_PERMISSION);
  }

  @NonNull
  @Override
  public Template getTemplate() {
    return MessageTemplate.builder("Please allow location access on the phone while not driving.")
        .setHeaderAction(Action.APP_ICON)
        .setActions(
            Arrays.asList(
                Action.builder()
                    .setBackgroundColor(CarColor.BLUE)
                    .setOnClickListener(
                        ParkedOnlyOnClickListener.create(
                            () -> {
                              // Launch a phone activity to request the location permission.
                              getCarContext()
                                  .startActivity(
                                      new Intent(getCarContext(), MainActivity.class)
                                          .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }))
                    .setTitle("Go to Phone")
                    .build()))
        .build();
  }

  /**
   * A {@link Handler.Callback} used to process the message queue for polling the location
   * permission.
   */
  private final class HandlerCallback implements Handler.Callback {
    @Override
    public boolean handleMessage(Message msg) {
      if (msg.what == MSG_SEND_POLL_PERMISSION) {
        // If the location permission is granted, invoke the callback and pop the screen, which will
        // show the preseeded navigation screen.
        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionCheckCallback.onPermissionGranted();
          RequestPermissionScreen.this.finish();
          return true;
        }

        mHandler.sendMessageDelayed(
            mHandler.obtainMessage(MSG_SEND_POLL_PERMISSION), POLL_PERMISSION_FREQUENCY_MILLIS);
        return true;
      }
      return false;
    }
  }
}
