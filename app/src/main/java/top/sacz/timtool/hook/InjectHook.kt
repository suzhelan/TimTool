package top.sacz.timtool.hook

import android.app.Application
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import top.sacz.timtool.hook.common.CommonMethod

/**
 * 模块入口
 */
private val TIM_PACKAGE = HookEnv.TIM_PACKAGE

private const val TAG = "[Tim助手]"

class InjectHook : IXposedHookLoadPackage, IXposedHookZygoteInit {
    val hookSteps = HookSteps()

    /**
     * 标准注入入口
     */
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == TARGET_PACKAGE && lpparam.isFirstApplication) {
            // Init hook
            hookSteps.initHandleLoadPackage(lpparam)
            initHook(lpparam)
        }
    }

    /**
     * 模块加载路径
     */
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        hookSteps.initZygote(startupParam)
    }

    /**
     * hook app context onCreate 方法 可以过掉大部分加固
     */
    private fun initHook(loadPackageParam: LoadPackageParam) {
        val onCreateContextMethod = CommonMethod.getContextCreateMethod(loadPackageParam)
        XposedBridge.hookMethod(onCreateContextMethod, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val application = param.thisObject as Application
                //init env
                hookSteps.initContext(application)
                hookSteps.initHooks()
            }
        })
    }

}