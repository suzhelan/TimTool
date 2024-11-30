package top.sacz.timtool.hook.item.chat

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.QQMessageViewListener
import top.sacz.timtool.hook.item.api.QQMsgViewAdapter
import top.sacz.timtool.util.ScreenParamUtils
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.ConstructorUtils
import top.sacz.xphelper.reflect.FieldUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HookItem("辅助功能/聊天/显示消息时间")
class ShowMessageTime : BaseSwitchFunctionHookItem() {
    private val timeTextViewId = 0x190f01e6
    override fun loadHook(loader: ClassLoader) {
        //责任链设计模式 让每一个有关msg view update 的类都能低耦合嵌入
        QQMessageViewListener.addMessageViewUpdateListener(
            this,
            object : QQMessageViewListener.OnChatViewUpdateListener {
                override fun onViewUpdateAfter(msgItemView: View, msgRecord: Any) {
                    //约束布局
                    val root = msgItemView as ViewGroup
                    val context = msgItemView.getContext()

                    //防止有撤回 进群等消息类型
                    if (!QQMsgViewAdapter.hasContentMessage(root)) return

                    val contentViewId: Int = QQMsgViewAdapter.getContentViewId()

                    val msgTime: Long = FieldUtils.create(msgRecord)
                        .fieldName("msgTime")
                        .fieldType(Long::class.javaPrimitiveType)
                        .firstValue(msgRecord)

                    //time
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
                    val timeStr = timeFormat.format(Date(msgTime * 1000))

                    //view
                    var textView = root.findViewById<TextView>(timeTextViewId)

                    //先从View池删除 防止重复添加和错误添加
                    if (textView != null) root.removeView(textView)

                    textView = TextView(context)
                    textView.textSize = 9f
                    textView.id = timeTextViewId
                    textView.text = timeStr

                    //制定约束布局参数 用反射做 不然androidx引用的是模块自身dex的而不是QQ自身的
                    val newLayoutParams: ViewGroup.LayoutParams = ConstructorUtils.newInstance(
                        ClassUtils.findClass("androidx.constraintlayout.widget.ConstraintLayout\$LayoutParams"),
                        arrayOf<Class<*>?>(
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        ),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ) as ViewGroup.LayoutParams

                    FieldUtils.create(newLayoutParams)
                        .fieldName("bottomToBottom")
                        .setFirst(newLayoutParams, contentViewId)
                    FieldUtils.create(newLayoutParams)
                        .fieldName("endToEnd")
                        .setFirst(newLayoutParams, contentViewId)
                    FieldUtils.create(newLayoutParams)
                        .fieldName("bottomMargin")
                        .setFirst(newLayoutParams, ScreenParamUtils.dpToPx(context, 3F))

                    root.addView(textView, newLayoutParams)
                }
            })
    }

}