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
        versionCode = 27
        versionName = "2.7"

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
    kotlin {
        jvmToolchain(BuildVersionConfig.kotlin.toInt())
    }

    androidResources {
        additionalParameters += arrayOf(
            "--allow-reserved-package-id",
            "--package-id", "0x42"
        )
    }

    buildFeatures {
        viewBinding = true
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        getByName("main") {

            proto {
                srcDirs("src/main/proto")
            }
        }
    }
}

/**
 * 自定义构建好的文件名
 */
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach {
            val output = it as? com.android.build.api.variant.impl.VariantOutputImpl
            if (output == null) {
                return@forEach
            }
            output.outputFileName.set("Tim小助手_${output.versionName.get()}-${variant.buildType}.apk")
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

    //注解扫描器
    ksp(project(":annotation-scanner"))

    //自己写的小工具 包含一些常用功能 反射工具 注入act res等 dexkit等
    implementation(libs.xphelper)


    implementation(libs.byte.buddy.android)
    //常用
    implementation(libs.okhttp3)
    implementation(libs.retrofit2)
    implementation(libs.glide)
    implementation(libs.fastkv)
    implementation(libs.fastjson2)
    implementation(libs.fastjson2.kotlin)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
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
