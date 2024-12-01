package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/表情/禁用表情下载限制")
class DownloadEmotion : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "新版QQ禁用了表情下载,开启了可以继续下载"
    }

    override fun loadHook(loader: ClassLoader) {
        val emotionDownloadDisableMethod =
            MethodUtils.create("com.tencent.mobileqq.emotionintegrate.m")
                .methodName("c")
                .returnType(Boolean::class.javaPrimitiveType)
                .first()
        hookBefore(emotionDownloadDisableMethod) { param ->
            param.result = false
        }

    }

}