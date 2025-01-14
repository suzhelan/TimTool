package top.sacz.timtool.net.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import top.sacz.timtool.net.entity.QSResult

interface PayApi {
    @GET("/pay/createAfdianTimToolOrder")
    fun createAfdianOrder(@Query("uin") uin: String): Call<QSResult<String>>

    @GET("/pay/queryOrderResult")
    fun queryOrderResult(@Query("uin") uin: String): Call<QSResult<String>>

}
