package top.sacz.timtool.hook.item.chat.retracting

import com.google.protobuf.ByteString
import de.robv.android.xposed.XC_MethodHook
import top.artmoe.inao.entries.InfoSyncPushOuterClass
import top.artmoe.inao.entries.MsgPushOuterClass
import top.artmoe.inao.entries.QQMessageOuterClass
import top.artmoe.inao.entries.QQMessageOuterClass.QQMessage.MessageBody.C2CRecallOperationInfo
import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.hook.item.chat.PreventRetractingMessage

/**
 * 防撤回核心解析
 * 如果top.artmoe.inao.entries包找不到 那么你需要先编译一次 上面的top.artmoe.inao.entries类会自动生成
 */
object PreventRetractingMessageCore {


    fun handleInfoSyncPush(buffer: ByteArray, param: XC_MethodHook.MethodHookParam) {
        val infoSyncPush = InfoSyncPushOuterClass.InfoSyncPush.parseFrom(buffer)
        val recallMsgSeqList = mutableListOf<Pair<String, Int>>()
        //新代码 构建新的InfoSyncPush
        val newInfoSyncPush = infoSyncPush.toBuilder().apply {
            syncRecallContent = syncRecallContent.toBuilder().apply {
                syncInfoBodyList.forEachIndexed { index, syncInfoBody ->
                    val newMsgList = syncInfoBody.msgList.filter { qqMessage ->
                        val msgType = qqMessage.messageContentInfo.msgType
                        val msgSubType = qqMessage.messageContentInfo.msgSubType
                        val isRecall =
                            (msgType == 732 && msgSubType == 17) || (msgType == 528 && msgSubType == 138)
                        //是私聊消息
                        if (msgType == 528 && msgSubType == 138) {
                            val opInfo = qqMessage.messageBody.operationInfo
                            val c2cRecall = C2CRecallOperationInfo
                                .parseFrom(opInfo)
                            val msgSeq = c2cRecall.info.msgSeq
                            val senderUid = qqMessage.messageHead.senderUid
                            recallMsgSeqList.add(senderUid to msgSeq)
                        } else if (msgType == 732 && msgSubType == 17) {
                            //群聊消息
                            val opInfo = qqMessage.messageBody.operationInfo
                            val groupRecall =
                                QQMessageOuterClass.QQMessage.MessageBody.GroupRecallOperationInfo
                                    .parseFrom(opInfo)
                            //groupUin
                            val groupPeerId = groupRecall.peerId.toString()
                            //msg seq
                            val recallMsgSeq = groupRecall.info.msgInfo.msgSeq
                            recallMsgSeqList.add(groupPeerId to recallMsgSeq)
                        }
                        !isRecall
                    }
                    setSyncInfoBody(
                        index,
                        syncInfoBody.toBuilder().clearMsg().addAllMsg(newMsgList).build()
                    )
                }
            }.build()
        }.build()
        param.args[1] = newInfoSyncPush.toByteArray()
        val retracting = HookItemFactory.getItem(PreventRetractingMessage::class.java)
        recallMsgSeqList.forEach { (peerId, msgSeq) ->
            retracting.writeAndRefresh(peerId, msgSeq)
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
