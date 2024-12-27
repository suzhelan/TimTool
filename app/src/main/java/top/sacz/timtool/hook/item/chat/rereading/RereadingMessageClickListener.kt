package top.sacz.timtool.hook.item.chat.rereading

import android.view.View
import android.view.View.OnClickListener
import top.sacz.timtool.hook.qqapi.QQSendMsgTool
import top.sacz.xphelper.reflect.FieldUtils

class RereadingMessageClickListener(private val msgRecord: Any, private val contact: Any) :
    OnClickListener {
    private var elements: ArrayList<Any> = FieldUtils.create(msgRecord)
        .fieldName("elements")
        .firstValue(msgRecord)

    override fun onClick(v: View?) {
        //启用双击
        if (MessageRereadingConfig.isDoubleClickMode() && MessageRereadingConfig.isFastClick()) {
            rereading()
        } else if (!MessageRereadingConfig.isFastClick()) {
            rereading()
        }
    }

    fun rereading() {
        if (forward()) return
        QQSendMsgTool.sendMsg(contact, elements)
    }
    private fun forward(): Boolean {
        try {
            var isForwardMsg = false
            for (element in this.elements) {
                val elementType: Int =
                    FieldUtils.create(element)
                        .fieldName("elementType")
                        .fieldType(Int::class.javaPrimitiveType)
                        .firstValue(element)
                if (elementType == 2 || elementType == 5 || elementType == 10) {
                    isForwardMsg = true
                    break
                }
            }
            if (isForwardMsg) {
                val msgId: Long =
                    FieldUtils.create(msgRecord).fieldName("msgId")
                        .fieldType(Long::class.javaPrimitiveType).firstValue(msgRecord)
                val msgIdList = ArrayList<Long>()
                msgIdList.add(msgId)
                val targetContactList = ArrayList<Any>()
                targetContactList.add(contact)
                QQSendMsgTool.forwardMsg(msgIdList, contact, targetContactList)
                return true
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        return false
    }
}