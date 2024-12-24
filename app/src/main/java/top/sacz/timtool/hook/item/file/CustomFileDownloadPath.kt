package top.sacz.timtool.hook.item.file

import android.os.Environment
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils
import java.io.File


@HookItem("辅助功能/文件/重定向文件下载位置")
class CustomFileDownloadPath : BaseSwitchFunctionHookItem() {

    private fun getDownloadPath(): String =
        "${Environment.getExternalStorageDirectory().absolutePath}/Download/Tim"

    private fun defaultCachePath(): String =
        "${Environment.getExternalStorageDirectory().absolutePath}/Android/data/${HookEnv.getCurrentHostAppPackageName()}/Tencent/TIMfile_recv/"

    override fun getTip(): String {
        return "目前只支持重定向到${getDownloadPath()}"
    }

    override fun loadHook(classLoader: ClassLoader) {
        val method = MethodUtils.create("com.tencent.mobileqq.vfs.VFSAssistantUtils")
            .methodName("getSDKPrivatePath")
            .returnType(String::class.java)
            .first()
        hookAfter(method) { param ->
            val result = param.result as String
            val file = File(result)
            if (file.exists() && file.isFile) return@hookAfter // 如果文件存在则不处理,防止已下载的文件出现异常
            if (result.startsWith(defaultCachePath())) {
                param.result = File(getDownloadPath(), file.name).absolutePath
            }
        }

    }

}