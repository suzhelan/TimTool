package top.sacz.timtool.hook.item.qzone

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.MethodUtils


@HookItem("辅助功能/QQ空间/隐藏QQ空间广告")
class HideQZoneAD : BaseSwitchFunctionHookItem() {

    override fun loadHook(loader: ClassLoader) {
        //假装广告正在播放
        val method =
            MethodUtils.create("com.qzone.proxy.feedcomponent.model.gdt.QZoneAdFeedDataExtKt")
                .methodName("isShowingRecommendAd")
                .params(ClassUtils.findClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData"))
                .returnType(Boolean::class.java)
                .first()
        hookBefore(method) { param ->
            param.result = true
        }
    }

}