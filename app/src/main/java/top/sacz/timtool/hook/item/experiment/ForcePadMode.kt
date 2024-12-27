package top.sacz.timtool.hook.item.experiment

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.timtool.hook.util.getStaticFieldValue

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.experimental.ForcePadMode
 */
@HookItem("辅助功能/实验性/强制平板模式")
class ForcePadMode : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "重启后生效, 可能需要重新登录"
    }

    override fun loadHook(loader: ClassLoader) {
        val appSettingClass = loader.loadClass("com.tencent.common.config.AppSetting")
        hookBefore(appSettingClass.getDeclaredMethod("f")) { param ->
            val pad = appSettingClass.getStaticFieldValue<Int>("g")
            param.result = pad
        }
    }
}