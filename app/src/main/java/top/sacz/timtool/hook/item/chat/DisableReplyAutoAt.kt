package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.method

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.ui.chat.ReplyNoAtHook
 */
@HookItem("辅助功能/聊天/禁止回复自动艾特")
class DisableReplyAutoAt : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        hookBefore("Lcom/tencent/mobileqq/aio/input/reply/c;->r(Lcom/tencent/mobileqq/aio/msg/AIOMsgItem;)V".method()) { param ->
            param.result = null
        }
    }
}