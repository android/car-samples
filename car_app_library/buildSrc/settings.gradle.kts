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
            version("agp", "8.5.0" )
            version("kotlin","1.9.0")
            library("android-build-gradle", "com.android.tools.build", "gradle").versionRef("agp")
            library("jetbrains-kotlin","org.jetbrains.kotlin","kotlin-gradle-plugin").versionRef("kotlin")
        }
    }
}

rootProject.name = "buildSrc"
