package top.sacz.timtool.hook.item.file

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import com.kongzue.dialogx.dialogs.MessageMenu
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.ui.activity.ChooseAgentActivity

@HookItem("辅助功能/文件/可选系统文件选择器")
class SystemFileSelector : BaseSwitchFunctionHookItem() {

    fun onStartActivityIntent(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        if (intent.component?.className?.contains("filemanager.activity.FMActivity") == true &&
            (!intent.getBooleanExtra("is_decorated", false))
        ) {
            val context = param.thisObject as Context
            val targetUin: Long = try {
                intent.getStringExtra("targetUin")?.toLong() ?: -1L
            } catch (e: NumberFormatException) {
                -1L
            }
            // note that old version of FMActivity has no targetUin
            if (targetUin in 1..9999) {
                // reserved for special usage
                return false
            }
            if ("guild" in Log.getStackTraceString(Throwable())) {
                // Filter out calls in the guild
                return false
            }
            val activityMap = mapOf(
                "系统文档" to Intent(context, ChooseAgentActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtras(intent)
                    type = "*/*"
                },
                "QQ 文件" to intent.apply {
                    putExtra("is_decorated", true)
                }
            )
            MessageMenu.build()
                .setTitle("文件选择器")
                .setMenuList(activityMap.keys.toTypedArray())
                .setOnMenuItemClickListener { dialog, text, index ->
                    ChooseAgentActivity.start(context, activityMap[text]!!)
                    false
                }.show()
            param.result = null
            return true
        }
        return false
    }

    override fun loadHook(loader: ClassLoader) {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                val intent: Intent = if (param.args[0] is Intent) {
                    param.args[0] as Intent
                } else {
                    param.args[1] as Intent
                }
                if (isEnabled) {
                    onStartActivityIntent(intent, param)
                }
            }
        }
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivityForResult", hook)
        XposedBridge.hookAllMethods(Activity::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(Activity::class.java, "startActivityForResult", hook)
    }
}