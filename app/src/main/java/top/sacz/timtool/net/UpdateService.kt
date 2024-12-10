package top.sacz.timtool.net

import android.content.Intent
import android.net.Uri
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.R
import top.sacz.timtool.net.entity.HasUpdate
import top.sacz.timtool.net.entity.UpdateInfo
import top.sacz.timtool.util.TimeUtils
import top.sacz.xphelper.util.ActivityTools

class UpdateService {
    companion object {
        /**
         * 更新信息列表
         */
        private var updateInfoList: List<UpdateInfo> = listOf()

        /**
         * 是否有更新
         */
        //默认自带空值,防止空指针
        private var hasUpdate: HasUpdate = HasUpdate(
            hasUpdate = false,
            isForceUpdate = false,
            version = 0
        )

    }

    /**
     * 异步请求更新并Toast更新提示
     */
    fun requestUpdateAsyncAndToast() {
        requestUpdateAsync { hasUpdate ->
            if (hasUpdate) {
                PopTip.show(R.string.update_toast)
                    .showLong()
                    .setButton(R.string.to_update) { _, _ ->
                        this.showUpdateDialog()
                        false
                    }
            }
        }
    }

    /**
     * 异步请求更新并获取结果 存储在伴生对象
     *
     * @param callback 回调 参数一为是否有更新
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun requestUpdateAsync(callback: (Boolean) -> Unit = {}) {
        val version = BuildConfig.VERSION_CODE
        //运行在IO线程
        GlobalScope.launch(Dispatchers.IO) {

            val api = HttpClient.getUpdateApi()
            api.hasUpdate(version)
                .execute().body()?.let { qsResult ->
                    hasUpdate = qsResult.data
                    if (hasUpdate.hasUpdate) {
                        api.getUpdateLog(version)
                            .execute().body()?.let { qsResult ->
                                updateInfoList = qsResult.data
                            }
                    }
                    callback(hasUpdate.hasUpdate)
                }

        }
    }

    fun showUpdateDialog() {
        val activity = ActivityTools.getTopActivity()

        val dialog = MessageDialog.show(
            activity.getString(R.string.update_title) + getLastVersionName(),
            getUpdateLog()
        ).setOkButton(R.string.to_update) { _, _ ->
            toUpdate(getDownloadUrl())
            true
        }.setCancelButton(R.string.next) { _, _ ->
            false
        }

        //有强制更新
        if (isForceUpdate()) {
            //设置弹窗不可取消
            dialog.setCancelable(false)
            dialog.setCancelButton(R.string.is_force_update) { _, _ ->
                toUpdate(getDownloadUrl())
                true
            }
        }
    }

    private fun toUpdate(url: String) {
        val activity = ActivityTools.getTopActivity()
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        activity.startActivity(intent)
    }

    fun getLastVersionName(): String = updateInfoList.first().versionName

    /**
     * 获取差距的更新日志
     */
    fun getUpdateLog(): String {
        try {
            val builder = StringBuilder()
            for (updateInfo in updateInfoList) {
                builder.append("v").append(updateInfo.versionName).append(" - ")
                    .append(TimeUtils.formatTime(updateInfo.time)).append("\n")
                builder.append(updateInfo.updateLog).append("\n\n")
            }
            return builder.toString()
        } catch (e: Exception) {
            return "请前往TG频道查看更新日志"
        }
    }

    /**
     * 是否强制更新
     */
    fun isForceUpdate(): Boolean = hasUpdate.isForceUpdate

    /**
     * 是否有更新
     */
    fun hasUpdate(): Boolean = hasUpdate.hasUpdate


    /**
     * 获取更新链接
     */
    fun getDownloadUrl(): String {
        val api = HttpClient.getUpdateApi()
        val call = api.download(updateInfoList.first().versionCode)
        return call.request().url.toString()
    }


}