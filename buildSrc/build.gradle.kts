plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.jetbrains.kotlin)
    implementation( libs.android.build.gradle)
}

gradlePlugin {
    plugins {
        create("android-library-module") {
            id = "android-library-module"
            implementationClass = "gradlePlugins.AndroidLibraryPlugin"
        }
        create("android-application-module"){
            id = "android-application-module"
            implementationClass = "gradlePlugins.AndroidApplicationPlugin"
        }
    }
}