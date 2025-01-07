package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.toMethod

/**
 * 思路 https://github.com/cinit/QAuxiliary -> xyz.nextalone.hook.AutoSendOriginalPhoto
 */
@HookItem("辅助功能/图片与表情/自动勾选发送原图")
class AutoCheckSendOriginalPhoto : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        //半屏相册
        val photoPanelVB = loader.loadClass("com.tencent.mobileqq.aio.panel.photo.PhotoPanelVB")
        val bindViewAndDataMethod = photoPanelVB.getDeclaredMethod("Q0")
        val setCheckedMethod = photoPanelVB.getDeclaredMethod("s", Boolean::class.java)
        hookAfter(bindViewAndDataMethod) { param ->
            setCheckedMethod.invoke(param.thisObject, true)
        }
        //全屏相册
        hookAfter("Lcom/tencent/qqnt/qbasealbum/model/Config;->z()Z".toMethod()) { param ->
            param.result = true
        }
    }
}