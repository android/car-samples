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