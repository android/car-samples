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

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the templated app.
 *
 * <p>{@link CarAppService} is the main interface between the app and Android Auto. For more
 * details, see the <a href="https://developer.android.com/training/cars/navigation">Android for
 * Cars Library developer guide</a>.
 */
public final class NavigationCarAppService extends CarAppService
    implements NavigationScreen.Listener {
  private static final String TAG = NavigationCarAppService.class.getSimpleName();

  public static final String CHANNEL_ID = "NavigationCarAppServiceChannel";

  /** The identifier for the notification displayed for the foreground service. */
  private static final int NOTIFICATION_ID = 97654321;

  private static final String URI_SCHEME = "samples";
  private static final String URI_HOST = "navigation";

  @Nullable private SurfaceRenderer mNavigationCarSurface;

  // A reference to the navigation service used to get location updates and routing.
  @Nullable private NavigationService mService;

  @Nullable private NavigationScreen mNavigationScreen;
  @NonNull private Action mSettingsAction;

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
            @Nullable Distance nextStepRemainingDistance,
            boolean shouldShowNextStep,
            boolean shouldShowLanes,
            @Nullable CarIcon junctionImage) {
          mNavigationScreen.updateTrip(
              isNavigating,
              isRerouting,
              hasArrived,
              destinations,
              steps,
              nextDestinationTravelEstimate,
              nextStepRemainingDistance,
              shouldShowNextStep,
              shouldShowLanes,
              junctionImage);
        }
      };

  // A listener to periodically update the surface with the location coordinates
  private LocationListener mLocationListener =
      new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
          mNavigationCarSurface.updateLocationString(getLocationString(location));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
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

          stopForeground(true);
          LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
          locationManager.removeUpdates(mLocationListener);
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

    mSettingsAction =
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
    mNavigationScreen =
        new NavigationScreen(getCarContext(), mSettingsAction, this, mNavigationCarSurface);

    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      CarToast.makeText(
              getCarContext(), "Navigation intent: " + intent.getDataString(), CarToast.LENGTH_LONG)
          .show();
    }

    createNotificationChannel();

    // Turn the car app service into a foreground service in order to make sure we can use all
    // granted "while-in-use" permissions (e.g. location) in the app's process.
    // The "while-in-use" location permission is granted as long as there is a foreground service
    // running in a process in which location access takes place. Here, we set this service, and not
    // NavigationService (which runs only during navigation), as a foreground service because we
    // need location access even when not navigating. If location access is needed only during
    // navigation, we can set NavigationService as a foreground service instead.
    // See
    // https://developer.android.com/reference/com/google/android/libraries/car/app/CarAppService#accessing-location for more details.
    startForeground(NOTIFICATION_ID, getNotification());

    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      requestLocationUpdates();
    } else {
      // If we do not have the location permission, preseed the navigation screen first, and push
      // the request permission screen. When the user grants the location permission, the request
      // permission screen will be popped and the navigation screen will be displayed.
      getCarContext().getCarService(ScreenManager.class).push(mNavigationScreen);
      return new RequestPermissionScreen(
          getCarContext(),
          () -> {
            requestLocationUpdates();
          });
    }

    return mNavigationScreen;
  }

  @Override
  public void onNewIntent(@NonNull Intent intent) {
    Log.i(TAG, "In onNewIntent() " + intent);
    ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      Uri uri = Uri.parse("http:///" + intent.getDataString());
      screenManager.popTo(Screen.ROOT);
      screenManager.pushForResult(
          new SearchResultsScreen(
              getCarContext(), mSettingsAction, mNavigationCarSurface, uri.getQueryParameter("q")),
          (obj) -> {
            if (obj != null) {
              // Need to copy over each element to satisfy Java type safety.
              List<?> results = (List<?>) obj;
              List<Instruction> instructions = new ArrayList<Instruction>();
              for (Object result : results) {
                instructions.add((Instruction) result);
              }
              executeScript(instructions);
            }
          });

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

  private void createNotificationChannel() {
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "Car App Service";
      NotificationChannel serviceChannel =
          new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(serviceChannel);
    }
  }

  /** Returns the {@link NotificationCompat} used as part of the foreground service. */
  private Notification getNotification() {
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Navigation App")
            .setContentText("App is running")
            .setSmallIcon(R.drawable.ic_launcher);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID);
      builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
    }
    return builder.build();
  }

  /**
   * Requests location updates for the navigation surface.
   *
   * @throws java.lang.SecurityException if the app does not have the location permission.
   */
  private void requestLocationUpdates() {
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    mNavigationCarSurface.updateLocationString(getLocationString(location));
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        /* minTimeMs= */ 1000,
        /* minDistanceM= */ 1,
        mLocationListener);
  }

  private static String getLocationString(@Nullable Location location) {
    if (location == null) {
      return "unknown";
    }
    return "time: "
        + location.getTime()
        + " lat: "
        + location.getLatitude()
        + " lng: "
        + location.getLongitude();
  }
}
