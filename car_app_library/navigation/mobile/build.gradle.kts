plugins {
    kotlin("android")
    id("android-application-module")
}

android {
    namespace = "androidx.car.app.sample.navigation"
    defaultConfig {
        applicationId = "androidx.car.app.sample.navigation"
    }
}

dependencies {
    implementation(libs.androidx.car.projected)
    implementation(project(":car_app_library:navigation:shared"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
  }