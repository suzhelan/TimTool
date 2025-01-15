package top.sacz.timtool.hook.item.chat

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.QQMessageViewListener
import top.sacz.timtool.hook.item.api.QQMsgViewAdapter
import top.sacz.timtool.util.ScreenParamUtils
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.ConstructorUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.util.KvHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HookItem("辅助功能/聊天/显示消息时间")
class ShowMessageTime : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "点击可以设置一些参数"
    }

    private var size: Int
        get() {
            return KvHelper("消息显示时间").getInt("size", 9)
        }
        set(value) {
            KvHelper("消息显示时间").put("size", value)
        }
    private var format: String
        get() {
            return KvHelper("消息显示时间").getString("format", "HH:mm:ss")
        }
        set(value) {
            KvHelper("消息显示时间").put("format", value)
        }

    override fun getOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            lateinit var editSize: EditText
            lateinit var editFormat: EditText
            MessageDialog.build().setCustomView(object :
                OnBindView<MessageDialog>(R.layout.layout_show_message_setting) {
                override fun onBind(
                    dialog: MessageDialog, v: View
                ) {
                    editSize = v.findViewById(R.id.edit_time_size)
                    editFormat = v.findViewById(R.id.edit_time_format)
                    editSize.setText("$size")
                    editFormat.setText(format)
                }
            }).setOkButton("保存") { _, _ ->
                size = editSize.text.toString().toInt()
                format = editFormat.text.toString()
                false
            }.show()

        }
    }

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

                    val msgTime: Long = FieldUtils.create(msgRecord).fieldName("msgTime")
                        .fieldType(Long::class.javaPrimitiveType).firstValue(msgRecord)

                    //time
                    val timeFormat = SimpleDateFormat(format, Locale.CHINA)
                    val timeStr = timeFormat.format(Date(msgTime * 1000))

                    //view
                    var textView = root.findViewById<TextView>(timeTextViewId)

                    //先从View池删除 防止重复添加和错误添加
                    if (textView != null) root.removeView(textView)

                    textView = TextView(context)
                    textView.textSize = size.toFloat()
                    textView.id = timeTextViewId
                    textView.text = timeStr

                    //制定约束布局参数 用反射做 不然androidx引用的是模块自身dex的而不是QQ自身的
                    val newLayoutParams: ViewGroup.LayoutParams = ConstructorUtils.newInstance(
                        ClassUtils.findClass("androidx.constraintlayout.widget.ConstraintLayout\$LayoutParams"),
                        arrayOf<Class<*>?>(
                            Int::class.javaPrimitiveType, Int::class.javaPrimitiveType
                        ),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ) as ViewGroup.LayoutParams

                    FieldUtils.create(newLayoutParams).fieldName("bottomToBottom")
                        .setFirst(newLayoutParams, contentViewId)
                    FieldUtils.create(newLayoutParams).fieldName("endToEnd")
                        .setFirst(newLayoutParams, contentViewId)
                    FieldUtils.create(newLayoutParams).fieldName("bottomMargin")
                        .setFirst(newLayoutParams, ScreenParamUtils.dpToPx(context, 3F))

                    root.addView(textView, newLayoutParams)
                }
            })
    }
}
