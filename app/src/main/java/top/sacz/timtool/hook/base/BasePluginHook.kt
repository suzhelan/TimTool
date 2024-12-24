package top.sacz.timtool.hook.base

import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.util.toMethod
import top.sacz.xphelper.XpHelper.classLoader

abstract class BasePluginHook : BaseSwitchFunctionHookItem() {
    abstract val pluginId: String
    override fun initOnce(): Boolean {
        val getPluginClassLoaderMethod by lazy {
            "Lcom/tencent/mobileqq/pluginsdk/PluginStatic;->getOrCreateClassLoader(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/ClassLoader;"
                .toMethod()
        }
        //判断plugin proxy adapter是否为空
        if ("Lcom/tencent/mobileqq/pluginsdk/IPluginAdapterProxy;->getProxy()Lcom/tencent/mobileqq/pluginsdk/IPluginAdapterProxy;".toMethod()
                .invoke(null) == null
        ) {
            //如果为空 那么我们主动设置plugin proxy adapter
            val proxyAdapterClass: Class<*> =
                classLoader.loadClass("cooperation.plugin.c") // implements IPluginAdapter
            val proxyAdapter = proxyAdapterClass.newInstance()

            "Lcom/tencent/mobileqq/pluginsdk/IPluginAdapterProxy;->setProxy(Lcom/tencent/mobileqq/pluginsdk/IPluginAdapter;)V".toMethod()
                .invoke(
                    null,
                    proxyAdapter
                )
        }
        //获取plugin apk的class loader
        val pluginClassLoader = getPluginClassLoaderMethod.invoke(
            null,
            HookEnv.getApplication(),
            pluginId
        ) as ClassLoader
        loadHook(pluginClassLoader)
        return false
    }
}