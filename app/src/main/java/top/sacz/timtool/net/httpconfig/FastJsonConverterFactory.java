package top.sacz.timtool.net.httpconfig;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 请求体为body/json/raw/xml会使用此转换器
 */
public class FastJsonConverterFactory extends Converter.Factory {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type,
                                                            @NotNull Annotation[] annotations,
                                                            @NotNull Retrofit retrofit) {
        //将响应转换为对象
        TypeReference<?> typeReference = TypeReference.get(type);
        return responseBody -> {
            String text = responseBody.string();
            return JSON.parseObject(text, typeReference);
        };
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type,
                                                          @NotNull Annotation[] parameterAnnotations,
                                                          @NotNull Annotation[] methodAnnotations,
                                                          @NotNull Retrofit retrofit) {
        //对象转json字符串
        return value -> {
            String jsonString = JSON.toJSONString(value);
            return RequestBody.create(jsonString, MEDIA_TYPE);
        };
    }
}
