package top.sacz.timtool.hook.item.api

import android.os.Bundle
import android.view.View
import top.sacz.timtool.hook.base.ApiHookItem
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.core.factory.ExceptionFactory
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils


@HookItem("监听QQMsgView更新")
class OnQQMessageView : ApiHookItem() {

    companion object {

        private val ON_AIO_CHAT_VIEW_UPDATE_LISTENER_MAP: HashMap<BaseSwitchFunctionHookItem, OnChatViewUpdateListener> =
            HashMap()

        /**
         * 添加消息监听器 责任链模式
         */
        @JvmStatic
        fun addMessageViewUpdateListener(
            hookItem: BaseSwitchFunctionHookItem,
            onMsgViewUpdateListener: OnChatViewUpdateListener
        ) {
            ON_AIO_CHAT_VIEW_UPDATE_LISTENER_MAP[hookItem] = onMsgViewUpdateListener
        }
    }

    override fun loadHook(loader: ClassLoader?) {
        val onMsgViewUpdate =
            MethodUtils.create("com.tencent.mobileqq.aio.msglist.holder.AIOBubbleMsgItemVB")
                .returnType(Void.TYPE)
                .params(Int::class.java, Ignore::class.java, List::class.java, Bundle::class.java)
                .first()
        hookAfter(onMsgViewUpdate) { param ->
            val thisObject = param.thisObject
            val msgView = FieldUtils.create(thisObject)
                .fieldType(View::class.java)
                .firstValue<View>(thisObject)

            val aioMsgItem = FieldUtils.create(thisObject)
                .fieldType(ClassUtils.findClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"))
                .firstValue<Any>(thisObject)

            onViewUpdate(aioMsgItem, msgView)
        }
    }

    private fun onViewUpdate(aioMsgItem: Any, msgView: View) {
        val msgRecord: Any = MethodUtils.create(aioMsgItem.javaClass)
            .methodName("getMsgRecord")
            .callFirst(aioMsgItem)

        for ((switchFunctionHookItem, listener) in ON_AIO_CHAT_VIEW_UPDATE_LISTENER_MAP.entries) {
            if (switchFunctionHookItem.isEnabled) {
                try {
                    listener.onViewUpdateAfter(msgView, msgRecord)
                } catch (e: Throwable) {
                    ExceptionFactory.add(switchFunctionHookItem, e)
                }
            }
        }
    }

    interface OnChatViewUpdateListener {
        fun onViewUpdateAfter(msgItemView: View, msgRecord: Any)
    }
}