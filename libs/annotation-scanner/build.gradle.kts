plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libs.ksp)
    implementation(libs.kotlinpoet.ksp)
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}