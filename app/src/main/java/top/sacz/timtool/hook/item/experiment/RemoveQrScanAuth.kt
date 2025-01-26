package top.sacz.timtool.hook.item.experiment

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem

/**
 * 思路 https://github.com/cinit/QAuxiliary -> me.ketal.hook.RemoveQRLoginAuth
 */
@HookItem("辅助功能/实验性/移除相册扫码检验")
class RemoveQrScanAuth : BaseSwitchFunctionHookItem() {
    override fun loadHook(loader: ClassLoader) {
        val managerClass = loader.loadClass("com.tencent.open.agent.QrAgentLoginManager")
        hookBefore(managerClass.declaredMethods.single { method ->
            method.returnType == Void.TYPE && method.parameterTypes.isNotEmpty() && method.parameterTypes[0] == Boolean::class.java
        }) { param ->
            param.args[0] = false
        }
    }
}
