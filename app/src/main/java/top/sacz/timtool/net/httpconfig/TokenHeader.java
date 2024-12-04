package top.sacz.timtool.net.httpconfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import top.sacz.timtool.net.HttpClient;


public class TokenHeader implements Interceptor {

    @Override
    public @NotNull Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        TokenInfo tokenInfo = HttpClient.getTokenInfo();
        if (tokenInfo != null) {
            String tokenName = tokenInfo.tokenName;
            String tokenValue = tokenInfo.tokenValue;
            if (tokenName != null && tokenValue != null) {
                builder.header(tokenName, tokenValue);
            }
        }
        request = builder.build();
        return chain.proceed(request);
    }

}
