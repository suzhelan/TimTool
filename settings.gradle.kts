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
        mavenLocal()
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        maven { url = uri("https://dl.bintray.com/ppartisan/maven/") }
        maven { url = uri("https://clojars.org/repo/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info/") }
    }
}

includeBuild("build-plugin")

rootProject.name = "Tim小助手"
include(":app")

include(":annotation-scanner")
project(":annotation-scanner").projectDir = file("./libs/annotation-scanner")

include(":xp-helper")
project(":xp-helper").projectDir = file("./libs/xp-helper")
