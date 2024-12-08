package top.sacz.timtool.hook.item.api

import top.sacz.timtool.hook.base.ApiHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.ClassUtils
import top.sacz.xphelper.reflect.ConstructorUtils
import top.sacz.xphelper.reflect.FieldUtils

@HookItem("监听联系人窗口更新")
class QQContactUpdateListener : ApiHookItem() {
    companion object {
        private var currentAIOContact: Any? = null

        @JvmStatic
        fun getCurrentAIOContact(): Any {
            return currentAIOContact!!
        }

    }

    override fun loadHook(loader: ClassLoader) {

        val aioContextImpl: Class<*> =
            ClassUtils.findClass("com.tencent.aio.runtime.AIOContextImpl")
        val method = ConstructorUtils.create(aioContextImpl)
            .paramTypes(

                ClassUtils.findClass("com.tencent.aio.main.fragment.ChatFragment"),
                    ClassUtils.findClass("com.tencent.aio.data.AIOParam"),
                    ClassUtils.findClass("androidx.lifecycle.LifecycleOwner"),
                    ClassUtils.findClass("kotlin.jvm.functions.Function0")

            ).first()
        hookBefore(method) { param ->
            val aioParam = param.args[1]
            val aioSession: Any = FieldUtils.create(aioParam)
                .fieldType(ClassUtils.findClass("com.tencent.aio.data.AIOSession"))
                .firstValue(aioParam)
            val aioContact: Any = FieldUtils.create(aioSession)
                .fieldType(ClassUtils.findClass("com.tencent.aio.data.AIOContact"))
                .firstValue(aioSession)
            currentAIOContact = aioContact
        }
    }

}