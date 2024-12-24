package top.sacz.timtool.hook.item.api

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodHook
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.hook.TimVersion
import top.sacz.timtool.hook.base.BaseHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.util.KvHelper
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("适配QQMsg内容ViewID")
class QQMsgViewAdapter : BaseHookItem() {


    companion object {
        private var contentViewId = 0

        @JvmStatic
        fun getContentView(msgItemView: View): View {
            return msgItemView.findViewById(contentViewId)
        }

        @JvmStatic
        fun getContentViewId(): Int {
            return contentViewId
        }

        @JvmStatic
        fun hasContentMessage(messageRootView: ViewGroup): Boolean {
            return messageRootView.childCount >= 5
        }
    }

    private var unhook: XC_MethodHook.Unhook? = null

    private fun findContentViewId(): Int {
        return KvHelper(javaClass.simpleName).getInt(
            "contentViewId${TimVersion.getVersionName()}:${BuildConfig.VERSION_NAME}",
            -1
        )
    }

    private fun putContentViewId(id: Int) {
        val kv = KvHelper(javaClass.simpleName)
        kv.clearAll()
        kv.put(
            "contentViewId${TimVersion.getVersionName()}:${BuildConfig.VERSION_NAME}",
            id
        )
    }

    override fun loadHook(loader: ClassLoader) {
        if (findContentViewId() > 0) {
            contentViewId = findContentViewId()
            return
        }
        val onMsgViewUpdate =
            MethodUtils.create("com.tencent.mobileqq.aio.msglist.holder.AIOBubbleMsgItemVB")
                .returnType(Void.TYPE)
                .params(Int::class.java, Ignore::class.java, List::class.java, Bundle::class.java)
                .first()
        unhook = hookAfter(onMsgViewUpdate) { param ->
            val thisObject = param.thisObject
            val msgView = FieldUtils.create(thisObject)
                .fieldType(View::class.java)
                .firstValue<View>(thisObject)

            val aioMsgItem = FieldUtils.create(thisObject)
                .fieldType(ClassUtils.findClass("com.tencent.mobileqq.aio.msg.AIOMsgItem"))
                .firstValue<Any>(thisObject)

            if (aioMsgItem == null || msgView == null) return@hookAfter

            val msgRecord: Any = MethodUtils.create(aioMsgItem.javaClass).methodName("getMsgRecord")
                .callFirst(aioMsgItem)

            val elements: ArrayList<Any> = FieldUtils.getField(
                msgRecord, "elements",
                ArrayList::class.java
            )

            for (msgElement in elements) {
                val type: Int =
                    FieldUtils.getField(msgElement, "elementType", Int::class.javaPrimitiveType)
                //文本和图片类型的view 不解析其他类型的 否则解析不出来
                if (type <= 2) {
                    findContentView(msgView as ViewGroup)
                    break
                }
            }
        }
    }

    private fun findContentView(itemView: ViewGroup) {
        for (i in 0..<itemView.childCount) {
            val child = itemView.getChildAt(i)
            if (child.javaClass.name == "com.tencent.qqnt.aio.holder.template.BubbleLayoutCompatPress") {
                contentViewId = child.id
                putContentViewId(child.id)
                //解开hook
                unhook?.unhook()
                ToastTool.show("[Tim小助手]已对MsgView进行适配")
                break
            }
        }
    }

}