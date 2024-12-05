package top.sacz.timtool.net.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import top.sacz.timtool.net.entity.HasUpdate
import top.sacz.timtool.net.entity.QSResult
import top.sacz.timtool.net.entity.UpdateInfo


interface UpdateApi {

    @FormUrlEncoded
    @POST("/update/getUpdateLog")
    fun getUpdateLog(@Field("version") versionCode: Int): Call<QSResult<List<UpdateInfo>>>

    @FormUrlEncoded
    @POST("/update/hasUpdate")
    fun hasUpdate(@Field("version") versionCode: Int): Call<QSResult<HasUpdate>>

    @GET("/update/download")
    fun download(@Query("version") version: Int): Call<String>

}