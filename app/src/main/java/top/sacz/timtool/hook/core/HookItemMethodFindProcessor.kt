package top.sacz.timtool.hook.core

import android.app.Activity
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.TimVersion
import top.sacz.timtool.hook.base.BaseHookItem
import top.sacz.timtool.hook.base.IMethodFinder
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.hook.util.ToastTool.show
import top.sacz.timtool.util.KvHelper
import top.sacz.xphelper.dexkit.DexFinder

class HookItemMethodFindProcessor {
    private val flag = "FLAG"
    fun isDataExpire(): Boolean {
        val key = TimVersion.getVersionName() + "_" + BuildConfig.VERSION_NAME
        val kvHelper = KvHelper("MethodFindProcessor")
        return key != kvHelper.getString(flag, "")
    }

    fun init() {
        //需要有activity才能展示查找方法的动画 所以等activity创建再开始查找
        XposedHelpers.findAndHookMethod(Activity::class.java, "onResume", object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                startFind()
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startFind() {
        show("开始查找方法")
        //Dispatchers.Default CPU密集型操作，比如一些计算型任务
        GlobalScope.launch(Dispatchers.Default) {
            DexFinder.create(HookEnv.getHostApkPath())
            HookItemFactory.getAllItemList()
                .forEach { hookItem: BaseHookItem ->
                    if (hookItem is IMethodFinder) {
                        hookItem.find()
                    }
                }
            DexFinder.close()
            val key = TimVersion.getVersionName() + "_" + BuildConfig.VERSION_NAME
            val kvHelper = KvHelper("MethodFindProcessor")
            kvHelper.put(flag, key)
            show("查找方法完成")
        }
    }

    private fun scanConfigMethod() {
        HookItemFactory.getAllItemList()
            .forEach { hookItem: BaseHookItem ->
                if (hookItem is IMethodFinder) {
                    hookItem.find()
                }
            }
    }
}