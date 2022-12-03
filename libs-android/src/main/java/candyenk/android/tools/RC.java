package candyenk.android.tools;

import android.os.Handler;
import android.os.Looper;

/**
 * Android 线程帮助类
 */
public final class RC {
    /*************************************静态变量**************************************************/
    public static final int SIGN_RETURN = Integer.MIN_VALUE;//线程返回值标志
    public static final int SIGN_CLOSE = Integer.MIN_VALUE + 1;//线程结束标志
    public static final int SIGN_DEFAULT = Integer.MIN_VALUE + 1;//线程默认标志
    /*************************************成员变量**************************************************/
    private final Run<RC> r1;
    private final RunCall<Object> r2;
    private final ReturnCall<Object> r3;
    private Handler h;

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * 通用Run接口
     */
    public interface Run<T> {
        Object run(T t) throws Exception;
    }

    /**
     * 通用CallBack接口
     */
    public interface RunCall<T> {
        void runCall(int sign, T msg);
    }

    /**
     * 通用Return接口
     */
    public interface ReturnCall<T> {
        void returnCall(T msg);
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/

    public RC(Run<RC> r1, RunCall<Object> r2, ReturnCall<Object> r3) {
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
    }

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/

    /**
     * 设置Handler
     * 不设置无法发送消息
     */
    public void setHandler(Handler handler) {
        this.h = handler;
    }

    /**
     * 发送一条消息
     * 没设置Handler则无效
     */
    public void send(int sign, Object msg) {
        if (h != null) h.sendMessage(h.obtainMessage(sign, msg));
    }

    /**
     * 运行Run实例
     */
    public Object run() throws Exception {
        return r1 != null ? r1.run(this) : null;
    }

    /**
     * Run内回调
     */
    public void runCall(int sign, Object msg) {
        if (r2 != null) r2.runCall(sign, msg);
    }

    /**
     * Run结束回调
     */
    public void returnCall(Object msg) {
        if (r3 != null) r3.returnCall(msg);
    }

    /**
     * 运行当前事件在子线程中
     */
    public void runThread() {
        setHandler(new Handler(Looper.getMainLooper(), msg -> {
            if (msg.what == SIGN_RETURN) returnCall(msg.obj);
            else runCall(msg.what, msg.obj);
            return true;
        }));
        new Thread(() -> {
            Object[] r = new Object[1];
            try {r[0] = run();} catch (Exception ignored) {}
            send(SIGN_RETURN, r[0]);
        }).start();
    }
}
