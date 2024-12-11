package top.sacz.timtool.ui.dialog

import com.kongzue.dialogx.dialogs.MessageDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import top.sacz.timtool.R
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.net.HttpClient
import top.sacz.timtool.net.entity.QSResult
import top.sacz.timtool.net.entity.UpdateInfo
import top.sacz.timtool.util.TimeUtils
import top.sacz.xphelper.util.ActivityTools

/**
 * 展示更新Dialog
 */
class UpdateLogDialog {

    fun show() {
        val dialog = MessageDialog.build()
            .setTitle(R.string.all_update_log)
            .setMessage(R.string.loading)
            .setOkButton(R.string.ok)
            .show()
        asyncGetUpdateLog(dialog)
    }

    fun updateDialog(dialog: MessageDialog, updateInfoList: List<UpdateInfo>) {
        val builder = StringBuilder()
        for (updateInfo in updateInfoList) {
            builder.append("v").append(updateInfo.versionName).append(" - ")
                .append(TimeUtils.formatTime(updateInfo.time)).append("\n")
            builder.append(updateInfo.updateLog).append("\n---------------\n")
        }

        ActivityTools.runOnUiThread {
            dialog.setMessage(builder.toString())
        }
    }

    fun asyncGetUpdateLog(dialog: MessageDialog) {
        val updateApi = HttpClient.getUpdateApi()
        updateApi.getUpdateLog(0).enqueue(object : Callback<QSResult<List<UpdateInfo>>> {
            override fun onResponse(
                call: Call<QSResult<List<UpdateInfo>>?>,
                response: Response<QSResult<List<UpdateInfo>>?>
            ) {
                val data = response.body()?.data
                if (data != null) updateDialog(dialog, data)
            }

            override fun onFailure(
                p0: Call<QSResult<List<UpdateInfo>>?>,
                p1: Throwable
            ) {
                ToastTool.show(p1.message!!)
            }
        })
    }
}