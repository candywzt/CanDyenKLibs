package candyenk.android.tools;

import android.os.Handler;
import android.os.Looper;

import java.util.function.Consumer;

/**
 * Android 线程帮助类
 * 好吧其实就是Handler的简易封装
 * Run里面发生的异常会发送到throwCall
 * Run没有异常会将结果发送到returnCall
 */
public class RC {
    /*************************************静态变量**************************************************/
    //固定值,自定义请避开
    /**
     * 线程默认标志(int最小值)
     */
    public static final int SIGN_DEFAULT = Integer.MIN_VALUE;
    /**
     * 线程结束标志(int最小值+1)
     */
    public static final int SIGN_CLOSE = Integer.MIN_VALUE + 1;
    /**
     * 线程返回标志(int最小值+2)
     */
    public static final int SIGN_RETURN = Integer.MIN_VALUE + 2;
    /**
     * 线程异常标志(int最小值+3)
     */
    public static final int SIGN_ERROR = Integer.MIN_VALUE + 3;
    /*************************************成员变量**************************************************/
    public final Run run;
    public final RunCall runCall;
    public final Consumer<Object> returnCall;
    public final Consumer<Throwable> throwCall;
    protected Handler h;

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * Run接口
     */
    public interface Run<T extends RC> {
        Object run(T t) throws Exception;
    }

    /**
     * CallBack接口
     */
    public interface RunCall {
        void runCall(int sign, Object msg);
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    /**
     * run内所有事件全部发送到runCall
     */
    public RC(Run<? extends RC> run, RunCall runCall) {
        this(run, runCall, null);
    }

    /**
     * 忽略run内所有事件(除了returnCall)
     */
    public RC(Run<? extends RC> run, Consumer<Object> returnCall) {
        this(run, null, returnCall);
    }

    /**
     * run内return事件发送到returnCall
     * run内异常事件发送到runCall
     */
    public RC(Run<? extends RC> run, RunCall runCall, Consumer<Object> returnCall) {
        this(run, runCall, returnCall, null);
    }

    /**
     * 各司其职
     */
    public RC(Run<? extends RC> run, RunCall runCall, Consumer<Object> returnCall, Consumer<Throwable> throwCall) {
        this.run = run;
        this.runCall = runCall;
        this.returnCall = returnCall;
        this.throwCall = throwCall;
    }
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    protected void newThread() {
        new Thread(() -> {
            Object[] r = new Object[1];
            try {
                r[0] = run();
                send(SIGN_RETURN, r[0]);
            } catch (Throwable e) {
                send(SIGN_ERROR, e);
            }
        }).start();
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
    public Object run() throws Throwable {
        return run != null ? run.run(this) : null;
    }

    /**
     * Run内回调
     * sign为异常时msg为Exception,其余都是指定返回值
     */
    public void runCall(int sign, Object msg) {
        if (runCall != null) runCall.runCall(sign, msg);
    }

    /**
     * Return结束回调
     */
    public void returnCall(Object msg) {
        if (returnCall != null) returnCall.accept(msg);
        else runCall(SIGN_RETURN, msg);
    }

    /**
     * Throw回调
     */
    public void throwCall(Object msg) {
        if (throwCall != null) throwCall.accept((Throwable) msg);
    }

    /**
     * 运行当前事件在子线程中
     */
    public void runThread() {
        setHandler(new Handler(Looper.getMainLooper(), msg -> {
            if (msg.what == SIGN_RETURN) returnCall(msg.obj);
            else if (msg.what == SIGN_ERROR) throwCall(msg.obj);
            else runCall(msg.what, msg.obj);
            return true;
        }));
        newThread();
    }
}
