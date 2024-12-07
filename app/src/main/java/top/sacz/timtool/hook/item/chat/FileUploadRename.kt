package top.sacz.timtool.hook.item.chat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import top.sacz.timtool.hook.HookEnv
import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.FieldUtils
import top.sacz.xphelper.reflect.Ignore
import top.sacz.xphelper.reflect.MethodUtils
import java.lang.reflect.Method


@HookItem("辅助功能/聊天/文件上传重命名")
class FileUploadRename : BaseSwitchFunctionHookItem() {

    override fun getTip(): String {
        return "自动重命名base为包名或应用名 自动将.apk重命名成.APK 防止被QQ自动重命名成.apk.1"
    }

    override fun isLoadedByDefault(): Boolean {
        return true
    }

    override fun loadHook(loader: ClassLoader) {
        //TODO 私聊还没做好 等待完善
        val friendMethod =
            MethodUtils.create("com.tencent.mobileqq.filemanager.nt.NTC2CFileTransferMgr")
                .returnType(Void.TYPE)
                .params(
                    loader.loadClass("com.tencent.mobileqq.filemanager.data.FileManagerEntity"),
                    Ignore::class.java
                )
                .first()

        //群组
        val troopMethod: Method =
            MethodUtils.create("com.tencent.mobileqq.troop.filemanager.TroopFileTransferMgr")
                .returnType(Void.TYPE)
                .params(
                    Long::class.javaPrimitiveType,
                    loader.loadClass("com.tencent.mobileqq.troop.utils.TroopFileTransferManager\$Item")
                )
                .first()

        //私聊
        hookBefore(friendMethod, { param ->
            val fileManagerEntity = param.args[0]
            val fileName: String =
                FieldUtils.getField(fileManagerEntity, "fileName", String::class.java)
            val localFile: String =
                FieldUtils.getField(fileManagerEntity, "strFilePath", String::class.java)
            if (meetHitConditions(fileName, localFile)) {
                FieldUtils.setField(
                    fileManagerEntity,
                    "fileName",
                    getFormattedFileNameByPath(localFile)
                )
            }
        }, 25)


        hookBefore(troopMethod, { param ->
            val item = param.args[1]
            val fileName: String = FieldUtils.getField(item, "FileName", String::class.java)
            val localFile: String = FieldUtils.getField(item, "LocalFile", String::class.java)
            if (meetHitConditions(fileName, localFile)) {
                FieldUtils.setField(item, "FileName", getFormattedFileNameByPath(localFile))
            }
        }, 25)


        //第二次挂钩 将.apk替换成.APK
        hookBefore(friendMethod, { param ->
            val fileManagerEntity = param.args[0]
            val fileName: String =
                FieldUtils.getField(fileManagerEntity, "fileName", String::class.java)
            if (fileName.endsWith(".apk")) {
                FieldUtils.setField(fileManagerEntity, "fileName", fileName.replace(".apk", ".APK"))
            }
        }, 20)

        hookBefore(troopMethod, { param ->
            val item = param.args[1]
            val fileName: String = FieldUtils.getField(item, "FileName", String::class.java)
            if (fileName.endsWith(".apk")) {
                FieldUtils.setField(item, "FileName", fileName.replace(".apk", ".APK"))
            }
        }, 20)
    }

    /**
     * 判断是否符合命中规则
     */
    private fun meetHitConditions(fileName: String, filePath: String): Boolean {
        if (fileName.matches("^base(\\([0-9]+\\))?.apk$".toRegex())) {
            return true
        }
        //后缀匹配命中
        val index = fileName.lastIndexOf(".")
        if (index == -1) return false
        val fileExtension = fileName.substring(index)
        //不区分大小写的匹配.apk
        if (fileExtension.equals(".apk", ignoreCase = true)) {
            //无后缀文件名 去除.apk 这四个字
            val fileNameWithoutSuffix = fileName.substring(0, index)
            val applicationInfo = getAppInfoByFilePath(filePath) ?: return false
            //包名命中
            if (fileNameWithoutSuffix == applicationInfo.packageName) return true
            //应用名命中
            if (fileNameWithoutSuffix == applicationInfo.name) return true
        }
        return false
    }

    private fun getAppInfoByFilePath(filePath: String?): ApplicationInfo? {
        try {
            val packageManager: PackageManager = HookEnv.getHostAppContext().packageManager
            val packageArchiveInfo = packageManager.getPackageArchiveInfo(
                filePath!!, 1
            )
            return packageArchiveInfo!!.applicationInfo
        } catch (e: Exception) {
            return null
        }
    }

    private fun getFormattedFileNameByPath(apkPath: String): String {
        try {
            val packageManager: PackageManager = HookEnv.getHostAppContext().packageManager
            val packageArchiveInfo = packageManager.getPackageArchiveInfo(apkPath, 1)
            val applicationInfo = packageArchiveInfo!!.applicationInfo
            applicationInfo!!.sourceDir = apkPath
            applicationInfo.publicSourceDir = apkPath
            val currentBaseApkFormat = "%n_%v.APK"
            return currentBaseApkFormat.replace(
                "%n",
                applicationInfo.loadLabel(packageManager).toString()
            ).replace("%p", applicationInfo.packageName).replace(
                "%v",
                packageArchiveInfo.versionName!!
            ).replace("%c", packageArchiveInfo.versionCode.toString())
        } catch (e: Exception) {
            return "base.APK"
        }
    }
}