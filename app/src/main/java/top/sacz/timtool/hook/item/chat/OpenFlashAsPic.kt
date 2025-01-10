package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.callMethod
import top.sacz.timtool.hook.util.setFieldValue
import top.sacz.timtool.hook.util.toMethod

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.FlashPicHook
 */
@HookItem("辅助功能/图片与表情/以图片方式打开闪照")
class OpenFlashAsPic : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "某种意外情况下可能导致聊天界面卡顿"
    }

    override fun loadHook(loader: ClassLoader) {
        hookAfter("Lcom/tencent/mobileqq/aio/msg/AIOMsgItem;->getMsgRecord()Lcom/tencent/qqnt/kernel/nativeinterface/MsgRecord;".toMethod()) { param ->
            val msgRecord = param.result
            val subMsgType: Int = msgRecord.callMethod("getSubMsgType")
            if ((subMsgType and 8192) != 0) {
                msgRecord.setFieldValue("subMsgType", subMsgType and 8192.inv())
            }
        }
    }
}
