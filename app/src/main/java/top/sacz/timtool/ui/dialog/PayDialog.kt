package top.sacz.timtool.ui.dialog

import android.content.Intent
import android.net.Uri
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import top.sacz.timtool.R
import top.sacz.timtool.hook.qqapi.QQEnvTool
import top.sacz.timtool.hook.util.ToastTool
import top.sacz.timtool.net.HttpClient
import top.sacz.timtool.net.NewLoginTask
import top.sacz.timtool.net.UserCenter
import top.sacz.timtool.net.entity.QSResult
import top.sacz.xphelper.util.ActivityTools

/**
 * 很高兴您参阅此项目的源码部分 也希望您对此项目做出贡献
 */
class PayDialog {

    private val payApi = HttpClient.getPayApi()

    private val uin = QQEnvTool.getCurrentUin()
    fun showFirstDialog() {
        if (UserCenter.getUserInfo().identity >= 1) {
            return
        }
        MessageDialog.build()
            .setTitle(R.string.preface)
            .setMessage(R.string.sponsor)
            .setOkButton("支持一下~") { _, _ ->
                showCreateOrder()
                false
            }.setOtherButton("下次一定") { _, _ ->
                false
            }.show()
    }

    private fun showCreateOrder() {
        WaitDialog.show("正在创建爱发电订单...")
        //开始创建订单
        payApi.createAfdianOrder(uin).enqueue(object : retrofit2.Callback<QSResult<String>> {
            override fun onResponse(p0: Call<QSResult<String>>, p1: Response<QSResult<String>>) {
                WaitDialog.dismiss()
                showPayDialog(p1.body()!!.data)
            }

            override fun onFailure(p0: Call<QSResult<String>>, p1: Throwable) {
                WaitDialog.dismiss()
                ToastTool.show("创建订单失败:$p1")
            }
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showPayDialog(payUrl: String) {
        MessageDialog.show("感谢您的支持", "正在跳转到浏览器爱发电平台")
            .setOkButton("赞助完成后点我~") { _, _ ->
                queryOrderResult()
                false
            }
        GlobalScope.launch {
            delay(2000)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.setData(Uri.parse(payUrl))
            ActivityTools.getTopActivity().startActivity(intent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun queryOrderResult() = GlobalScope.launch(Dispatchers.IO) {
        var ok = false
        WaitDialog.show("正在查询订单结果...")
        //循环10次
        for (i in 0..10) {
            if (ok) {
                break
            }
            payApi.queryOrderResult(uin).enqueue(object : retrofit2.Callback<QSResult<String>> {
                override fun onResponse(p0: Call<QSResult<String>>, p1: Response<QSResult<String>>) {
                    val result = p1.body()!!
                    if (result.isSuccess) {
                        ToastTool.show(result.msg)
                        doPaySuccess()
                        ok = true
                    }
                    if (i >= 10) {
                        ToastTool.show(result.msg)
                    }
                }

                override fun onFailure(p0: Call<QSResult<String>>, p1: Throwable) {
                }
            })
            delay(1000)
        }
        WaitDialog.dismiss()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun doPaySuccess() = GlobalScope.launch(Dispatchers.IO) {
        WaitDialog.show("赞助成功 正在为您刷新用户信息")
        NewLoginTask().awaitLogin()
        TipDialog.show("用户信息更新成功", WaitDialog.TYPE.SUCCESS)
    }
}
