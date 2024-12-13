package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.toMethod
import top.sacz.xphelper.reflect.FieldUtils

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.ShowMsgCount
 */
@HookItem("辅助功能/聊天/显示消息具体数量")
class ShowMsgDetailCount : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        //群消息
        hookBefore("Lcom/tencent/mobileqq/quibadge/QUIBadge;->updateNum(I)V".toMethod()) { param ->
            val num = param.args[0] as Int
            FieldUtils.setField(param.thisObject, "mNum", num)
            FieldUtils.setField(param.thisObject, "mText", num.toString())
            param.result = null
        }
        //总消息
        hookBefore("Lcom/tencent/widget/b;->a(Lcom/tencent/mobileqq/quibadge/QUIBadge;IIILjava/lang/String;)V".toMethod()) { param ->
            param.args[3] = Int.MAX_VALUE
        }
    }
}