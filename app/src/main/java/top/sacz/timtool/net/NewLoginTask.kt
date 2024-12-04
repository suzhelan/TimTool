package top.sacz.timtool.net

import com.alibaba.fastjson2.JSONObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import top.sacz.timtool.BuildConfig
import top.sacz.timtool.hook.TimVersion
import top.sacz.timtool.hook.qqapi.QQEnvTool
import top.sacz.timtool.hook.util.LogUtils
import top.sacz.timtool.net.entity.User
import top.sacz.timtool.util.IpUtil
import top.sacz.timtool.util.SystemUtil


class NewLoginTask {

    init {

    }

    /**
     * 检查当前用户是否和token一致
     */
    private fun checkUserUin(): Boolean {
        val uin = QQEnvTool.getCurrentUin()
        return uin == UserCenter.getUserInfo().uin
    }

    /**
     * 非阻塞获取用户信息并保存到本地
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun loginAndGetUserInfoAsync() {
        GlobalScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            LogUtils.addError("login", e)
        }) {
            //进行登录
            val userApi = HttpClient.getUserApi()
            //是否登录
            if (!userApi.isLogin().execute().body()!!.data) {
                val loginParam = JSONObject()
                loginParam["uin"] = QQEnvTool.getCurrentUin()
                //未登录,进行登录
                val loginInfo = userApi.doLogin(loginParam).execute().body()!!.data
                UserCenter.setUpdateToken(loginInfo)
            }
            //提交登录信息
            commitInfoAsync()
            //刷新用户信息
            val user = userApi.getUserInfo().execute().body()!!.data
            UserCenter.setUserInfo(user)
            if (!checkUserUin()) {
                UserCenter.removeAll()
                awaitLogin()
                return@launch
            }
        }
    }

    fun awaitLogin(): User = runBlocking {
        //进行登录
        val userApi = HttpClient.getUserApi()
        //是否登录
        if (!userApi.isLogin().execute().body()!!.data) {
            val loginParam = JSONObject()
            loginParam["uin"] = QQEnvTool.getCurrentUin()
            //未登录,进行登录
            val loginInfo = userApi.doLogin(loginParam).execute().body()!!.data
            UserCenter.setUpdateToken(loginInfo)
        }
        //刷新用户信息
        val user = userApi.refresh().execute().body()!!.data
        UserCenter.setUserInfo(user)

        if (!checkUserUin()) {
            UserCenter.removeAll()
            return@runBlocking awaitLogin()
        }
        user
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun commitInfoAsync() {
        GlobalScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, e ->
            LogUtils.addError("commitInfo", e)
        }) {
            val param = JSONObject()
            //qq
            param["nickname"] = QQEnvTool.getCurrentAccountNickName()
            param["hostApp"] = TimVersion.getAppName()
            param["version"] = TimVersion.getVersionName()
            param["moduleVersion"] = BuildConfig.VERSION_NAME
            //android
            param["systemModel"] = SystemUtil.systemModel
            param["systemVersion"] = SystemUtil.systemVersion
            param["deviceBrand"] = SystemUtil.deviceBrand
            param["sdk"] = android.os.Build.VERSION.SDK_INT
            param["city"] = IpUtil.getCity()
            //进行登录
            val userApi = HttpClient.getUserApi()
            userApi.commitLoginInfo(param).execute()
        }
    }


}