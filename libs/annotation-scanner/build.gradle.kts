import top.sacz.buildplugin.BuildConfig

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.kotlinpoet.ksp)
}

kotlin {
    jvmToolchain(BuildConfig.kotlin.toInt())
}

