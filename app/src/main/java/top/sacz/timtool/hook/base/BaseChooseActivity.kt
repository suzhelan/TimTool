package top.sacz.timtool.hook.base

import android.content.ClipData
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.sacz.xphelper.activity.BaseActivity
import java.io.File

abstract class AbstractChooseActivity : BaseActivity() {

    // androidx: Can only use lower 16 bits for requestCode
    val REQUEST_CODE = this.hashCode() and 0x0000ffff

    lateinit var sendCacheDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendCacheDir = File(externalCacheDir, "SendCache")
    }

    final override fun onStart() {
        super.onStart()
        setVisible(true)
    }

    var bundle: Bundle? = null

    fun initSendCacheDir() {
        if (!sendCacheDir.exists()) {
            sendCacheDir.mkdirs()
        } else {
            sendCacheDir.listFiles()?.let {
                for (file in it) {
                    file.delete()
                }
            }
        }
    }

    suspend fun convertUriToPath(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            contentResolver.openInputStream(uri)?.use { input ->
                val displayName: String? =
                    contentResolver.query(uri, null, null, null, null, null)?.use {
                        if (it.moveToFirst()) {
                            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index != -1) {
                                it.getString(index)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                val file =
                    File(sendCacheDir, displayName ?: "默认文件名${System.currentTimeMillis()}")
                file.createNewFile()
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
                return@withContext file.absolutePath
            }
        }
    }

    fun convertClipDataToPath(clipData: ClipData): Array<String> {
        val result = Array(clipData.itemCount) { "" }
        for (i in 0 until clipData.itemCount) {
            val uri = clipData.getItemAt(i).uri
            contentResolver.openInputStream(uri)?.use { input ->
                val displayName: String? =
                    contentResolver.query(uri, null, null, null, null, null)?.use {
                        if (it.moveToFirst()) {
                            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (index != -1) {
                                it.getString(index)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                val file =
                    File(sendCacheDir, displayName ?: "默认文件名${System.currentTimeMillis()}")
                file.createNewFile()
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
                result[i] = file.absolutePath
            }
        }
        return result
    }

}