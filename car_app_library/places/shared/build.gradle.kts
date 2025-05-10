plugins {
//    kotlin("android")
    id("android-library-module")
}

android {
    namespace = "androidx.car.app.sample.places.common"
}

dependencies {
    implementation( libs.androidx.core)
    implementation( libs.androidx.car.app)
    implementation( libs.google.guava)
}