package candyenk.android.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.ObjIntConsumer;

/**
 * 多线程等待处理器
 */
public class THS<T> implements AutoCloseable {
    private final int size;//最大列表长度
    private ObjIntConsumer<T> cb;//数据变动监听
    private final Collection<T> list;//返回值列表
    private boolean ok;//弃用标记

    /**
     * 自行new一个集合
     * 创建THS
     * 别用不可变List
     *
     * @param size 最大列表长度,达到后自动关闭接收器
     * @param list 指定List
     */
    public THS(int size, Collection<T> list) {
        this.size = size;
        this.list = list;
        this.list.clear();
    }

    /**
     * 默认使用ArrayList
     */
    public THS(int size) {
        this(size, new ArrayList<>());
    }

    /**
     * 设置数据添加监听
     * int:添加当前对象后的数据总量
     * obj:当前添加的对象
     */
    public THS<T> setAddListener(ObjIntConsumer<T> cb) {
        this.cb = cb;
        return this;
    }

    /**
     * 添加数据到集合中
     * close后失效
     */
    public THS<T> add(T obj) {
        if (ok) return this;
        synchronized (this) {
            this.list.add(obj);
            if (cb != null) cb.accept(obj, list.size());
            if (list.size() >= size) close();
            return this;
        }

    }

    /**
     * 等待close
     * 卡线程
     * 返回一个不可变集合
     */
    public Collection<T> get() {
        while (!ok) TH.sleep(1);
        return Collections.unmodifiableCollection(list);
    }


    @Override
    public synchronized void close() {
        this.ok = true;
    }
}
