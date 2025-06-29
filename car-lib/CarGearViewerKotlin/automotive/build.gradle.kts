plugins {
    kotlin("android")
    id("android-application-module")
}

android {
    namespace = "com.example.cargearviewer"
    defaultConfig {
        applicationId = "com.example.cargearviewer"
    }
    // android.car exists since Android 10 (API level 29) Revision 5.
    useLibrary("android.car")
}

dependencies {
    implementation(libs.androidx.core.ktx)
  }
//dependencies {
//    implementation fileTree(include: ['*.jar'], dir: 'libs')
//    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
//    implementation 'androidx.core:core-ktx:1.3.1'
//}
