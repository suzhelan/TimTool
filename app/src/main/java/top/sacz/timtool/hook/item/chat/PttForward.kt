package top.sacz.timtool.hook.item.chat

import android.content.Context
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.OnMenuBuilder
import top.sacz.timtool.hook.item.api.QQCustomMenu
import top.sacz.timtool.hook.util.callMethod
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.util.ActivityTools
import java.io.File

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.msg.PttForwardHook
 */
@HookItem("辅助功能/聊天/语音长按菜单添加转发")
class PttForward : BaseSwitchFunctionHookItem(), OnMenuBuilder {
    override fun loadHook(loader: ClassLoader) {}

    private fun startForwardIntent(context: Context, file: File) {
        context.startActivity(
            Intent(context, ClassUtils.findClass("com.tencent.mobileqq.activity.ForwardRecentActivity")).apply {
                putExtra("selection_mode", 0)
                putExtra("direct_send_if_dataline_forward", false)
                putExtra("forward_text", "null")
                putExtra("ptt_forward_path", file.path)
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
                startForwardIntent(ActivityTools.getTopActivity(), File(pttFilePath))
            }
        }
        param.result = listOf(item) + param.result as List<*>
    }
}
