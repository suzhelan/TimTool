package top.sacz.timtool.hook

import android.content.Context
import com.github.kyuubiran.ezxhelper.EzXHelper

class HookSteps {

    fun initContext(context: Context) {
        EzXHelper.classLoader = context.classLoader
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        HookEnv.getInstance()
            .setHostAppContext(context)
            .setProcessName(context.applicationInfo.processName)
            .setCurrentHostAppPackageName(context.packageName)
            .setHostApkPath(context.applicationInfo.sourceDir)
            .setVersionCode(packageInfo.versionCode)
            .setVersionName(packageInfo.versionName)
    }

}