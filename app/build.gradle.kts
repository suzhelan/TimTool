plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "top.sacz.timtool"
    compileSdk = 35

    defaultConfig {
        applicationId = "top.sacz.timtool"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        ndk {
            //只支持arm64 v8a的lib so库,因为qq只支持arm64 v8a
            abiFilters.add("arm64-v8a")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x48"
        )
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    //xposed
    compileOnly(libs.xposed.api)
    implementation(libs.dexkit)

    //注解扫描器
    ksp(project(":annotation-scanner"))

    //自己写的小工具 包含一些常用功能 反射工具 注入act res等
    implementation(project(":xp-helper"))
    //常用
    implementation(libs.okhttp3)
    implementation(libs.glide)
    implementation(libs.fastkv)
    implementation(libs.fastjson2)
    implementation(libs.base.recyclerview.helper)
}