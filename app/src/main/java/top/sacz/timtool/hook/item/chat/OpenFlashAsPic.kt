package top.sacz.timtool.hook.item.chat

import de.robv.android.xposed.XposedHelpers
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.toMethod
import top.sacz.xphelper.reflect.FieldUtils

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.FlashPicHook
 */
@HookItem("辅助功能/聊天/以图片方式打开闪照")
class OpenFlashAsPic : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        hookAfter("Lcom/tencent/mobileqq/aio/msg/AIOMsgItem;->getMsgRecord()Lcom/tencent/qqnt/kernel/nativeinterface/MsgRecord;".toMethod()) { param ->
            val msgRecord = param.result
            val subMsgType = XposedHelpers.callMethod(msgRecord, "getSubMsgType") as Int
            if ((subMsgType and 8192) != 0) {
                FieldUtils.setField(msgRecord, "subMsgType", subMsgType and 8192.inv())
            }
        }
    }
}