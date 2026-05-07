package candyenk.android.tools;

import android.os.Handler;
import android.os.Looper;

/**
 * Android 线程帮助类
 * 结束回调和异常回调优先级高于自定义回调
 * 设置结束回调后将不会在自定义回调中触发结束事件
 */
public final class RC {
    /** 线程默认标志 */
    public static final int SIGN_DEFAULT = Integer.MIN_VALUE;
    /** 线程返回标志，事件运行完毕输出返回值 */
    public static final int SIGN_RETURN = Integer.MIN_VALUE + 2;
    /** 线程异常标志，事件发生异常，提前终止 */
    public static final int SIGN_ERROR = Integer.MIN_VALUE + 3;
    
    private final Tool t;
    
    private RC(Run run) {
        this.t = new Tool(run);
    }
    
    /**
     * 创建RC，并设置事件
     *
     * @param run 事件
     * @return RC
     */
    public static RC run(Run run) {
        return new RC(run);
    }
    
    /**
     * 设置自定义回调
     *
     * @param cb 自定义回调
     * @return 调用链
     */
    public RC custom(CustomCB cb) {
        this.t.customCB = cb;
        return this;
    }
    
    /**
     * 设置结束回调
     *
     * @param cb  结束回调
     * @param <T> 返回值类型
     * @return 调用链
     */
    public <T> RC result(ResultCB<T> cb) {
        this.t.resultCB = cb;
        return this;
    }
    
    /**
     * 设置异常回调
     *
     * @param cb 异常回调
     * @return 调用链
     */
    public RC error(ErrorCB cb) {
        this.t.errorCB = cb;
        return this;
    }
    
    /**
     * 获取结束回调
     *
     * @param <T> 返回值类型
     * @return 结束回调
     */
    public <T> ResultCB<T> result() {
        return t.resultCB;
    }
    
    /**
     * 获取异常回调
     *
     * @return 异常回调
     */
    public ErrorCB error() {
        return t.errorCB;
    }
    
    /**
     * 设置自定义回调
     *
     * @return 自定义回调
     */
    public CustomCB custom() {
        return t.customCB;
    }
    
    /**
     * 设置Handler
     * 不设置无法发送消息
     *
     * @param handler 处理器
     */
    public void setHandler(Handler handler) {
        t.h = handler;
    }
    
    /**
     * 在主线程中执行当前事件
     * 这是事件启动方法
     */
    public void run() throws Throwable {
        t.run();
    }
    
    /**
     * 在子线程中执行当前事件
     * 这是事件启动方法
     */
    public void runThread() {
        t.runThread();
    }
    
    /**
     * 事件接口
     */
    public interface Run {
        /**
         * 事件内容
         *
         * @param t 用于调用回调的对象
         * @throws Exception 事件异常
         */
        Object run(RC.Tool t) throws Exception;
    }
    
    /**
     * 自定义回调接口
     * 用于事件中和主线程通信
     */
    public interface CustomCB {
        /**
         * 回调
         *
         * @param sign 标志
         * @param msg  消息内容
         */
        void call(int sign, Object msg);
    }
    
    /**
     * 结束回调接口
     *
     * @param <T> 返回值类型
     */
    public interface ResultCB<T> {
        /**
         * 回调
         *
         * @param o 返回值
         */
        void call(T o);
    }
    
    /**
     * 异常回调接口
     */
    public interface ErrorCB {
        /**
         * 回调
         *
         * @param e 异常
         */
        void call(Throwable e);
    }
    
    /** 事件中使用的工具类，用于发送事件 */
    public static class Tool {
        /** 事件 */
        private final Run run;
        /** 事件中自定义回调 */
        private CustomCB customCB;
        /** 事件结束回调 */
        private ResultCB resultCB;
        /** 事件异常回调 */
        private ErrorCB errorCB;
        private Handler h;
        
        private Tool(Run run) {
            this.run = run;
        }
        
        /**
         * 发送一条消息
         * 没设置Handler则无效
         *
         * @param sign 标记
         * @param msg  消息本体
         */
        public void send(int sign, Object msg) {
            if (h != null) h.sendMessage(h.obtainMessage(sign, msg));
        }
        
        private void runCall(int sign, Object msg) {
            if (customCB != null) customCB.call(sign, msg);
        }
        
        private <T> void returnCall(T msg) {
            if (resultCB != null) resultCB.call(msg);
            else runCall(SIGN_RETURN, msg);
        }
        
        private void throwCall(Object msg) {
            if (errorCB != null) errorCB.call((Throwable) msg);
        }
        
        private void run() throws Exception {
            if (run == null) return;
            run.run(this);
        }
        
        private void runThread() {
            if (run == null) return;
            h = new Handler(Looper.getMainLooper(), msg -> {
                switch (msg.what) {
                    case SIGN_RETURN -> returnCall(msg.obj);
                    case SIGN_ERROR -> throwCall(msg.obj);
                    default -> runCall(msg.what, msg.obj);
                }
                return true;
            });
            new Thread(() -> {
                try {
                    send(SIGN_RETURN, run.run(this));
                } catch (Throwable e) {
                    send(SIGN_ERROR, e);
                }
            }).start();
        }
    }
}
