package top.sacz.timtool.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IpUtil {


    public static String getCity() {
        JSONObject ipInfo = getIpInfo();
        if (ipInfo == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        JSONArray data = ipInfo.getJSONArray("data");
        sb.append(data.getString(0))
                .append("-")
                .append(data.getString(1))
                .append("-")
                .append(data.getString(2))
                .append("-")
                .append(data.getString(4));
        return sb.toString();
    }

    private static JSONObject getIpInfo() {
        try {
            String url = "https://2024.ipchaxun.com/";
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("User-Agent", "QQ-Android")
                    .addHeader("Accept", "*/*")
                    .addHeader("Connection", "keep-alive")
                    .build();
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            response.close();
            return JSON.parseObject(json);
        } catch (Exception e) {
            return null;
        }
    }
}
