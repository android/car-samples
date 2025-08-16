package gradlePlugins

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApplicationPlugin : Plugin<Project> {

    private val Project.android: BaseExtension
        get() = extensions.findByName("android") as? BaseExtension
            ?: error("Not an Android module: $name")

    override fun apply(project: Project) =
        with(project) {
            val libs = project.rootProject
                .extensions
                .getByType(VersionCatalogsExtension::class.java)
                .named("libs")
            applyPlugins()
            androidConfig(libs)
            dependenciesConfig()
        }

    private fun Project.applyPlugins() {
        plugins.run {
            apply("com.android.application")
            apply("org.jetbrains.kotlin.android")
        }
    }

    private fun Project.androidConfig(libs: VersionCatalog) {
        val javaVer = JavaVersion.valueOf(libs.findVersion("java_compatibility").get().displayName)
        android.run {
            compileSdkVersion( libs.findVersion("compile_sdk").get().displayName.toInt())
            defaultConfig {
                minSdk = libs.findVersion("compile_sdk").get().displayName.toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                targetSdk = libs.findVersion("compile_sdk").get().displayName.toInt()
                versionCode = libs.findVersion("versionCode").get().displayName.toInt()
                versionName = libs.findVersion("versionName").get().displayName
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
//                isCoreLibraryDesugaringEnabled = true
                sourceCompatibility = javaVer
                targetCompatibility = javaVer
            }
            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions.jvmTarget = javaVer.toString()
            }
        }
    }
    private fun Project.dependenciesConfig() {
        dependencies {
//            "coreLibraryDesugaring"( "com.android.tools:desugar_jdk_libs:${ver.android.desugar}")
        }
    }
}