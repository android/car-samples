plugins {
    kotlin("android")
    id("android-application-module")
}

android {
    namespace = "androidx.car.app.sample.showcase"
    defaultConfig {
        applicationId = "androidx.car.app.sample.showcase"
    }
}

dependencies {
    implementation(libs.androidx.car.automotive)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(project(":car_app_library:showcase:shared"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}