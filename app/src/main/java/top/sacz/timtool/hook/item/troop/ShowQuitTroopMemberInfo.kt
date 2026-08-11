package top.sacz.timtool.hook.item.troop

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.ClassUtils

@HookItem("辅助功能/群聊/显示已退群用户的消息")
class ShowQuitTroopMemberInfo : BaseSwitchFunctionHookItem() {

    override fun getTip(): String = "用于直接查看已退群用户的发言记录，在群成员信息页显示加入时间"

    override fun loadHook(classLoader: ClassLoader) {
        val troopInfoClass = ClassUtils.findClass("com.tencent.mobileqq.profilecard.component.troop.ElegantProfileTroopMemInfoComponent")
            ?: return

        val targetMethod = troopInfoClass.declaredMethods.find { it.name == "getTroopMemeJoinTime" }
            ?: return

        hookAfter(targetMethod) { param ->
            if (param.result == "") {
                param.result = "已退出该群"
            }
        }
    }
}
