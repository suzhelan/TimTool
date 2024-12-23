
import com.google.protobuf.gradle.proto
import top.sacz.buildplugin.BuildVersionConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
}

android {
    namespace = BuildVersionConfig.applicationId
    compileSdk = BuildVersionConfig.compileSdk

    defaultConfig {

        applicationId = BuildVersionConfig.applicationId
        minSdk = BuildVersionConfig.minSdk
        targetSdk = BuildVersionConfig.targetSdk
        versionCode = 19
        versionName = "1.9"

        ndk {
            //只支持arm64 v8a的lib so库,因为qq只支持arm64 v8a
            abiFilters.add("arm64-v8a")
        }

        buildConfigField("String", "BUILD_GIT_VERSION", "\"${getGitVersion()}\"")
        buildConfigField("long", "BUILD_TIMESTAMP", "${System.currentTimeMillis()}L")

        signingConfigs {
            //需要在buildTypes主动引用,暂时不需要
            create("release") {
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
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
        sourceCompatibility = BuildVersionConfig.javaVersion
        targetCompatibility = BuildVersionConfig.javaVersion
    }

    android.applicationVariants.all {
        outputs.all {
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                outputFileName = "${rootProject.name}_${this.name}_${versionName}.apk"
            }
        }
    }

    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x42"
        )
    }

    kotlinOptions {
        jvmTarget = BuildVersionConfig.kotlin
    }

    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        named("main") {
            proto {
                srcDirs("src/main/proto")
            }
        }
    }

}

fun getGitVersion(): String {
    return try {
        val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
        process.inputStream.bufferedReader().use { it.readText().trim() }
    } catch (e: Exception) {
        "unknown"
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


    implementation(libs.byte.buddy.android)
    //常用
    implementation(libs.okhttp3)
    implementation(libs.retrofit2)
    implementation(libs.squareup.retrofit.converter.gson)
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