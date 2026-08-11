package top.sacz.timtool.net

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import top.sacz.timtool.net.UserCenter.getTokenInfo
import top.sacz.timtool.net.api.UpdateApi
import top.sacz.timtool.net.api.UserApi
import top.sacz.timtool.net.entity.TokenInfo
import top.sacz.timtool.net.httpconfig.AesEncryptInterceptor
import top.sacz.timtool.net.httpconfig.LogInterceptor
import top.sacz.timtool.net.httpconfig.TokenHeader


/**
 * 技术栈 Retrofit + Okhttp
 */
object HttpClient {
    const val BASE_URL: String = "https://timtool.suzhelan.top"
    private val HTTP_JSON_ENCODER = Json {
        //忽略未知jsonKey
        ignoreUnknownKeys = true
        //是否将null的属性写入json 默认true
        explicitNulls = true
        //是否使用默认值 默认false
        encodeDefaults = false
        //是否格式化json
        prettyPrint = true
        //宽容解析模式 可以解析不规范的json格式
        isLenient = false
    }


    fun createKey(): String {
        //AES密钥直接写前台,因为我开源都开源了不在意数据安全,如果数据重要我建议严格传输和保存
        return "MPRT7ZWOB4GQPA6S7LXYXVQBLS0RCNDF"
    }

    fun buildClient(isUseEncrypt: Boolean): OkHttpClient {
        val encryptInterceptor = AesEncryptInterceptor(createKey())
        val clientBuilder = OkHttpClient().newBuilder() //日志拦截器
            .addInterceptor(LogInterceptor()) //token头
            .addInterceptor(TokenHeader())
        //是否使用加密
        if (isUseEncrypt) {
            clientBuilder.addInterceptor(encryptInterceptor)
        }
        return clientBuilder.build()
    }


    @JvmStatic
    val tokenInfo: TokenInfo?
        get() = getTokenInfo()

    @JvmStatic
    val userApi: UserApi
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildClient(true))
//            .addConverterFactory(FastJsonConverterFactory())
            .addConverterFactory(
                HTTP_JSON_ENCODER.asConverterFactory(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()
            .create(UserApi::class.java)

    @JvmStatic
    val updateApi: UpdateApi
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildClient(false))
//            .addConverterFactory(FastJsonConverterFactory())
            .addConverterFactory(
                HTTP_JSON_ENCODER.asConverterFactory(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()
            .create(UpdateApi::class.java)
}
