package candyenk.java.io;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串数组输出流
 * 性能应该不戳
 */
public class StringArrayOutputStream extends OutputStream {
    private final List<String> list;//保存的数据
    private byte[] cache;//缓存
    private int index;//缓存索引
    private boolean sign;//标记

    public StringArrayOutputStream() {
        list = new ArrayList<>();
        cache = new byte[4096];
    }

    @Override
    public void write(int b) {
        synchronized (this) {
            checkCache();//检查缓存容量
            if (b == 10 || sign) save();///n或/r标记,保存
            else if (b == 13) sign = true;///r,标记
            else cache[index++] = (byte) b;//缓存
        }
    }

    /**
     * 无用
     */
    public void flush() {}

    /**
     * 无用
     */
    public void close() {}

    @Override
    public String toString() {
        return Arrays.toString(toStringArray());
    }

    /**
     * 导出为不可变List
     */
    public List<String> toStringList() {
        return Arrays.asList(toStringArray());
    }

    /**
     * 导出为字符串数组
     */
    public String[] toStringArray() {
        if (index != 0) save();
        return list.toArray(new String[0]);
    }

    /*** 检查缓存大小 ***/
    private void checkCache() {
        if (index > cache.length - 100) cache = Arrays.copyOf(cache, index + 1124);
    }

    /*** 保存一行数据 ***/
    private void save() {
        synchronized (this) {
            list.add(new String(cache, 0, index));
            index = 0;
            sign = false;
        }
    }
}
