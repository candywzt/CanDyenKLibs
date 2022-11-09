package candyenk.android.utils;

import android.os.Handler;
import android.os.Looper;
import candyenk.java.io.IO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Android HTTP工具
 * 采用Handler
 * 可操作UI
 */
public class UHttp extends candyenk.java.utils.UHttp {
    /**
     * Get访问HTTP
     */
    public static void get(String url, CallBack cb) {
        Handler handler = new Handler(Looper.myLooper(), msg -> {
            cb.callback(msg.what, (InputStream) msg.obj);
            return true;
        });
        new Thread(() -> {
            HttpURLConnection uc = null;
            int response;
            ByteArrayInputStream in;
            try {
                uc = createUC(url);
                uc.setRequestMethod("GET");
                uc.connect();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IO.streamRW(uc.getInputStream(), baos);
                response = uc.getResponseCode();
                in = new ByteArrayInputStream(baos.toByteArray());
            } catch (Exception e) {
                response = -1;
                in = new ByteArrayInputStream((e.getClass() + "-" + e.getMessage()).getBytes());
            }
            handler.sendMessage(handler.obtainMessage(response, in));
            if (uc != null) uc.disconnect();
        }).start();
    }

    /**
     * Post访问HTTP
     */
    public static void post(String url, String parames, CallBack cb) {
        Handler handler = new Handler(Looper.myLooper(), msg -> {
            cb.callback(msg.what, (InputStream) msg.obj);
            return true;
        });
        handler.post(() -> {
            HttpURLConnection uc = null;
            int response;
            ByteArrayInputStream in;
            try {
                uc = createUC(url);
                uc.setRequestMethod("POST");
                IO.writeString(uc.getOutputStream(), parames);
                uc.connect();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IO.streamRW(uc.getInputStream(), baos);
                response = uc.getResponseCode();
                in = new ByteArrayInputStream(baos.toByteArray());
            } catch (Exception e) {
                response = -1;
                in = new ByteArrayInputStream((e.getClass() + "-" + e.getMessage()).getBytes());
            }
            handler.sendMessage(handler.obtainMessage(response, in));
            if (uc != null) uc.disconnect();
        });
    }
}
