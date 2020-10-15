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

package com.google.android.libraries.car.app.samples.showcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.libraries.car.app.CarAppService;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.Screen;
import com.google.android.libraries.car.app.ScreenManager;
import com.google.android.libraries.car.app.samples.showcase.misc.GoToPhoneScreen;
import com.google.android.libraries.car.app.samples.showcase.misc.PreSeedingFlowScreen;
import com.google.android.libraries.car.app.samples.showcase.misc.ReservationCancelledScreen;
import com.google.android.libraries.car.app.samples.showcase.navigation.NavigationNotificationsDemoScreen;
import com.google.android.libraries.car.app.samples.showcase.navigation.SurfaceRenderer;
import com.google.android.libraries.car.app.samples.showcase.navigation.routing.NavigatingDemoScreen;
import java.util.Objects;

/**
 * Entry point for the showcase app.
 *
 * <p>{@link CarAppService} is the main interface between the app and Android Auto. For more
 * details, see the <a href="https://developer.android.com/training/cars/navigation">Android for
 * Cars Library developer guide</a>.
 */
public final class ShowcaseService extends CarAppService implements DefaultLifecycleObserver {
  public static final String SHARED_PREF_KEY = "ShowcasePrefs";
  public static final String PRE_SEED_KEY = "PreSeed";

  private static final String URI_SCHEME = "samples";
  private static final String URI_HOST = "showcase";

  @Nullable private SurfaceRenderer mRenderer;

  private final BroadcastReceiver mReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          CarToast.makeText(getCarContext(), "Returned from phone", CarToast.LENGTH_LONG).show();
          getGoToPhoneScreenAndSetItAsTop().onPhoneFlowComplete();
        }
      };

  @NonNull
  @Override
  public Screen onCreateScreen(@NonNull Intent intent) {
    Lifecycle lifecycle = getLifecycle();
    lifecycle.addObserver(this);
    mRenderer = new SurfaceRenderer(getCarContext(), lifecycle);

    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      // Handle the navigation Intent by pushing first the "home" screen onto the stack, then
      // returning the screen that we want to show a template for.
      // Doing this allows the app to go back to the previous screen when the user clicks on a back
      // action.
      getCarContext().getCarService(ScreenManager.class).push(new StartScreen(getCarContext()));
      return new NavigatingDemoScreen(getCarContext());
    }

    // For demo purposes this uses a shared preference setting to store whether we should pre-seed
    // the screen back stack.  This allows the app to have a way to go back to the home/start screen
    // making the home/start screen the 0th position.
    // For a real application, it would probably check if it has all the needed system permissions,
    // and if any are missing, it would pre-seed the start screen and return a screen that will send
    // the user to the phone to grant the needed permissions.
    boolean shouldPreSeedBackStack =
        getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE).getBoolean(PRE_SEED_KEY, false);
    if (shouldPreSeedBackStack) {
      // Reset so that we don't require it next time
      getSharedPreferences(ShowcaseService.SHARED_PREF_KEY, Context.MODE_PRIVATE)
          .edit()
          .putBoolean(ShowcaseService.PRE_SEED_KEY, false)
          .apply();

      getCarContext().getCarService(ScreenManager.class).push(new StartScreen(getCarContext()));
      return new PreSeedingFlowScreen(getCarContext());
    }
    return new StartScreen(getCarContext());
  }

  @Override
  public void onStart(LifecycleOwner owner) {
    registerReceiver(mReceiver, new IntentFilter(GoToPhoneScreen.PHONE_COMPLETE_ACTION));
  }

  @Override
  public void onStop(LifecycleOwner owner) {
    unregisterReceiver(mReceiver);
  }

  @Override
  public void onNewIntent(@NonNull Intent intent) {
    // Process various deeplink intents.

    ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);

    if (CarContext.ACTION_NAVIGATE.equals(intent.getAction())) {
      // If the Intent is to navigate, and we aren't already, push the navigation screen.
      if (screenManager.getTop() instanceof NavigatingDemoScreen) {
        return;
      }
      screenManager.push(new NavigatingDemoScreen(getCarContext()));
      return;
    }

    Uri uri = intent.getData();
    if (uri != null
        && URI_SCHEME.equals(uri.getScheme())
        && URI_HOST.equals(uri.getSchemeSpecificPart())) {

      Screen top = screenManager.getTop();
      switch (uri.getFragment()) {
        case DeepLinkNotificationReceiver.INTENT_ACTION_PHONE:
          getGoToPhoneScreenAndSetItAsTop();
          break;
        case DeepLinkNotificationReceiver.INTENT_ACTION_CANCEL_RESERVATION:
          if (!(top instanceof ReservationCancelledScreen)) {
            screenManager.push(new ReservationCancelledScreen(getCarContext()));
          }
          break;
        case DeepLinkNotificationReceiver.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP:
          if (!(top instanceof NavigationNotificationsDemoScreen)) {
            screenManager.push(new NavigationNotificationsDemoScreen(getCarContext()));
          }
          break;
        default:
          // No-op
      }
    }
  }

  @Override
  public void onCarConfigurationChanged(@NonNull Configuration configuration) {
    if (mRenderer != null) {
      mRenderer.onCarConfigurationChanged();
    }
  }

  @Override
  public void onCarAppFinished() {
    Log.i("SHOWCASE", "onCarAppFinished");
  }

  public static Uri createDeepLinkUri(String deepLinkAction) {
    return Uri.fromParts(URI_SCHEME, URI_HOST, deepLinkAction);
  }

  private GoToPhoneScreen getGoToPhoneScreenAndSetItAsTop() {
    ScreenManager screenManager =
        Objects.requireNonNull(getCarContext().getCarService(ScreenManager.class));

    Screen top = screenManager.getTop();
    if (!(top instanceof GoToPhoneScreen)) {
      Screen newTop = new GoToPhoneScreen(getCarContext());
      screenManager.push(newTop);
      screenManager.remove(top);
      top = newTop;
    }
    return (GoToPhoneScreen) top;
  }
}
