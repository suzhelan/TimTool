package top.sacz.timtool.hook

import android.content.Context
import android.content.ContextWrapper
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import top.sacz.timtool.hook.common.CommonMethod

/**
 * 模块入口
 */

private const val TARGET_PACKAGE = "com.tencent.tim"
private const val TAG = "[Tim助手]"

class InjectHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == TARGET_PACKAGE && lpparam.isFirstApplication) {
            // Init EzXHelper
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag(TAG)
            EzXHelper.setToastTag(TAG)
            // Init hooks
            initHook(lpparam)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        EzXHelper.initZygote(startupParam)
        HookEnv.getInstance().moduleApkPath = startupParam.modulePath
    }

    private fun initHook(loadPackageParam: LoadPackageParam) {
        val createContextMethod = CommonMethod.getContextCreateMethod(loadPackageParam)
        XposedBridge.hookMethod(createContextMethod, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val context: Context = param.thisObject as ContextWrapper
                val hookSteps = HookSteps()
                hookSteps.initContext(context)
            }
        })
    }

}