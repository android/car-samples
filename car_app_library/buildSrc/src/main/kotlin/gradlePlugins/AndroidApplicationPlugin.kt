package gradlePlugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class AndroidApplicationPlugin : Plugin<Project> {

    private val Project.android: BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
            ?: error("Not an Android module: $name")

    override fun apply(project: Project) =
        with(project) {
            applyPlugins()
            androidConfig()
            dependenciesConfig()
        }

    private fun Project.applyPlugins() {
        plugins.run {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
    }

    private fun Project.androidConfig() {
        android.run {
            compileSdkVersion( ver.build.compile_sdk)
            defaultConfig {
                minSdk = ver.build.min_sdk
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                targetSdk = ver.build.compile_sdk
                versionCode = ver.build.versionCode
                versionName = ver.build.versionName
            }
            buildTypes {
                getByName("debug") {
                    isMinifyEnabled = false
                }
                getByName("release") {
                    isMinifyEnabled= false
                    consumerProguardFiles("consumer-rules.pro")
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
            compileOptions {
                sourceCompatibility = ver.build.java_compatibility
                targetCompatibility = ver.build.java_compatibility
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = ver.build.java_compatibility.toString()
            }
        }
    }
    private fun Project.dependenciesConfig() {
        dependencies {
//            "coreLibraryDesugaring"( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
        }
    }
}