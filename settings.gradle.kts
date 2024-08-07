pluginManagement {
    repositories {
        google()
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

rootProject.name = "WikiHikingOsm"
include(":app")
include(":core:database")
include(":core:models")
include(":core:data")
include(":core:common")
include(":core:domain")
include(":feature:hikings")
