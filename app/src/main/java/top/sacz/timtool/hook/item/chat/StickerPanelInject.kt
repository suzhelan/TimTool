package top.sacz.timtool.hook.item.chat

import android.view.View
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.item.chat.stickerpanel.BottomStickerPanelDialog

@HookItem("辅助功能/聊天/表情面板")
class StickerPanelInject : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader?) {

    }

    override fun getOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            BottomStickerPanelDialog()
                .show()
        }
    }

}