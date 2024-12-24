package top.sacz.timtool.hook.item.chat

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem

/**
 * 核心思路来自 https://github.com/cinit/QAuxiliary -> me.singleneuron.hook.decorator.FxxkQQBrowser
 */
@HookItem("辅助功能/聊天/禁用内置浏览器")
class DisableTimBrowser : BaseSwitchFunctionHookItem() {

    override fun getTip(): String {
        return "打开非QQ官方网页时使用外部浏览器打开"
    }

    val flag = "TIM_TOOL_URL"

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

    private fun shouldUseInternalBrowserForUrl(url: String): Boolean {
        val body = if (url.contains("://")) {
            url.substring(url.indexOf("://") + 3)
        } else {
            url
        }.dropWhile { it == '/' } // https:///ti.qq.com 前面有多个/不影响跳转，给腾讯擦屁股
        val host = if (body.contains("/")) {
            body.substring(0, body.indexOf("/"))
        } else {
            body
        }.lowercase()
        return host.endsWith("qq.com")
                || host.endsWith("tenpay.com")
                || host.endsWith("meeting.tencent.com")
                || host == "qq-web.cdn-go.cn" // for CAPTCHA https://qq-web.cdn-go.cn/captcha_cdn-go/latest/captcha.html
    }

    fun onStartActivityIntent(intent: Intent, param: XC_MethodHook.MethodHookParam) {
        if (intent.getBooleanExtra(flag, false)) return

        val url = intent.getStringExtra("url")
        if (!url.isNullOrBlank()
            && url.lowercase().let { it.startsWith("http://") || it.startsWith("https://") }
            && !shouldUseInternalBrowserForUrl(url)
            && intent.component?.shortClassName?.contains("QQBrowserActivity") == true
        ) {
            val context = param.thisObject as Context
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            context.startActivity(intent)

            param.result = null
            true
        }
    }
}