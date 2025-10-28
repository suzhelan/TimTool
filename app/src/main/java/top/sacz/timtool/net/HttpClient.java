package top.sacz.timtool.net;



import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import top.sacz.timtool.net.api.PayApi;
import top.sacz.timtool.net.api.UpdateApi;
import top.sacz.timtool.net.api.UserApi;
import top.sacz.timtool.net.httpconfig.AesEncryptInterceptor;
import top.sacz.timtool.net.httpconfig.FastJsonConverterFactory;
import top.sacz.timtool.net.httpconfig.LogInterceptor;
import top.sacz.timtool.net.httpconfig.TokenHeader;
import top.sacz.timtool.net.httpconfig.TokenInfo;


/**
 * 技术栈 Retrofit + Okhttp
 */
public class HttpClient {

    public static final String BASE_URL = "https://timtool.suzhelan.top";


    public static String createKey() {
        //AES密钥直接写前台,因为我开源都开源了不在意数据安全,如果数据重要我建议严格传输和保存
        return "MPRT7ZWOB4GQPA6S7LXYXVQBLS0RCNDF";
    }

    public static OkHttpClient buildClient(boolean isUseEncrypt) {

        AesEncryptInterceptor encryptInterceptor = new AesEncryptInterceptor(createKey());
        OkHttpClient.Builder clientBuilder = new OkHttpClient().newBuilder()
                //日志拦截器
                .addInterceptor(new LogInterceptor())
                //token头
                .addInterceptor(new TokenHeader());
        //是否使用加密
        if (isUseEncrypt) {
            clientBuilder.addInterceptor(encryptInterceptor);
        }
        return clientBuilder.build();
    }


    public static TokenInfo getTokenInfo() {
        return UserCenter.INSTANCE.getTokenInfo();
    }

    public static UserApi getUserApi() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(buildClient(true))
                .addConverterFactory(new FastJsonConverterFactory())
                .build()
                .create(UserApi.class);
    }

    public static PayApi getPayApi() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(buildClient(false))
                .addConverterFactory(new FastJsonConverterFactory())
                .build()
                .create(PayApi.class);
    }

    public static UpdateApi getUpdateApi() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(buildClient(false))
                .addConverterFactory(new FastJsonConverterFactory())
                .build()
                .create(UpdateApi.class);
    }
}
