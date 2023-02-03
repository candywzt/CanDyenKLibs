package candyenk.android.tools;

import candyenk.android.shell.Shell;

/**
 * 双线程等待处理器
 */
public class TH<T> {
    public static int CODE_OVER = -12345;//默认超时代码,可自行修改
    private final int ot;//超时时间ms(1~360000)6分钟
    private final T or;//超时返回值
    private boolean ok = true;//弃用标记
    private int code = CODE_OVER;
    private T obj;

    /**
     * 不报错的休眠当前线程
     */
    public static void sleep(long time) {
        try {Thread.sleep(time);} catch (InterruptedException ignored) {}
    }

    /**
     * 创建
     * 创建后默认不可用,请先调用reset
     *
     * @param overTime   超时时间,超时自动返回(1-360000)
     * @param overResult 超时返回值,超时自动返回该对象
     */
    public TH(int overTime, T overResult) {
        this.ot = Math.max(1, Math.min(overTime, Shell.OT_MAX));
        this.or = overResult;
    }

    /**
     * 在子线程中修改返回值
     * 携带返回代码
     * 在reset之前重复调用无效
     */
    public TH<T> set(int code, T obj) {
        if (ok) return this;
        synchronized (this) {
            this.obj = obj;
            this.code = code;
            this.ok = true;
            return this;
        }
    }


    /**
     * 在主线程中调用
     * 等待set调用后返回
     * 在reset之前重复调用直接返回上一次结果
     */
    public int code() {
        if (ok) return this.code;
        new Thread(() -> {
            try {Thread.sleep(ot);} catch (Throwable ignored) {} finally {set(CODE_OVER, or);}
        }).start();
        while (!ok) sleep(1);
        return this.code;
    }

    /**
     * 返回当前Object
     * 等待set调用后返回
     * 在reset之前重复调用直接返回上一次结果
     */
    public T get() {
        if (!ok) code();
        return this.obj;
    }

    /**
     * 重置状态
     * 以便循环利用
     */
    public synchronized TH<T> reset() {
        this.ok = false;
        return this;
    }

}
