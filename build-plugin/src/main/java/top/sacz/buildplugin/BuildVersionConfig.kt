package top.sacz.buildplugin

import org.gradle.api.JavaVersion

/*＊
 * 构建配置
 ＊*/
object BuildVersionConfig {
    const val applicationId = "top.sacz.timtool"
    val javaVersion = JavaVersion.VERSION_17
    const val kotlin = "17"
    const val compileSdk = 35
    const val targetSdk = 35
    const val minSdk = 27
}
