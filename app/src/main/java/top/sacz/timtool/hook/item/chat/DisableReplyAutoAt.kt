package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.ui.chat.ReplyNoAtHook
 */
@HookItem("辅助功能/聊天/禁止回复自动艾特")
class DisableReplyAutoAt : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        val replyMethod = MethodUtils.create("com.tencent.mobileqq.aio.input.reply.c")
            .returnType(Void.TYPE)
            .methodName("r")
            .params(loader.loadClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"))
            .first()
        hookBefore(replyMethod) { param ->
            param.result = null
        }
    }
}