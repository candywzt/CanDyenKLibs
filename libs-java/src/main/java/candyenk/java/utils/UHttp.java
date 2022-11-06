package candyenk.java.utils;

import candyenk.java.io.IO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * Java HTTP工具
 */
public class UHttp {
    /**
     * Get访问HTTP
     */
    public static void get(String url, CallBack cb) {
        new Thread(() -> {
            HttpURLConnection uc = null;
            try {
                uc = createUC(url);
                uc.setRequestMethod("GET");
                uc.connect();
                cb.callback(uc.getResponseCode(), uc.getInputStream());
            } catch (IOException e) {
                cb.callback(-1, new ByteArrayInputStream((e.getClass() + "-" + e.getMessage()).getBytes()));
            }
            if (uc != null) uc.disconnect();
        }).start();
    }

    /**
     * Post访问HTTP
     */
    public static void post(String url, String parames, CallBack cb) {
        new Thread(() -> {
            HttpURLConnection uc = null;
            try {
                uc = createUC(url);
                uc.setRequestMethod("POST");
                IO.writeString(uc.getOutputStream(), parames);
                uc.connect();
                cb.callback(uc.getResponseCode(), uc.getInputStream());
            } catch (IOException e) {
                cb.callback(-1, new ByteArrayInputStream((e.getClass() + "-" + e.getMessage()).getBytes()));
            }
            if (uc != null) uc.disconnect();
        }).start();
    }

    /**
     * PostJson访问HTTP
     */
    public static void post(String url, JsonObject json, CallBack callBack) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = json.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            sb.append(iterator).append("=").append(json.get(key));
            if (iterator.hasNext()) sb.append("&");
        }
        post(url, sb.toString(), callBack);
    }

    /*** 创建默认HTTPURLConnection ***/
    protected static HttpURLConnection createUC(String url) throws IOException {
        HttpURLConnection uc = (HttpURLConnection) new URL(url).openConnection();
        uc.setConnectTimeout(5000);
        uc.setDoInput(true);
        uc.setDoOutput(true);
        uc.setRequestProperty("accept", "*/*");
        uc.setRequestProperty("connection", "Keep-Alive");
        uc.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
        return uc;
    }

    /**
     * HTTP访问回调
     */
    public interface CallBack {
        void callback(int response, InputStream in);
    }

    public interface StringCallBack extends CallBack {
        @Override
        default void callback(int response, InputStream in) {
            callback(response, IO.readString(in));
        }

        void callback(int response, String content);
    }

    public interface JsonCallBack extends CallBack {
        @Override
        default void callback(int response, InputStream in) {
            callback(response, JsonParser.parseReader(new InputStreamReader(in)).getAsJsonObject());
        }

        void callback(int response, JsonObject json);
    }
}
