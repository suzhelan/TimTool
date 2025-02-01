package top.sacz.timtool.hook.item.chat

import de.robv.android.xposed.XC_MethodHook
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.OnMenuBuilder
import top.sacz.timtool.hook.item.api.QQCustomMenu
import top.sacz.timtool.hook.item.chat.rereading.RereadingMessageClickListener
import top.sacz.timtool.hook.qqapi.ContactUtils
import top.sacz.timtool.hook.util.callMethod

@HookItem("辅助功能/聊天/消息长按菜单添加复读")
class MessageMenuAddRereading : BaseSwitchFunctionHookItem(), OnMenuBuilder {
    override fun loadHook(classLoader: ClassLoader) {
        //什么都不用写
    }

    override fun onGetMenu(aioMsgItem: Any, targetType: String, param: XC_MethodHook.MethodHookParam) {
        val item = QQCustomMenu.createMenuItem(aioMsgItem, "复读", R.id.item_msg_repeat, R.drawable.repeat) {
            val msgRecord = aioMsgItem.callMethod<Any>("getMsgRecord")
            val listener = RereadingMessageClickListener(msgRecord, ContactUtils.getCurrentContact())
            listener.rereading()
        }
        param.result = listOf(item) + param.result as List<*>
    }

}
