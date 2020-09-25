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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarAppService;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.ScreenManager;
import com.google.android.libraries.car.app.model.Action;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.navigation.model.Destination;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;
import com.google.android.libraries.car.app.samples.navigation.R;
import com.google.android.libraries.car.app.samples.navigation.model.Instruction;
import com.google.android.libraries.car.app.samples.navigation.nav.DeepLinkNotificationReceiver;
import com.google.android.libraries.car.app.samples.navigation.nav.NavigationService;
import java.util.List;

/** Entry point for the templated app. */
public final class NavigationCarAppService extends CarAppService
    implements NavigationScreen.Listener {
  private static final String TAG = NavigationCarAppService.class.getSimpleName();

  private static final String URI_SCHEME = "samples";
  private static final String URI_HOST = "navigation";

  @Nullable private SurfaceRenderer mNavigationCarSurface;

  // A reference to the navigation service used to get location updates and routing.
  @Nullable private NavigationService mService;

  @Nullable private NavigationScreen mNavigationScreen;

  public static Uri createDeepLinkUri(String deepLinkAction) {
    return Uri.fromParts(URI_SCHEME, URI_HOST, deepLinkAction);
  }

  private final NavigationService.Listener mServiceListener =
      new NavigationService.Listener() {
        @Override
        public void navigationStateChanged(
            boolean isNavigating,
            boolean isRerouting,
            boolean hasArrived,
            @Nullable List<Destination> destinations,
            @Nullable List<Step> steps,
            @Nullable TravelEstimate nextDestinationTravelEstimate,
            @Nullable Distance nextStepRemainingDistance) {
          mNavigationScreen.updateTrip(
              isNavigating,
              isRerouting,
              hasArrived,
              destinations,
              steps,
              nextDestinationTravelEstimate,
              nextStepRemainingDistance);
        }
      };

  // Monitors the state of the connection to the Navigation service.
  private final ServiceConnection mServiceConnection =
      new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          Log.i(TAG, String.format("In onServiceConnected() component:%s", name));
          NavigationService.LocalBinder binder = (NavigationService.LocalBinder) service;
          mService = binder.getService();
          mService.setCarContext(getCarContext(), mServiceListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
          Log.i(TAG, String.format("In onServiceDisconnected() component:%s", name));
          // Unhook map models here
          mService.clearCarContext();
          mService = null;
        }
      };

  private final LifecycleObserver mLifeCycleObserver =
      new DefaultLifecycleObserver() {

        @Override
        public void onCreate(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onCreate()");
        }

        @Override
        public void onStart(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onStart()");
          bindService(
              new Intent(NavigationCarAppService.this, NavigationService.class),
              mServiceConnection,
              Context.BIND_AUTO_CREATE);
        }

        @Override
        public void onResume(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onResume()");
        }

        @Override
        public void onPause(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onPause()");
        }

        @Override
        public void onStop(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onStop()");
          unbindService(mServiceConnection);
          mService = null;
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner lifecycleOwner) {
          Log.i(TAG, "In onDestroy()");
        }
      };

  public NavigationCarAppService() {
    Lifecycle lifecycle = getLifecycle();
    lifecycle.addObserver(mLifeCycleObserver);
  }

  @Override
  @NonNull
  public Screen onCreateScreen(@Nullable Intent intent) {
    Log.i(TAG, "In onCreateScreen()");

    Action settingsAction =
        Action.builder()
            .setIcon(
                CarIcon.of(IconCompat.createWithResource(getCarContext(), R.drawable.ic_settings)))
            .setOnClickListener(
                () -> {
                  getCarContext()
                      .getCarService(ScreenManager.class)
                      .push(new SettingsScreen(getCarContext()));
                })
            .build();

    mNavigationCarSurface = new SurfaceRenderer(getCarContext(), getLifecycle());
    mNavigationScreen = new NavigationScreen(getCarContext(), settingsAction, this);

    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      CarToast.makeText(
              getCarContext(), "Navigation intent: " + intent.getDataString(), CarToast.LENGTH_LONG)
          .show();
    }
    return mNavigationScreen;
  }

  @Override
  public void onNewIntent(@NonNull Intent intent) {
    Log.i(TAG, "In onNewIntent() " + intent);
    ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      CarToast.makeText(
              getCarContext(), "Navigation intent: " + intent.getDataString(), CarToast.LENGTH_LONG)
          .show();
      return;
    }

    // Process the intent from DeepLinkNotificationReceiver. Bring the routing screen back to the
    // top if any other screens were pushed onto it.
    Uri uri = intent.getData();
    if (uri != null
        && URI_SCHEME.equals(uri.getScheme())
        && URI_HOST.equals(uri.getSchemeSpecificPart())) {

      Screen top = screenManager.getTop();
      switch (uri.getFragment()) {
        case DeepLinkNotificationReceiver.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP:
          if (!(top instanceof NavigationScreen)) {
            screenManager.popTo(Screen.ROOT);
          }
          break;
        default:
          // No-op
      }
    }
  }

  @Override
  public void onCarConfigurationChanged(@NonNull Configuration newConfiguration) {
    mNavigationCarSurface.onCarConfigurationChanged();
  }

  @Override
  public void executeScript(@NonNull List<Instruction> instructions) {
    if (mService != null) {
      mService.executeInstructions(instructions);
    }
  }

  @Override
  public void stopNavigation() {
    if (mService != null) {
      mService.stopNavigation();
    }
  }
}
