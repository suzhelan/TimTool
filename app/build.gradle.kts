import top.sacz.buildplugin.BuildVersionConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = BuildVersionConfig.applicationId
    compileSdk = BuildVersionConfig.compileSdk

    defaultConfig {
        applicationId = BuildVersionConfig.applicationId
        minSdk = BuildVersionConfig.minSdk
        targetSdk = BuildVersionConfig.targetSdk
        versionCode = 33
        versionName = "3.3"

        buildConfigField("long", "BUILD_TIME", System.currentTimeMillis().toString())

        signingConfigs {
            // 需要在buildTypes主动引用 暂时不需要
            create("release") {
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }

        ndk {
            abiFilters.add("arm64-v8a") // 只编译arm64 v8a的lib, 因为qq只支持arm64 v8a
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    androidResources {
        additionalParameters.addAll(listOf("--allow-reserved-package-id", "--package-id", "0x42"))
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = BuildVersionConfig.javaVersion
        targetCompatibility = BuildVersionConfig.javaVersion
    }
    kotlin {
        jvmToolchain(BuildVersionConfig.kotlin.toInt())
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach {
            val output = it as? com.android.build.api.variant.impl.VariantOutputImpl ?: return@forEach
            output.outputFileName.set("Tim小助手_${output.versionName.get()}-${variant.buildType}.apk")
        }
    }
}

dependencies {
    // 安卓依赖
    implementation(libs.material)
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    // MiuiX
    implementation(libs.miuix.android)
    implementation(libs.miuix.icons.android)
    implementation(libs.miuix.preference.android)

    // FastJson
    implementation(libs.fastjson2)
    implementation(libs.fastjson2.kotlin)
    // 图片加载库
    implementation(libs.glide)
    // 键值存储库
    implementation(libs.fastkv)
    // 动态字节库
    implementation(libs.byte.buddy.android)
    // 网络库
    implementation(libs.okhttp3)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.kotlinx.serialization)
    // 标准库
    implementation(libs.kotlin.stdlib)
    // 反射库
    implementation(libs.kotlin.reflect)
    // 适配器库
    implementation(libs.base.recyclerview.helper)

    // 防撤回 ProtoBuf 解析库
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)
    // 动态解析 ProtoBuf
    implementation(libs.protobuf.java)

    // XpHelper
    implementation(libs.xphelper)

    // DialogX
    implementation(libs.suzhelan.dialogx)
    implementation(libs.suzhelan.dialogx.materialstyle)

    // 注解处理器
    ksp(project(":annotation-scanner"))

    // Xposed Api
    compileOnly(libs.xposed.api)
}