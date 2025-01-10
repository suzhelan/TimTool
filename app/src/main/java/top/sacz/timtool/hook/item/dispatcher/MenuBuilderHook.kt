package top.sacz.timtool.hook.item.dispatcher

import de.robv.android.xposed.XC_MethodHook
import top.sacz.timtool.hook.base.BaseHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.chat.MessageRereading
import top.sacz.timtool.hook.item.chat.StickerPanelInject
import java.lang.reflect.Modifier

@HookItem("对应类型消息菜单构建时回调接口")
class MenuBuilderHook : BaseHookItem() {
    // 对应类型消息菜单构建时回调
    private val decorators: Array<OnMenuBuilder> = arrayOf(
        StickerPanelInject(),
        MessageRereading(),
    )

    override fun loadHook(classLoader: ClassLoader) {
        val baseClass = classLoader.loadClass("com.tencent.mobileqq.aio.msglist.holder.component.BaseContentComponent")
        val getMsgMethodName = baseClass.declaredMethods.first {
            it.returnType == classLoader.loadClass("com.tencent.mobileqq.aio.msg.AIOMsgItem") && it.parameterTypes.isEmpty()
        }.name
        val getListMethodName = baseClass.declaredMethods.first {
            Modifier.isAbstract(it.modifiers) && it.returnType == MutableList::class.java && it.parameterTypes.isEmpty()
        }.name
        for (target in decorators.flatMap { it.targetTypes.asIterable() }.toMutableSet()) {
            hookAfter(classLoader.loadClass(target).getDeclaredMethod(getListMethodName)) { param ->
                val getMsgMethod = baseClass.getDeclaredMethod(getMsgMethodName).apply { isAccessible = true }
                val aioMsgItem = getMsgMethod.invoke(param.thisObject)!!
                for (decorator in decorators) {
                    if (target in decorator.targetTypes) {
                        decorator.onGetMenu(aioMsgItem, target, param)
                    }
                }
            }
        }
    }
}

interface OnMenuBuilder {
    val targetTypes: Array<String>
        get() = arrayOf(
            "com.tencent.mobileqq.aio.aiogift.AIOTroopGiftComponent",//群礼物
            "com.tencent.mobileqq.aio.msglist.holder.component.anisticker.AIOAniStickerContentComponent",//动态表情
            "com.tencent.mobileqq.aio.msglist.holder.component.ark.AIOArkContentComponent",//卡片
            "com.tencent.mobileqq.aio.msglist.holder.component.chain.ChainAniStickerContentComponent",//连续动态表情(龙)
            "com.tencent.mobileqq.aio.msglist.holder.component.facebubble.AIOFaceBubbleContentComponent",//表情泡泡
            "com.tencent.mobileqq.aio.msglist.holder.component.file.AIOFileContentComponent",//文件
            "com.tencent.mobileqq.aio.msglist.holder.component.file.AIOOnlineFileContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.flashpic.AIOFlashPicContentComponent",//闪照
            "com.tencent.mobileqq.aio.msglist.holder.component.fold.AIOFoldContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.graptips.common.CommonGrayTipsComponent",//提示文本
            "com.tencent.mobileqq.aio.msglist.holder.component.graptips.revoke.RevokeGrayTipsComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.ickbreak.AIOIceBreakContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.LocationShare.AIOLocationShareComponent",//位置
            "com.tencent.mobileqq.aio.msglist.holder.component.longmsg.AIOLongMsgContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.markdown.AIOMarkdownContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.marketface.AIOMarketFaceComponent",//商店表情
            "com.tencent.mobileqq.aio.msglist.holder.component.mix.AIOMixContentComponent",//图文混排
            "com.tencent.mobileqq.aio.msglist.holder.component.multifoward.AIOMultifowardContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.multipci.AIOMultiPicContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.pic.AIOPicContentComponent",//图片
            "com.tencent.mobileqq.aio.msglist.holder.component.poke.AIOPokeContentComponent",//戳一戳
            "com.tencent.mobileqq.aio.msglist.holder.component.prologue.AIOPrologueContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.ptt.AIOPttContentComponent",//语音
            "com.tencent.mobileqq.aio.msglist.holder.component.reply.AIOReplyComponent",//回复
            "com.tencent.mobileqq.aio.msglist.holder.component.text.AIOTextContentComponent",//文本
            "com.tencent.mobileqq.aio.msglist.holder.component.text.AIOUnsuportContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.tofu.AIOTofuContentComponent",
            "com.tencent.mobileqq.aio.msglist.holder.component.video.AIOVideoContentComponent",//视频
            "com.tencent.mobileqq.aio.msglist.holder.component.videochat.AIOVideoResultContentComponent",//语音视频通话
            "com.tencent.mobileqq.aio.msglist.holder.component.zplan.AIOZPlanContentComponent",
            "com.tencent.mobileqq.aio.qwallet.AIOQWalletComponent",//红包转账
            "com.tencent.mobileqq.aio.shop.AIOShopArkContentComponent",
            "com.tencent.qqnt.aio.sample.BusinessSampleContentComponent",
        )

    fun onGetMenu(
        aioMsgItem: Any,
        targetType: String,
        param: XC_MethodHook.MethodHookParam
    )
}
