package top.sacz.timtool.hook

import android.app.Application
import android.content.Context
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sacz.timtool.hook.core.HookItemLoader
import top.sacz.timtool.hook.core.HookItemMethodFindProcessor
import top.sacz.timtool.hook.util.PathTool
import top.sacz.timtool.net.NewLoginTask
import top.sacz.timtool.net.UpdateService
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

    fun initContext(application: Application) {
        val context = application.baseContext
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val packageManager = context.packageManager
        val appName = packageManager.getApplicationLabel(context.applicationInfo).toString()
        HookEnv.setHostAppContext(context)
        HookEnv.setApplication(application)
        HookEnv.setHostApkPath(context.applicationInfo.sourceDir)
        HookEnv.setAppName(appName)
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
        DialogX.globalTheme = DialogX.THEME.AUTO
        DialogX.globalStyle = MaterialYouStyle()
    }

    fun initHooks() {
        //环境初始化 开始进行hook项目的初始化
        if (HookEnv.isMainProcess()) {
            val methodFindProcessor = HookItemMethodFindProcessor()
            if (methodFindProcessor.isDataExpire()) {
                methodFindProcessor.init()
                return
            }
            XposedBridge.log("[Tim小助手]环境初始化完成")
            //登录
            NewLoginTask().loginAndGetUserInfoAsync()
            //检查更新
            val service = UpdateService()
            service.requestUpdateAsyncAndToast()
        }
        val hookItemLoader = HookItemLoader()
        hookItemLoader.loadConfig()
        hookItemLoader.loadHookItem()
    }
}