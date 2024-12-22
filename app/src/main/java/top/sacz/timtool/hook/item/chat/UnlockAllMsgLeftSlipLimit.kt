package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.toMethod

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.hicore.hook.UnlockLeftSlipLimit
 */
@HookItem("辅助功能/聊天/解锁所有消息左滑回复限制")
class UnlockAllMsgLeftSlipLimit : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "如群公告可左滑回复"
    }

    override fun loadHook(loader: ClassLoader) {
        hookAfter("Lcom/tencent/mobileqq/ark/api/impl/ArkHelperImpl;->isSupportReply(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z".toMethod()) { param ->
            param.result = true
        }
    }
}