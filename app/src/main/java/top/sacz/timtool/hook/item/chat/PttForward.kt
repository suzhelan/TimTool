package top.sacz.timtool.hook.item.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kongzue.dialogx.dialogs.MessageDialog
import de.robv.android.xposed.XC_MethodHook
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.OnMenuBuilder
import top.sacz.timtool.hook.item.api.QQCustomMenu
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.hook.util.callMethod
import top.sacz.timtool.hook.util.getFieldValue
import top.sacz.timtool.hook.util.toMethod
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.util.ActivityTools
import java.io.File

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.PttForwardHook
 */
@HookItem("辅助功能/聊天/语音长按菜单添加转发")
class PttForward : BaseSwitchFunctionHookItem(), OnMenuBuilder {
    override fun loadHook(loader: ClassLoader) {
        hookBefore("Lcom/tencent/mobileqq/forward/ForwardBaseOption;->buildConfirmDialog()V".toMethod(), 51) { param ->
            val activity = param.thisObject.getFieldValue<Activity>("mActivity")
            val extraData = param.thisObject.getFieldValue<Bundle>("mExtraData")
            val pttFilePath = extraData.getString("ptt_forward_path")
            if (pttFilePath != null && File(pttFilePath).exists()) {
                MessageDialog.build().apply {
                    setTitle("发送给 测试群")
                    setMessage("[语音转发] $pttFilePath")
                    setOkButton("发送") { _, _ ->
                        sendPttFile(pttFilePath)
                        activity.finish()
                        true
                    }
                    setCancelButton("取消")
                    setCancelable(false)
                }.show(activity)
            } else {
                ToastTool.show("语音文件不存在")
            }
        }
    }

    // TODO send ptt file
    private fun sendPttFile(pttFilePath: String) {
        // val msgElement = CreateElement.createPttElement(pttFilePath)
        // QQSendMsgTool.sendMsg(ContactUtils.getCurrentContact(), arrayListOf(msgElement))
    }

    private fun startForwardIntent(context: Context, filePath: String) {
        context.startActivity(
            Intent(context, ClassUtils.findClass("com.tencent.mobileqq.activity.ForwardRecentActivity")).apply {
                putExtra("selection_mode", 0)
                putExtra("direct_send_if_dataline_forward", false)
                putExtra("forward_text", "null")
                putExtra("ptt_forward_path", filePath)
                putExtra("forward_type", -1)
                putExtra("caller_name", "ChatActivity")
                putExtra("k_smartdevice", false)
                putExtra("k_dataline", false)
                putExtra("k_forward_title", "语音转发")
            }
        )
    }

    override val targetTypes = arrayOf("com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent")

    override fun onGetMenu(aioMsgItem: Any, targetType: String, param: XC_MethodHook.MethodHookParam) {
        val item = QQCustomMenu.createMenuItem(aioMsgItem, "转发", R.id.item_ptt_forward, R.drawable.ic_item_ptt_forward_24dp) {
            val msgRecord = aioMsgItem.callMethod<Any>("getMsgRecord")
            val elements = msgRecord.callMethod<ArrayList<*>>("getElements")
            elements.forEach { element ->
                val pttElement = element.callMethod<Any>("getPttElement")
                val pttFilePath = pttElement.callMethod<String>("getFilePath")
                startForwardIntent(ActivityTools.getTopActivity(), pttFilePath)
            }
        }
        param.result = listOf(item) + param.result as List<*>
    }
}
