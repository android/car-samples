plugins {
//    kotlin("android")
    id("android-library-module")
}

android {
    namespace = "androidx.car.app.sample.navigation.common"
}

dependencies {
    implementation( libs.androidx.core)
    implementation( libs.androidx.car.app)

    implementation( libs.androidx.constraintlayout)
//    implementation "androidx.annotation:annotation-experimental:1.0.0"
}