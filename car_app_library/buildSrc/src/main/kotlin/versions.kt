import org.gradle.api.JavaVersion

object ver {
    object build {
        const val min_sdk = 29
        const val compile_sdk = 34
        val java_compatibility = JavaVersion.VERSION_17
        const val versionCode = 1
        val versionName = "1.0"
    }
}