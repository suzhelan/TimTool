import com.google.protobuf.gradle.proto
import top.sacz.buildplugin.BuildConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "top.sacz.timtool"
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        applicationId = "top.sacz.timtool"
        minSdk = BuildConfig.minSdk
        //noinspection OldTargetApi
        targetSdk = BuildConfig.targetSdk
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
        sourceCompatibility = BuildConfig.javaVersion
        targetCompatibility = BuildConfig.javaVersion
    }

    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x42"
        )
    }

    kotlinOptions {
        jvmTarget = BuildConfig.kotlin
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        named("main") {
            res.srcDirs("src/main/res")
            proto {
                srcDirs("src/main/proto")
            }
        }
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
    //弹窗组件库
    implementation(project(":xpopup"))

    //常用
    implementation(libs.okhttp3)
    implementation(libs.glide)
    implementation(libs.fastkv)
    implementation(libs.fastjson2)
    implementation(libs.base.recyclerview.helper)

    //dialogx
    implementation(libs.suzhelan.dialogx)
    implementation(libs.suzhelan.dialogx.materialstyle)

    //防撤回用到的proto buf解析
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.protobuf.java)

//    implementation(project(":DialogX"))
//    implementation(project(":DialogXMaterialYou"))
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}