# Android for Cars App Library Samples

This directory contains sample apps that use the Android Auto car app SDK.

## Installing the Apps

### From Android Studio

This directory is an Android Studio project containing the sample apps written using the Android Auto car app SDK. In order to install the apps,

1. Open the project in Android Studio: File -> Open -> Select this directory and click OK.

2. Go to Run -> Edit Configurations, select your app, and in the General tab, under Launch Options, select Launch: Nothing, then click OK to close the dialog.

3. Select Run -> Run ‘your app’, to run the app, which will just install it in the selected device.

### From the Command Line

Run ./gradlew :sample:assemble to assemble the APKs. E.g.

```bash
./gradlew :showcase:assemble
```

The APK should be generated under your app’s build directory, e.g. your_app/build/outputs/apk/debug.


Install the APK with ADB:

```bash
adb install -t path_to_your_apk
```



## Run the Apps in the Desktop Head Unit (DHU)

Follow the instructions in [Test Android apps for cars][1] to run the sample apps in the DHU.

In short, do the following:

* [Enable the Android Developer Settings][2]
* [Enable Unknown Sources in Android Auto][3]
* [Run the DHU][4]

**Note**: In Android Q, there is no Android Auto app in the launcher. The way to get to the settings in that case is through Settings -> Apps & Notifications -> See all apps -> Android Auto -> Advanced -> Additional settings in the app.


[1]: https://developer.android.com/training/cars/testing
[2]: https://developer.android.com/studio/debug/dev-options
[3]: https://developer.android.com/training/cars/testing#step1
[4]: https://developer.android.com/training/cars/testing#running-dhu
