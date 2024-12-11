package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.MethodUtils

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.ShowMsgCount
 */
@HookItem("辅助功能/聊天/显示消息具体数量")
class ShowMsgDetailCount : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        //群消息
        val updateNumMethod = MethodUtils.create("com.tencent.mobileqq.quibadge.QUIBadge")
            .returnType(Void.TYPE)
            .methodName("updateNum")
            .params(Int::class.java)
            .first()
        hookBefore(updateNumMethod) { param ->
            val num = param.args[0] as Int
            FieldUtils.setField(param.thisObject, "mNum", num)
            FieldUtils.setField(param.thisObject, "mText", num.toString())
            param.result = null
        }
        //总消息
        val updateCustomTxtMethod = MethodUtils.create("com.tencent.widget.b")
            .returnType(Void.TYPE)
            .methodName("a")
            .params(
                loader.loadClass("com.tencent.mobileqq.quibadge.QUIBadge"),
                Int::class.java,
                Int::class.java,
                Int::class.java,
                String::class.java
            )
            .first()
        hookBefore(updateCustomTxtMethod) { param ->
            param.args[3] = Int.MAX_VALUE
        }
    }
}