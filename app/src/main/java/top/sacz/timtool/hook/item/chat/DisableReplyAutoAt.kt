package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.base.IMethodFinder
import top.sacz.timtool.hook.core.annotation.HookItem
import java.lang.reflect.Method

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.ui.chat.ReplyNoAtHook
 */
@HookItem("辅助功能/聊天/禁止回复自动艾特")
class DisableReplyAutoAt : BaseSwitchFunctionHookItem(), IMethodFinder {
    lateinit var method: Method

    override fun find() {
        method = buildMethodFinder()
            .searchPackages("com.tencent.mobileqq.aio.input.reply")
            .useString("msgItem.msgRecord.senderUid")
            .first()
    }

    override fun loadHook(loader: ClassLoader) {
        hookBefore(method) { param ->
            param.result = null
        }
    }
}