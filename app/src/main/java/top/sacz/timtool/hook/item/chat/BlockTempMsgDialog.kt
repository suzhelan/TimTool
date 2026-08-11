package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/屏蔽临时会话弹窗")
class BlockTempMsgDialog : BaseSwitchFunctionHookItem() {

    override fun getTip(): String = "在没有开启临时会话的情况下查看已删除好友的聊天记录 则不会有开启临时会话弹窗"

    override fun loadHook(classLoader: ClassLoader) {
        val tempMsgManagerClass = try {
            classLoader.loadClass("com.tencent.mobileqq.managers.TempMsgManager")
        } catch (e: ClassNotFoundException) {
            return
        }

        val vMethod = MethodUtils.create(tempMsgManagerClass)
            .methodName("v")
            .params(String::class.java)
            .firstOrNull() ?: return

        hookBefore(vMethod) { param ->
            param.result = true
        }
    }
}
