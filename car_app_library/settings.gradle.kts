pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
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
            version("carApp", "1.4.0" )
            version("coreKtx","1.10.1")
            version("junit","4.13.2")
            version("junitVersion","1.1.5")
            version("espressoCore","3.5.1")
            version("appcompat","1.6.1")
            version("material","1.10.0")
            version("activity","1.8.0")
            version("constraintlayout","2.1.4")
            version("media","1.6.0")
            version("guava","28.1-jre")
            library("androidx-car-projected","androidx.car.app","app-projected").versionRef("carApp")
            library("androidx-car-automotive","androidx.car.app","app-automotive").versionRef("carApp")
            library("androidx-car-app","androidx.car.app","app").versionRef("carApp")
            library("androidx-core","androidx.core","core").versionRef("coreKtx")
            library("androidx-core-ktx","androidx.core","core-ktx").versionRef("coreKtx")
            library("junit","junit","junit").versionRef("junit")
            library("androidx-junit","androidx.test.ext","junit").versionRef("junitVersion")
            library("androidx-espresso-core","androidx.test.espresso","espresso-core").versionRef("espressoCore")
            library("androidx-appcompat","androidx.appcompat","appcompat").versionRef("appcompat")
            library("material","com.google.android.material","material").versionRef("material")
            library("androidx-activity","androidx.activity","activity").versionRef("activity")
            library("androidx-constraintlayout","androidx.constraintlayout","constraintlayout").versionRef("constraintlayout")
            library("androidx-media","androidx.media","media").versionRef("media")
            library("google-guava","com.google.guava","guava").versionRef("guava")
        }
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":buildSrc:testClasses"))
rootProject.name = "Car_App_Library"

include(":helloworld:mobile")
include(":helloworld:automotive")
include(":helloworld:shared")
include(":navigation:mobile")
include(":navigation:automotive")
include(":navigation:shared")
include(":places:mobile")
include(":places:automotive")
include(":places:shared")
include(":showcase:mobile")
include(":showcase:automotive")
include(":showcase:shared")