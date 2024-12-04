package top.sacz.timtool.net.httpconfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import top.sacz.timtool.util.AESHelper;

public class AesEncryptInterceptor implements Interceptor {
    private final String password;

    public AesEncryptInterceptor(String password) {
        this.password = password;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        // 拿到请求参数
        Request request = chain.request();
        // 拿到请求 body
        RequestBody body = request.body();
        //对请求进行加密
        if (body != null) {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            //将body 转成字符串,然后进行加密
            String bodyString = buffer.readString(StandardCharsets.UTF_8);
            String encryptString = AESHelper.encrypt(bodyString, password);
            //重新构造请求
            RequestBody newBody = RequestBody.create(encryptString, body.contentType());
            request = request.newBuilder()
                    .post(newBody)
                    .build();
        }
        //对响应进行解密
        try (Response response = chain.proceed(request)) {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            String responseBodyString = buffer.readString(StandardCharsets.UTF_8);
            String decryptString = AESHelper.decrypt(responseBodyString, password);
            ResponseBody newResponseBody = ResponseBody.create(decryptString, responseBody.contentType());
            return response.newBuilder().body(newResponseBody).build();
        }
    }
}
