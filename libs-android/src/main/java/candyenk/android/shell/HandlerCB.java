package candyenk.android.shell;

import android.os.Handler;
import android.os.Looper;

/**
 * Handler CallBack
 * Handler回调将在主线程中调用,方便UI操作
 */
public class HandlerCB implements ShellCallBack {
    private final Handler handler;

    /**
     * 创建一个Handler
     */
    public static HandlerCB create(ShellCallBack cb) {
        if (cb instanceof HandlerCB) return (HandlerCB) cb;
        return new HandlerCB(cb);
    }

    private HandlerCB(ShellCallBack cb) {
        this.handler = new Handler(Looper.getMainLooper(), msg -> {
            cb.callBack(msg.what, (byte[]) msg.obj);
            return true;
        });
    }

    @Override
    public void callBack(int code, byte[] msg) {
        this.handler.handleMessage(handler.obtainMessage(code, msg));
    }
}
