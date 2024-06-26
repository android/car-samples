plugins {
    kotlin("android")
    id("android-application-module")
}

android {
    namespace = "androidx.car.app.sample.places"
    defaultConfig {
        applicationId = "androidx.car.app.sample.places"
    }
}

dependencies {
    implementation(libs.androidx.car.projected)
    implementation(project(":places:shared"))
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
  }