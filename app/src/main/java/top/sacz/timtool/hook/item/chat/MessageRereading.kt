package top.sacz.timtool.hook.item.chat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import top.sacz.timtool.R
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.core.factory.ExceptionFactory
import top.sacz.timtool.hook.item.api.QQCustomMenu
import top.sacz.timtool.hook.item.api.QQMessageViewListener
import top.sacz.timtool.hook.item.api.QQMsgViewAdapter
import top.sacz.timtool.hook.item.chat.rereading.MessageRereadingConfig
import top.sacz.timtool.hook.item.chat.rereading.RereadingMessageClickListener
import top.sacz.timtool.hook.item.dispatcher.OnMenuBuilder
import top.sacz.timtool.hook.qqapi.ContactUtils
import top.sacz.timtool.hook.qqapi.QQEnvTool
import top.sacz.timtool.hook.util.PathTool
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.hook.util.callMethod
import top.sacz.timtool.util.DrawableUtil
import top.sacz.timtool.util.ScreenParamUtils
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.ConstructorUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils
import java.io.File
import java.lang.reflect.Method

@SuppressLint("UseCompatLoadingForDrawables")
@HookItem("辅助功能/聊天/复读")
class MessageRereading : BaseSwitchFunctionHookItem(), OnMenuBuilder {

    override fun getTip(): String {
        return "点击可以设置一些参数"
    }

    override fun getOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            lateinit var editSize: EditText
            lateinit var cbDoubleClick: CheckBox
            lateinit var cbShowInMenu: CheckBox
            MessageDialog.build()
                .setCustomView(object :
                    OnBindView<MessageDialog>(R.layout.layout_rereading_setting) {
                    override fun onBind(
                        dialog: MessageDialog,
                        v: View
                    ) {
                        editSize = v.findViewById(R.id.edit_repeat_icon_size)
                        cbDoubleClick = v.findViewById(R.id.cb_double_click_repeat)
                        cbShowInMenu = v.findViewById(R.id.cb_show_in_menu)
                        editSize.setText("${MessageRereadingConfig.getSize()}")
                        cbDoubleClick.isChecked = MessageRereadingConfig.isDoubleClickMode()
                        cbShowInMenu.isChecked = MessageRereadingConfig.isShowInMenu()
                    }
                })
                .setOkButton("保存") { _, _ ->
                    MessageRereadingConfig.setSize(editSize.text.toString().toFloat())
                    MessageRereadingConfig.setDoubleClickMode(cbDoubleClick.isChecked)
                    MessageRereadingConfig.setShowInMenu(cbShowInMenu.isChecked)
                    false
                }
                .show()

        }
    }

    override fun onGetMenu(aioMsgItem: Any, targetType: String, param: XC_MethodHook.MethodHookParam) {
        if (!MessageRereadingConfig.isShowInMenu()) return
        val item = QQCustomMenu.createMenuItem(aioMsgItem, "复读", R.id.item_repeat, R.drawable.repeat) {
            val msgRecord = aioMsgItem.callMethod<Any>("getMsgRecord")
            val listener = RereadingMessageClickListener(msgRecord, ContactUtils.getCurrentContact())
            listener.rereading()
        }
        param.result = listOf(item) + param.result as List<*>
    }


    val icon: Drawable by lazy {
        val context = HookEnv.getHostAppContext()
        val path = PathTool.getModuleDataPath() + "/+1.png"
        if (!File(path).exists()) {
            val icon = ResourcesCompat.getDrawable(
                HookEnv.getHostAppContext().resources,
                R.drawable.repeat,
                null
            )
            DrawableUtil.drawableToFile(icon, path, Bitmap.CompressFormat.PNG)
            ToastTool.show("+1图标已生成")
        }
        DrawableUtil.readDrawableFromFile(context, path)
    }

    override fun isLoadedByDefault(): Boolean {
        return true
    }

    private val repetitionViewId = 0x2399332

    private fun hideQQRepetitionView() {
        //隐藏QQ原本的复读图标
        val followComponentMethod: Method =
            MethodUtils.create("com.tencent.mobileqq.aio.msglist.holder.component.msgfollow.AIOMsgFollowComponent")
                .returnType(Void.TYPE)
                .params(
                    Int::class.javaPrimitiveType,
                    Ignore::class.java,
                    MutableList::class.java
                )
                .first()

        XposedBridge.hookMethod(followComponentMethod, object : XC_MethodReplacement(25) {
            @Throws(Throwable::class)
            override fun replaceHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val originalRepeatIcon: ImageView =
                    MethodUtils.create(thisObject.javaClass)
                        .returnType(
                            ImageView::class.java
                        ).callFirst(thisObject)
                originalRepeatIcon.setImageDrawable(null)
                if (originalRepeatIcon.visibility != View.GONE) {
                    originalRepeatIcon.visibility = View.GONE
                }
            }
        })
    }

    override fun loadHook(loader: ClassLoader) {
        hideQQRepetitionView()

        QQMessageViewListener.addMessageViewUpdateListener(
            this,
            object : QQMessageViewListener.OnChatViewUpdateListener {
                @Throws(Throwable::class)
                override fun onViewUpdateAfter(msgItemView: View, msgRecord: Any) {
                    val senderUin: Long = FieldUtils.create(msgRecord)
                        .fieldName("senderUin")
                        .fieldType(Long::class.javaPrimitiveType)
                        .firstValue(msgRecord)

                    //约束布局
                    val root = msgItemView as ViewGroup
                    val context = root.context
                    //消息内容布局
                    val contentViewId: Int = QQMsgViewAdapter.getContentViewId()

                    //防止有撤回 进群等消息类型
                    if (!QQMsgViewAdapter.hasContentMessage(root)) return

                    //如果有 则删掉
                    var repetitionImageView = root.findViewById<ImageView>(repetitionViewId)
                    if (repetitionImageView != null) root.removeView(repetitionImageView)

                    repetitionImageView = ImageView(context)
                    repetitionImageView.id = repetitionViewId
                    repetitionImageView.setImageDrawable(icon)

                    try {
                        val contact = ContactUtils.getCurrentContact()
                        val rereadingMessageClickListener = RereadingMessageClickListener(
                            msgRecord,
                            contact
                        )
                        repetitionImageView.setOnClickListener(rereadingMessageClickListener)
                    } catch (e: Exception) {
                        ExceptionFactory.add(this@MessageRereading, e)
                    }
                    val size: Int =
                        ScreenParamUtils.dpToPx(context, MessageRereadingConfig.getSize())

                    //制定约束布局参数 用反射做 不然androidx引用的是模块的而不是QQ自身的
                    val newlayoutParams: ViewGroup.LayoutParams = ConstructorUtils.newInstance(
                        ClassUtils.findClass("androidx.constraintlayout.widget.ConstraintLayout\$LayoutParams"),
                        arrayOf<Class<*>?>(
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType
                        ),
                        size,
                        size
                    ) as ViewGroup.LayoutParams
                    FieldUtils.create(newlayoutParams)
                        .fieldName("topToTop")
                        .setFirst(newlayoutParams, contentViewId)
                    FieldUtils.create(newlayoutParams)
                        .fieldName("bottomToBottom")
                        .setFirst(newlayoutParams, contentViewId)

                    val sendUin = senderUin.toString()
                    if (sendUin == QQEnvTool.getCurrentUin()) {
                        FieldUtils.create(newlayoutParams)
                            .fieldName("endToStart")
                            .setFirst(newlayoutParams, contentViewId)
                    } else {
                        FieldUtils.create(newlayoutParams)
                            .fieldName("startToEnd")
                            .setFirst(newlayoutParams, contentViewId)
                    }
                    root.addView(repetitionImageView, newlayoutParams)
                }
            })
    }

}
