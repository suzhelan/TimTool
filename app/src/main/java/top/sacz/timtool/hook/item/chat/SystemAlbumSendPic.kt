package top.sacz.timtool.hook.item.chat

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.kongzue.dialogx.dialogs.MessageMenu
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.ui.activity.ChooseAgentActivity

@HookItem("辅助功能/文件/可选使用系统相册发送图片")
class SystemAlbumSendPic : BaseSwitchFunctionHookItem() {
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

    fun onStartActivityIntent(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        if (intent.component?.className?.contains("com.tencent.qqnt.qbasealbum.WinkHomeActivity") == true
            && intent.getIntExtra("key_chat_type", -1) != -1
            && intent.getBooleanExtra("is_decorated", false) == false
        ) {
            // must use Activity context as base context to show dialog window
            val uin = intent.getStringExtra("uin")
            if (uin.toString().length > 10) {
                // Filter out calls in the guild
                return true
            }
            val context = param.thisObject as Context
            val activityMap = mapOf(
                "系统相册" to Intent(context, ChooseAgentActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("use_ACTION_PICK", true)
                    putExtras(intent)
                    type = "image/*"
                },
                "系统文档" to Intent(context, ChooseAgentActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtras(intent)
                    type = "image/*"
                },
                "QQ 相册" to intent.apply {
                    putExtra("is_decorated", true)
                }
            )
            MessageMenu.build()
                .setTitle("文件选择器")
                .setMenuList(activityMap.keys.toTypedArray())
                .setOnMenuItemClickListener { dialog, text, index ->
                    context.startActivity(activityMap[text])
                    false
                }.show()
            param.result = null
            return true
        }
        return false
    }
}