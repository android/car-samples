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
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":buildSrc:testClasses"))
rootProject.name = "Car_Samples"

include(":car_app_library:helloworld:mobile")
include(":car_app_library:helloworld:automotive")
include(":car_app_library:helloworld:shared")
include(":car_app_library:navigation:mobile")
include(":car_app_library:navigation:automotive")
include(":car_app_library:navigation:shared")
include(":car_app_library:places:mobile")
include(":car_app_library:places:automotive")
include(":car_app_library:places:shared")
include(":car_app_library:showcase:mobile")
include(":car_app_library:showcase:automotive")
include(":car_app_library:showcase:shared")
//
include(":car-lib:CarGearViewerKotlin:automotive")