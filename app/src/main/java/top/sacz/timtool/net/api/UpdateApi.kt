package top.sacz.timtool.net.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
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

    @FormUrlEncoded
    @POST("/update/download")
    fun download(@Field("fileName") fileName: String): Call<String>

}