package top.sacz.timtool.hook.core

import top.sacz.timtool.hook.core.factory.HookItemFactory
import top.sacz.timtool.hook.item.api.MenuBuilderApi
import top.sacz.timtool.hook.item.api.OnMenuBuilder

/**
 * api处理器 在loadHook前会判断类所实现的接口 分配类给对应的api处理类处理
 */
object ApiProcessor {

    fun processor() {
        val allHookItems = HookItemFactory.getAllItemList()
        for (hookItem in allHookItems) {
            //如果是OnMenuBuilder接口的实现类
            if (hookItem is OnMenuBuilder) {
                MenuBuilderApi.register(hookItem)
            }
        }
    }

}
