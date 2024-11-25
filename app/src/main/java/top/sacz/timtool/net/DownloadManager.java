package top.sacz.timtool.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {

    public static void download(String url, String path) {
        try {
            File downloadPath = new File(path);
            if (!downloadPath.getParentFile().exists()) {
                downloadPath.getParentFile().mkdirs();
            }
            if (!downloadPath.exists()) {
                downloadPath.createNewFile();
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("User-Agent", "Android/TimNT TimTool")
                    .url(url)
                    .get()
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            try (BufferedInputStream bufIn = new BufferedInputStream(response.body().byteStream());
                 BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(downloadPath))) {
                int len;
                byte[] buf = new byte[2048];//2k
                while ((len = bufIn.read(buf)) != -1) {
                    bufOut.write(buf, 0, len);
                }
                bufOut.flush();
            }
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
