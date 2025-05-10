plugins {
//    kotlin("android")
    id("android-library-module")
}

android {
    namespace = "androidx.car.app.sample.showcase.common"
}

dependencies {
    implementation( libs.androidx.core)
    implementation( libs.androidx.car.app)
}