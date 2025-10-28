package top.sacz.timtool.net.api

import com.alibaba.fastjson2.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import top.sacz.timtool.net.entity.QSResult
import top.sacz.timtool.net.entity.User
import top.sacz.timtool.net.httpconfig.TokenInfo

/**
 * 在宿主环境 不能使用suspend搭配协程
 */
interface UserApi {

    @POST("/user/doLogin")
    fun doLogin(@Body param: JSONObject): Call<QSResult<TokenInfo>>

    @POST("/user/info")
    fun getUserInfo(): Call<QSResult<User>>

    @POST("/user/refreshUserInfo")
    fun refresh(): Call<QSResult<User>>

    @POST("/user/commitLoginInfo")
    fun commitLoginInfo(@Body param: JSONObject): Call<QSResult<String>>

    @POST("/user/isLogin")
    fun isLogin(): Call<QSResult<Boolean>>

}