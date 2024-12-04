package top.sacz.timtool.net.httpconfig;

import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

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
import top.sacz.timtool.BuildConfig;

public class LogInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody body = request.body();
        String bodyString = "";
        String responseBodyString;
        if (body != null) {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            bodyString = buffer.readString(StandardCharsets.UTF_8);
        }
        Response response = chain.proceed(request);
        {
            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            responseBodyString = buffer.clone().readString(StandardCharsets.UTF_8);
        }
        printLog(request, response, bodyString, responseBodyString);
        return response;
    }

    private void printLog(Request request, Response response, String reqBody, String respBody) {
        //判断是否是json格式
        String log = String.format("""
                请求成功：%s
                RequestBody:%s
                ResponseBody:%s
                """, request.url(), reqBody, respBody);
        try {
            log = String.format("""
                    请求成功：%s
                    RequestBody:%s
                    ResponseBody:%s
                    """, request.url(), JSON.toJSONString(JSON.parseObject(reqBody), JSONWriter.Feature.PrettyFormat), JSON.toJSONString(JSON.parseObject(respBody), JSONWriter.Feature.PrettyFormat));
        } catch (Exception e) {
            //不是json格式
        }
        if (BuildConfig.DEBUG) {
            Log.d("[Tim小助手]请求日志", log);
        }
    }
}
