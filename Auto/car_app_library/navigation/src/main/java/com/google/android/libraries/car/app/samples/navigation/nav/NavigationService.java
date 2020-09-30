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

package com.google.android.libraries.car.app.samples.navigation.nav;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static com.google.android.libraries.car.app.samples.navigation.nav.DeepLinkNotificationReceiver.INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.libraries.car.app.CarContext;
import com.google.android.libraries.car.app.CarToast;
import com.google.android.libraries.car.app.model.CarIcon;
import com.google.android.libraries.car.app.model.Distance;
import com.google.android.libraries.car.app.navigation.NavigationManager;
import com.google.android.libraries.car.app.navigation.NavigationManagerListener;
import com.google.android.libraries.car.app.navigation.model.Destination;
import com.google.android.libraries.car.app.navigation.model.Step;
import com.google.android.libraries.car.app.navigation.model.TravelEstimate;
import com.google.android.libraries.car.app.navigation.model.Trip;
import com.google.android.libraries.car.app.notification.CarAppExtender;
import com.google.android.libraries.car.app.samples.navigation.R;
import com.google.android.libraries.car.app.samples.navigation.app.MainActivity;
import com.google.android.libraries.car.app.samples.navigation.model.Instruction;
import com.google.android.libraries.car.app.samples.navigation.model.Script;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Foreground service to provide navigation directions. */
public class NavigationService extends Service {
  private static final String TAG = "NavigationService";

  public static final String CHANNEL_ID = "NavigationServiceChannel";

  /** The identifier for the notification displayed for the foreground service. */
  private static final int NOTIFICATION_ID = 87654321;

  // Constants for location broadcast
  private static final String PACKAGE_NAME =
      "com.google.android.libraries.car.app.samples.navigation.nav.navigationservice";

  private static final String EXTRA_STARTED_FROM_NOTIFICATION =
      PACKAGE_NAME + ".started_from_notification";
  public static final String CANCEL_ACTION = "CANCEL";

  private NotificationManager mNotificationManager;
  private final IBinder mBinder = new LocalBinder();

  private @Nullable CarContext mCarContext;
  private @Nullable Listener mListener;
  private @Nullable NavigationManager mNavigationManager;
  private boolean mIsNavigating;
  private int mStepsSent;

  private List<Destination> destinations = new ArrayList<>();
  private List<Step> steps = new ArrayList<>();
  private final Trip.Builder tripBuilder = Trip.builder();
  private @Nullable Script mScript;

  public interface Listener {
    void navigationStateChanged(
        boolean isNavigating,
        boolean isRerouting,
        boolean hasArrived,
        List<Destination> destinations,
        List<Step> steps,
        TravelEstimate nextDestinationTravelEstimate,
        Distance nextStepRemainingDistance,
        boolean shouldShowNextStep,
        boolean shouldShowLanes,
        @Nullable CarIcon junctionImage);
  }

  /**
   * Class used for the client Binder. Since this service runs in the same process as its clients,
   * we don't need to deal with IPC.
   */
  public class LocalBinder extends Binder {
    public NavigationService getService() {
      return NavigationService.this;
    }
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "In onCreate()");
    createNotificationChannel();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "In onStartCommand()");
    if (CANCEL_ACTION.equals(intent.getAction())) {
      stopNavigation();
    }
    return START_NOT_STICKY;
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return true; // Ensures onRebind() is called when a client re-binds.
  }

  public void setCarContext(CarContext carContext, Listener listener) {
    mCarContext = carContext;
    mNavigationManager = mCarContext.getCarService(NavigationManager.class);
    mNavigationManager.setListener(
        new NavigationManagerListener() {
          @Override
          public void stopNavigation() {
            NavigationService.this.stopNavigation();
          }

          @Override
          public void onAutoDriveEnabled() {
            CarToast.makeText(carContext, "Auto drive enabled", CarToast.LENGTH_LONG);
          }
        });
    mListener = listener;

    // Uncomment if navigating
    // mNavigationManager.navigationStarted();
  }

  public void clearCarContext() {
    mCarContext = null;
    mNavigationManager = null;
  }

  public void executeInstructions(List<Instruction> instructions) {
    mScript =
        Script.execute(
            instructions,
            (instruction) -> {
              switch (instruction.getType()) {
                case START_NAVIGATION:
                  startNavigation();
                  break;
                case END_NAVIGATION:
                  endNavigationFromScript();
                  break;
                case ADD_DESTINATION_NAVIGATION:
                  Destination destination = instruction.getDestination();
                  destinations.add(destination);
                  if (destinations.size() == 1) {
                    tripBuilder.clearDestinations();
                    tripBuilder.addDestination(destination);
                  }
                  break;
                case POP_DESTINATION_NAVIGATION:
                  destinations.remove(0);
                  if (destinations.size() > 0) {
                    tripBuilder.clearDestinations();
                    tripBuilder.addDestination(destinations.get(0));
                  }
                  break;
                case ADD_STEP_NAVIGATION:
                  Step step = instruction.getStep();
                  steps.add(step);
                  if (steps.size() == 1) {
                    tripBuilder.clearSteps();
                    tripBuilder.addStep(step);
                  }
                  break;
                case POP_STEP_NAVIGATION:
                  steps.remove(0);
                  tripBuilder.clearSteps();
                  if (steps.size() > 0) {
                    tripBuilder.addStep(steps.get(0));
                  }
                  break;
                case SET_TRIP_POSITION_NAVIGATION:
                  if (mIsNavigating) {
                    TravelEstimate destinationTravelEstimate =
                        instruction.getDestinationTravelEstimate();
                    TravelEstimate stepTravelEstimate = instruction.getStepTravelEstimate();
                    tripBuilder
                        .clearDestinationTravelEstimates()
                        .addDestinationTravelEstimate(destinationTravelEstimate)
                        .clearStepTravelEstimates()
                        .addStepTravelEstimate(stepTravelEstimate)
                        .setCurrentRoad(instruction.getRoad())
                        .setIsLoading(false);
                    mNavigationManager.updateTrip(tripBuilder.build());

                    if (++mStepsSent % 10 == 0) {
                      // For demo purposes only play audio of next turn every 10 steps.
                      playNavigationDirection(R.raw.turn_right);
                    }

                    update(
                        /* isNavigating= */ true,
                        /* isRerouting= */ false,
                        /* hasArrived= */ false,
                        destinations,
                        steps,
                        destinationTravelEstimate,
                        instruction.getStepRemainingDistance(),
                        instruction.getShouldNotify(),
                        instruction.getNotificationTitle(),
                        instruction.getNotificationContent(),
                        instruction.getNotificationIcon(),
                        instruction.getShouldShowNextStep(),
                        instruction.getShouldShowLanes(),
                        instruction.getJunctionImage());
                  }
                  break;
                case SET_REROUTING:
                  if (mIsNavigating) {
                    TravelEstimate destinationTravelEstimate =
                        instruction.getDestinationTravelEstimate();
                    tripBuilder
                        .clearDestinationTravelEstimates()
                        .addDestinationTravelEstimate(destinationTravelEstimate)
                        .clearSteps()
                        .clearStepTravelEstimates()
                        .setCurrentRoad(instruction.getRoad())
                        .setIsLoading(true);
                    mNavigationManager.updateTrip(tripBuilder.build());
                    update(
                        /* isNavigating= */ true,
                        /* isRerouting= */ true,
                        /* hasArrived= */ false,
                        null,
                        null,
                        null,
                        null,
                        instruction.getShouldNotify(),
                        instruction.getNotificationTitle(),
                        instruction.getNotificationContent(),
                        instruction.getNotificationIcon(),
                        instruction.getShouldShowNextStep(),
                        instruction.getShouldShowLanes(),
                        instruction.getJunctionImage());
                  }
                  break;
                case SET_ARRIVED:
                  if (mIsNavigating) {
                    update(
                        /* isNavigating= */ true,
                        /* isRerouting= */ false,
                        /* hasArrived= */ true,
                        destinations,
                        null,
                        null,
                        null,
                        instruction.getShouldNotify(),
                        instruction.getNotificationTitle(),
                        instruction.getNotificationContent(),
                        instruction.getNotificationIcon(),
                        instruction.getShouldShowNextStep(),
                        instruction.getShouldShowLanes(),
                        instruction.getJunctionImage());
                  }
                  break;
              }
            });
  }

  void update(
      boolean isNavigating,
      boolean isRerouting,
      boolean hasArrived,
      List<Destination> destinations,
      List<Step> steps,
      TravelEstimate nextDestinationTravelEstimate,
      Distance nextStepRemainingDistance,
      boolean shouldNotify,
      @Nullable String notificationTitle,
      @Nullable String notificationContent,
      int notificationIcon,
      boolean shouldShowNextStep,
      boolean shouldShowLanes,
      @Nullable CarIcon junctionImage) {
    if (mListener != null) {
      mListener.navigationStateChanged(
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

    if (mNotificationManager != null && !TextUtils.isEmpty(notificationTitle)) {
      mNotificationManager.notify(
          NOTIFICATION_ID,
          getNotification(
              shouldNotify, true, notificationTitle, notificationContent, notificationIcon));
    }
  }

  public boolean getIsNavigating() {
    return mIsNavigating;
  }

  public void startNavigation() {
    Log.i(TAG, "Starting Navigation");
    startService(new Intent(getApplicationContext(), NavigationService.class));

    Log.i(TAG, "Starting foreground service");
    startForeground(
        NOTIFICATION_ID,
        getNotification(
            true, false, getString(R.string.navigation_active), null, R.drawable.ic_launcher));

    if (mNavigationManager != null) {
      mNavigationManager.navigationStarted();
      mIsNavigating = true;
      mListener.navigationStateChanged(
          mIsNavigating,
          /* isRerouting= */ true,
          /* hasArrived= */ false,
          /* destinations= */ null,
          /* steps= */ null,
          /* nextDestinationTravelEstimate= */ null,
          /* nextStepRemainingDistance= */ null,
          /* shouldShowNextStep= */ false,
          /* shouldShowLanes= */ false,
          /* junctionImage= */ null);
    }
  }

  public void stopNavigation() {
    Log.i(TAG, "Stopping Navigation");
    if (mScript != null) {
      mScript.stop();
      destinations.clear();
      steps.clear();
      mScript = null;
    }

    if (mNavigationManager != null) {
      mNavigationManager.navigationEnded();
      mIsNavigating = false;
      mListener.navigationStateChanged(
          mIsNavigating,
          /* isRerouting= */ false,
          /* hasArrived= */ false,
          /* destinations= */ null,
          /* steps= */ null,
          /* nextDestinationTravelEstimate= */ null,
          /* nextStepRemainingDistance= */ null,
          /* shouldShowNextStep= */ false,
          /* shouldShowLanes= */ false,
          /* junctionImage= */ null);
    }
    stopForeground(true);
    stopSelf();
  }

  private void playNavigationDirection(@RawRes int resourceId) {
    CarContext carContext = mCarContext;
    if (carContext == null) {
      return;
    }

    MediaPlayer mediaPlayer = new MediaPlayer();

    // Use USAGE_ASSISTANCE_NAVIGATION_GUIDANCE as the usage type for any navigation related
    // audio, so that the audio will be played in the car speaker.
    AudioAttributes audioAttributes =
        new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .build();

    mediaPlayer.setAudioAttributes(audioAttributes);

    // Request audio focus with AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK so that it will duck ongoing
    // media.
    // Ducking will behave differently depending on what is playing, if it is music it will lower
    // the volume, if it is speech, it will pause it.
    AudioFocusRequest request =
        new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(audioAttributes)
            .build();

    AudioManager audioManager = carContext.getSystemService(AudioManager.class);

    mediaPlayer.setOnCompletionListener(
        player -> {
          try {
            // When the audio finishes playing: stop and release the media player.
            player.stop();
            player.release();
          } finally {
            // Release the audio focus so that any previously playing audio can continue.
            audioManager.abandonAudioFocusRequest(request);
          }
        });

    // Requesting the audio focus.
    if (audioManager.requestAudioFocus(request) != AUDIOFOCUS_REQUEST_GRANTED) {
      // If audio focus is not granted ignore it.
      return;
    }

    try {
      // Load our raw resource file, in the case where you synthesize the audio for the given
      // direction, just use that audio file.
      AssetFileDescriptor afd = carContext.getResources().openRawResourceFd(resourceId);
      mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
      afd.close();

      mediaPlayer.prepare();
    } catch (IOException e) {
      Log.e(TAG, "Failure loading audio resource", e);
      // Release the audio focus so that any previously playing audio can continue.
      audioManager.abandonAudioFocusRequest(request);
    }

    // Start the audio playback.
    mediaPlayer.start();
  }

  private void endNavigationFromScript() {
    stopNavigation();
  }

  private void createNotificationChannel() {
    mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.app_name);
      NotificationChannel serviceChannel =
          new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
      mNotificationManager.createNotificationChannel(serviceChannel);
    }
  }

  /** Returns the {@link NotificationCompat} used as part of the foreground service. */
  private Notification getNotification(
      boolean shouldNotify,
      boolean showInCar,
      CharSequence navigatingDisplayTitle,
      CharSequence navigatingDisplayContent,
      int notificationIcon) {
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(createMainActivityPendingIntent())
            // .addAction(0, "STOP", createStopPendingIntent())
            .setContentTitle(navigatingDisplayTitle)
            .setContentText(navigatingDisplayContent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            .setOnlyAlertOnce(!shouldNotify)
            .setSmallIcon(R.drawable.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), notificationIcon))
            .setTicker(navigatingDisplayTitle)
            .setWhen(System.currentTimeMillis());

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID);
      builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
    }
    if (showInCar) {
      builder
          .extend(
              CarAppExtender.builder()
                  .setImportance(NotificationManagerCompat.IMPORTANCE_HIGH)
                  .setContentIntent(

                      // Set an intent to open the car app. The app receives this intent when the
                      // user taps the heads-up notification or the rail widget.
                      PendingIntent.getBroadcast(
                          this,
                          INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP.hashCode(),
                          new Intent(INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP)
                              .setComponent(
                                  new ComponentName(
                                      mCarContext, DeepLinkNotificationReceiver.class)),
                          0))
                  .build())
          .build();
    }
    return builder.build();
  }

  private PendingIntent createStopPendingIntent() {
    Intent intent = new Intent(this, NavigationService.class);
    intent.setAction(CANCEL_ACTION);
    intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
    return PendingIntent.getService(this, 0, intent, 0);
  }

  private PendingIntent createMainActivityPendingIntent() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);
    return PendingIntent.getActivity(this, 0, intent, 0);
  }
}
