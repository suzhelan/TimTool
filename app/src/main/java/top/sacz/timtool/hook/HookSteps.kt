package top.sacz.timtool.hook

import android.content.Context
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sacz.timtool.hook.core.HookItemLoader
import top.sacz.timtool.hook.util.PathTool
import top.sacz.timtool.util.KvHelper
import top.sacz.xphelper.XpHelper

class HookSteps {

    fun initHandleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        HookEnv.setProcessName(loadPackageParam.processName)
        HookEnv.setCurrentHostAppPackageName(loadPackageParam.packageName)
    }

    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        XpHelper.initZygote(startupParam)
        HookEnv.setModuleApkPath(startupParam.modulePath)
    }

    fun initContext(context: Context) {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        HookEnv.setHostAppContext(context)
        HookEnv.setHostApkPath(context.applicationInfo.sourceDir)
        HookEnv.setVersionCode(packageInfo.versionCode)
        HookEnv.setVersionName(packageInfo.versionName)
        HookEnv.setHostClassLoader(context.classLoader)
        XpHelper.initContext(context)
        XpHelper.injectResourcesToContext(context)
        val dataDir = PathTool.getModuleDataPath() + "/data"
        KvHelper.initialize(dataDir)
        initDialogX(context)
    }

    private fun initDialogX(context: Context) {
        DialogX.init(context)
        DialogX.globalStyle = MaterialYouStyle()
        DialogX.dialogMaxWidth
    }

    fun initHooks() {
        //环境初始化 开始进行hook项目的初始化
        if (HookEnv.isMainProcess()) {
            XposedBridge.log("[Tim小助手]环境初始化完成")
        }
        val hookItemLoader = HookItemLoader()
        hookItemLoader.loadConfig()
        hookItemLoader.loadHookItem()
    }
}