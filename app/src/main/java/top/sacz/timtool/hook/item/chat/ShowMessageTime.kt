package top.sacz.timtool.hook.item.chat

import android.content.Context
import android.widget.TextView
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.HookTestUtils
import top.sacz.timtool.hook.util.LogUtils
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/显示消息时间")
class ShowMessageTime : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader?) {
        val method =
            MethodUtils.create("com.tencent.qqnt.aio.sample.BusinessSampleContentComponent")
                .params(Context::class.java)
                .returnType(TextView::class.java)
                .first()
        hookAfter(method) {
            LogUtils.addRunLog("hook显示消息时间", LogUtils.getCallStack())
        }
        HookTestUtils.hookTextView("test")
        LogUtils.addRunLog("hook显示消息时间")
    }

}