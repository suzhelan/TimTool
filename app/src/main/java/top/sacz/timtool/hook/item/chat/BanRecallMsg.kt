package top.sacz.timtool.hook.item.chat

import com.google.protobuf.ByteString
import de.robv.android.xposed.XC_MethodHook
import top.artmoe.inao.entries.InfoSyncPushOuterClass
import top.artmoe.inao.entries.MsgPushOuterClass
import top.artmoe.inao.entries.QQMessageOuterClass
import top.artmoe.inao.entries.QQMessageOuterClass.QQMessage.MessageBody.C2CRecallOperationInfo
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.hook.util.LogUtils

/**
 * 防撤回核心解析
 */
object BanRecallMsg {


    fun handleInfoSyncPush(buffer: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val infoSyncPush = InfoSyncPushOuterClass.InfoSyncPush.parseFrom(buffer)
        infoSyncPush.syncRecallContent.syncInfoBodyList.forEach { syncInfoBody ->
            syncInfoBody.msgList.forEach { qqMessage ->
                val msgType = qqMessage.messageContentInfo.msgType
                val msgSubType = qqMessage.messageContentInfo.msgSubType
                if ((msgType == 732 && msgSubType == 17) || (msgType == 528 && msgSubType == 138)) {
                    val newInfoSyncPush = infoSyncPush.toBuilder().apply {
                        syncRecallContent = syncRecallContent.toBuilder().apply {
                            for (i in 0 until syncInfoBodyCount) {
                                setSyncInfoBody(
                                    i, getSyncInfoBody(i).toBuilder().clearMsg().build()
                                )
                            }
                        }.build()
                    }.build()
                    param.args[1] = newInfoSyncPush.toByteArray()
                }
            }
        }
    }

    fun handleMsgPush(buffer: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val msgPush = MsgPushOuterClass.MsgPush.parseFrom(buffer)
        val msg = msgPush.qqMessage
        val msgTargetUid = msg.messageHead.receiverUid   // 接收人的uid
//        if (msgTargetUid != EnvHelper.getQQAppRuntime().currentUid) return  // 不是当前用户接受就返回
        val msgType = msg.messageContentInfo.msgType
        val msgSubType = msg.messageContentInfo.msgSubType

        val operationInfoByteArray = msg.messageBody.operationInfo.toByteArray()

        when (msgType) {
            732 -> when (msgSubType) {
                17 -> onGroupRecallByMsgPush(operationInfoByteArray, msgPush, param)
            }

            528 -> when (msgSubType) {
                138 -> onC2CRecallByMsgPush(operationInfoByteArray, msgPush, param)
            }
        }
    }


    private fun onC2CRecallByMsgPush(
        operationInfoByteArray: ByteArray,
        msgPush: MsgPushOuterClass.MsgPush,
        param: XC_MethodHook.MethodHookParam
    ) {
        LogUtils.addRunLog("onC2CRecallByMsgPush", HookEnv.getInstance().processName)
        val operationInfo = C2CRecallOperationInfo.parseFrom(operationInfoByteArray)
        //msg seq
        val recallMsgSeq = operationInfo.info.msgSeq
        //peerUid
        val operatorUid = operationInfo.info.operatorUid

        //本地消息key 用这个判断是不是已经撤回的消息

        val retracting = HookItemFactory.getItem(PreventRetractingMessage::class.java)
        retracting.writeAndRefresh(operatorUid, recallMsgSeq)


        val newOperationInfoByteArray = operationInfo.toBuilder().apply {
            info = info.toBuilder().apply {
                msgSeq = 1
            }.build()
        }.build().toByteArray()

        val newMsgPush = msgPush.toBuilder().apply {
            qqMessage = qqMessage.toBuilder().apply {
                messageBody = messageBody.toBuilder().apply {
                    setOperationInfo(
                        ByteString.copyFrom(newOperationInfoByteArray)
                    )
                }.build()
            }.build()
        }.build()
        param.args[1] = newMsgPush.toByteArray()
    }

    private fun onGroupRecallByMsgPush(
        operationInfoByteArray: ByteArray,
        msgPush: MsgPushOuterClass.MsgPush,
        param: XC_MethodHook.MethodHookParam
    ) {
        val firstPart = operationInfoByteArray.copyOfRange(0, 7)
        val secondPart = operationInfoByteArray.copyOfRange(7, operationInfoByteArray.size)

        val operationInfo =
            QQMessageOuterClass.QQMessage.MessageBody.GroupRecallOperationInfo.parseFrom(secondPart)
        //msg seq
        val recallMsgSeq = operationInfo.info.msgInfo.msgSeq
        //group uin
        val groupPeerId = operationInfo.peerId.toString()

        val newOperationInfoByteArray = firstPart + (operationInfo.toBuilder().apply {
            msgSeq = 1
            info = info.toBuilder().apply {
                msgInfo = msgInfo.toBuilder().setMsgSeq(1).build()
            }.build()
        }.build().toByteArray())

        val newMsgPush = msgPush.toBuilder().apply {
            qqMessage = qqMessage.toBuilder().apply {
                messageBody = messageBody.toBuilder().apply {
                    setOperationInfo(
                        ByteString.copyFrom(newOperationInfoByteArray)
                    )
                }.build()
            }.build()
        }.build()
        param.args[1] = newMsgPush.toByteArray()


        val retracting = HookItemFactory.getItem(PreventRetractingMessage::class.java)
        retracting.writeAndRefresh(groupPeerId, recallMsgSeq)

    }

}