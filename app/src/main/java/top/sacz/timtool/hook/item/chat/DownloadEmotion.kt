package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils


@HookItem("辅助功能/表情与图片/禁用表情下载限制")
class DownloadEmotion : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "新版QQ禁用了表情下载,开启了可以继续下载"
    }

    override fun loadHook(loader: ClassLoader) {
        //有很多限制方法都走的这个接口
        val isSwitchOn =
            MethodUtils.create("com.tencent.mobileqq.activity.api.impl.UnitedConfigImpl")
                .methodName("isSwitchOn")
                .params(String::class.java, Boolean::class.javaObjectType)
                .returnType(Boolean::class.javaObjectType)
                .first()
        hookBefore(isSwitchOn) { param ->
            val cmd = param.args[0] as String
            if (cmd == "emotion_download_disable_8980_887036489") {
                param.result = false
            }
        }
    }

}