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
        maven { url = uri("https://www.jitpack.io" ) }
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
include(":core:designsystem")
include(":feature:routes")
