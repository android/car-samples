pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
    buildscript {
        repositories {
            mavenCentral()
//            maven {
//                url = uri("https://storage.googleapis.com/r8-releases/raw")
//            }
        }
//        dependencies {
//            classpath("com.android.tools:r8:8.2.42")
//        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
