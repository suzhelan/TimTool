package top.sacz.timtool.hook

import android.content.Context
import android.os.Environment
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sacz.timtool.hook.core.HookItemLoader
import top.sacz.timtool.util.KvHelper
import top.sacz.xphelper.XpHelper

class HookSteps {

    fun initHandleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        HookEnv.getInstance()
            .setProcessName(loadPackageParam.processName)
            .setCurrentHostAppPackageName(loadPackageParam.packageName)
    }

    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        XpHelper.initZygote(startupParam)
        HookEnv.getInstance().setModuleApkPath(startupParam.modulePath)
    }

    fun initContext(context: Context) {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        HookEnv.getInstance()
            .setHostAppContext(context)
            .setHostApkPath(context.applicationInfo.sourceDir)
            .setVersionCode(packageInfo.versionCode)
            .setVersionName(packageInfo.versionName)
            .setHostClassLoader(context.classLoader)
        XpHelper.initContext(context)
        val dataDir =
            Environment.getExternalStorageDirectory().absolutePath + "/Android/data/" + HookEnv.getInstance().currentHostAppPackageName + "/Tim小助手"
        KvHelper.initialize(dataDir)
    }


    fun initHooks() {
        //环境初始化 开始进行hook项目的初始化
        if (HookEnv.getInstance().isMainProcess) {
            XposedBridge.log("[Tim小助手]环境初始化完成")
            val hookItemLoader = HookItemLoader()
            hookItemLoader.loadConfig()
            hookItemLoader.loadHookItem()
        }
    }
}