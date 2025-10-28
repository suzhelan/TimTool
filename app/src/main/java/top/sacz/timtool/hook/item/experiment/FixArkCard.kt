package top.sacz.timtool.hook.item.experiment

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem

/**
 * 思路来自 TAssistant ，我自己能用就行
 */
@HookItem("辅助功能/实验性/修复小程序卡片无法打开")
class ForceArkCardDisplay : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        val arkComponentClass = loader.loadClass("com.tencent.mobileqq.aio.msglist.holder.component.ark.d")
        
        hookAfter(arkComponentClass.getDeclaredMethod("a", String::class.java, String::class.java)) { param ->
            param.result = true
        }
    }
}