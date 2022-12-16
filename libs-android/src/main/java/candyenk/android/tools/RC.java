package candyenk.android.tools;

import android.os.Handler;
import android.os.Looper;

import java.util.function.Consumer;

/**
 * Android 线程帮助类
 * 好吧其实就是Handler的简易封装
 * Run里面发生的异常会发送到runCall
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
    protected Handler h;

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * Run接口
     */
    public interface Run {
        Object run(RC t) throws Exception;
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

    public RC(Run run, RunCall runCall) {
        this.run = run;
        this.runCall = runCall;
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
     * 运行当前事件在子线程中
     */
    public void runThread() {
        setHandler(new Handler(Looper.getMainLooper(), msg -> {
            runCall(msg.what, msg.obj);
            return true;
        }));
        newThread();
    }

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/
    /**
     * 将Return结果独立出来的RC
     */
    public static class RCR extends RC {
        public final Consumer<Object> returnCall;

        public interface Run extends RC.Run {
            Object run(RCR t) throws Exception;
        }

        public RCR(Run run, RunCall runCall, Consumer<Object> returnCall) {
            super(run, runCall);
            this.returnCall = returnCall;
        }

        /**
         * Run结束回调
         */
        public void returnCall(Object msg) {
            if (returnCall != null) returnCall.accept(msg);
        }

        @Override
        public void runThread() {
            setHandler(new Handler(Looper.getMainLooper(), msg -> {
                if (msg.what == SIGN_RETURN) returnCall(msg.obj);
                else runCall(msg.what, msg.obj);
                return true;
            }));
            newThread();
        }
    }
}
