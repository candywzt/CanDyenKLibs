package candyenk.android.tools;

/**
 * 双线程等待处理器
 */
public class TH<T> {
    private final T or;
    private boolean ok = true;//弃用标记
    private int code;
    private T obj;

    /**
     * 新建
     *
     * @param or code为0时返回的值(超时值)
     */
    public TH(T or) {
        this.or = or;
    }

    /**
     * 不报错的休眠当前线程
     */
    public static void sleep(long time) {
        try {Thread.sleep(time);} catch (InterruptedException ignored) {}
    }


    /**
     * 在子线程中修改返回值
     * 携带返回代码
     * 在reset之前重复调用无效
     * code==0时obj无效
     */
    public TH<T> set(int code, T obj) {
        if (ok) return this;
        synchronized (this) {
            this.code = code;
            this.obj = obj;
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
        return code == 0 ? or : this.obj;
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
