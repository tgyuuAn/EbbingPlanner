pluginManagement {
    includeBuild("build-logic")

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

rootProject.name = "EbbingPlanner"
include(":app")

include(":core:domain")
include(":core:data")
include(":core:designsystem")
include(":core:common-ui")
include(":core:common")
include(":core:network")
include(":core:navigation")
include(":core:database")
include(":core:datastore")

include(":feature:home")
include(":feature:dashboard")
include(":feature:setting")
include(":feature:tag")
include(":feature:onboarding")
include(":feature:memo")
include(":feature:repeatcycle")
include(":feature:alarm")

include(":baselineprofile")
include(":feature:sync")
