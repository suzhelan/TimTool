package top.sacz.timtool.hook.item.chat

import android.view.View
import android.widget.ImageButton
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.MethodUtils


@HookItem("辅助功能/聊天/移除聊天界面语音图标")
class CancelVoiceIcon : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        val m2 =
            MethodUtils.create("com.tencent.tim.aio.inputbar.simpleui.TimAIOInputSimpleUIVBDelegate")
                .methodName("W")
                .params(Boolean::class.java)
                .returnType(Void.TYPE)
                .first()
        hookAfter(m2) { param ->
            val targetObj = param.thisObject
            val icon = FieldUtils.create(targetObj)
                .fieldName("g")
                .fieldType(ImageButton::class.java)
                .firstValue<ImageButton>(targetObj)
            icon.visibility = View.GONE
        }
        /*val method = MethodUtils.create("com.tencent.tim.aio.inputbar.simpleui.TimAIOInputSimpleUIVBDelegate\$c")
            .params(Context::class.java)
            .returnType(ImageButton::class.java)
            .last()
        hookAfter(method){param->
            val voiceIcon = param.result as ImageButton
            voiceIcon.visibility = View.GONE
            param.result = null
        }*/


    }

}