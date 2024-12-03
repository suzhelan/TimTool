package top.sacz.timtool.hook.item.chat

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import com.alibaba.fastjson2.TypeReference
import top.sacz.timtool.R
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.api.QQMessageViewListener
import top.sacz.timtool.hook.item.api.QQMsgViewAdapter
import top.sacz.timtool.hook.item.chat.retracting.PreventRetractingMessageCore
import top.sacz.timtool.util.KvHelper
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.ConstructorUtils
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/消息防撤回")
class PreventRetractingMessage : BaseSwitchFunctionHookItem() {

    private val viewId = 0x298382
    private var retractMessageMap: MutableMap<String, MutableList<Int>> = HashMap()
    override fun loadHook(loader: ClassLoader) {
        readData()

        val onMSFPushMethod =
            MethodUtils.create("com.tencent.qqnt.kernel.nativeinterface.IQQNTWrapperSession\$CppProxy")
                .params(
                    String::class.java,
                    ByteArray::class.java,
                    ClassUtils.findClass("com.tencent.qqnt.kernel.nativeinterface.PushExtraInfo")
                )
                .methodName("onMsfPush")
                .first()

        hookBefore(onMSFPushMethod) { param ->
            val cmd = param.args[0] as String
            val protoBuf = param.args[1] as ByteArray
            if (cmd == "trpc.msg.register_proxy.RegisterProxy.InfoSyncPush") {
                PreventRetractingMessageCore.handleInfoSyncPush(protoBuf, param)
            } else if (cmd == "trpc.msg.olpush.OlPushService.MsgPush") {
                PreventRetractingMessageCore.handleMsgPush(protoBuf, param)
            }
        }
        hookAIOMsgUpdate()
    }

    private fun getConfigUtils(): KvHelper {
        return KvHelper("防撤回数据库")
    }

    private fun hookAIOMsgUpdate() {
        QQMessageViewListener.addMessageViewUpdateListener(
            this,
            object : QQMessageViewListener.OnChatViewUpdateListener {
                override fun onViewUpdateAfter(msgItemView: View, msgRecord: Any) {
                    //约束布局
                    val rootView = msgItemView as ViewGroup

                    //防止有撤回 进群等消息类型
                    if (!QQMsgViewAdapter.hasContentMessage(rootView)) return

                    val peerUid: String = FieldUtils.create(msgRecord)
                        .fieldName("peerUid")
                        .fieldType(String::class.java)
                        .firstValue(msgRecord)

                    val msgSeq: Long = FieldUtils.create(msgRecord)
                        .fieldName("msgSeq")
                        .fieldType(Long::class.javaPrimitiveType).firstValue(msgRecord)

                    //防止错误添加提示没有删除
                    val recallPromptTextView = rootView.findViewById<View>(viewId)
                    if (recallPromptTextView != null) rootView.removeView(recallPromptTextView)
                    //这个msg是秒级的 不是毫秒
                    var msgTime: Long = FieldUtils.create(msgRecord).fieldName("msgTime")
                        .fieldType(Long::class.javaPrimitiveType).firstValue(msgRecord)
                    //变成毫秒级
                    msgTime *= 1000
                    //计算时间差 发送时间低于1秒不判断
                    if ((System.currentTimeMillis() - msgTime) < 1000) {
                        return
                    }
                    //如果有那就是已经撤回的消息
                    if (isRetractMessage(peerUid, msgSeq.toInt())) {
                        addViewToQQMessageView(rootView)
                    }
                }

            })
    }

    private fun addViewToQQMessageView(rootView: ViewGroup) {
        val context = rootView.context
        val parentLayoutId = rootView.id
        val contentId: Int = QQMsgViewAdapter.getContentViewId()
        //制定约束布局参数 用反射做 不然androidx引用的是模块的而不是QQ自身的
        val newLayoutParams: LayoutParams = ConstructorUtils.newInstance(
            ClassUtils.findClass("androidx.constraintlayout.widget.ConstraintLayout\$LayoutParams"),
            arrayOf<Class<*>?>(
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ),
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ) as LayoutParams
        FieldUtils.create(newLayoutParams)
            .fieldName("startToStart")
            .setFirst(newLayoutParams, parentLayoutId)

        FieldUtils.create(newLayoutParams)
            .fieldName("endToEnd")
            .setFirst(newLayoutParams, parentLayoutId)

        FieldUtils.create(newLayoutParams)
            .fieldName("topToTop")
            .setFirst(newLayoutParams, contentId)

        val textView = TextView(context)
        textView.text = "该消息已撤回"
        textView.id = viewId
        textView.gravity = Gravity.CENTER
        textView.textSize = 20f
        textView.setTextColor(context.getColor(R.color.皇家蓝))
        textView.isClickable = false
        rootView.addView(textView, newLayoutParams)
    }

    /**
     * 是否撤回的消息
     */
    private fun isRetractMessage(peerUid: String?, msgSeq: Int): Boolean {
        val seqList = retractMessageMap[peerUid] ?: return false
        return seqList.contains(msgSeq)
    }

    /**
     * 写入本地撤回记录
     */
    fun writeAndRefresh(peerUid: String, msgSeq: Int) {
        var seqList: MutableList<Int>? = retractMessageMap[peerUid]
        if (seqList == null) {
            seqList = ArrayList()
        }
        //往该set添加seq
        seqList.add(msgSeq)
        //刷新map
        retractMessageMap[peerUid] = seqList
        getConfigUtils().put("retractMessageMap", retractMessageMap)
    }

    /**
     * 从本地读取撤回记录数据
     */
    private fun readData() {
        val type = object : TypeReference<MutableMap<String, MutableList<Int>>>() {}
        var localRetractMessageMap = getConfigUtils().getObject("retractMessageMap", type)
        if (localRetractMessageMap == null) {
            localRetractMessageMap = HashMap()
        }
        this.retractMessageMap = localRetractMessageMap
    }
}