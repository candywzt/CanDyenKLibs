package candyenk.java.utils;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

/**
 * Java数组工具
 * 自定义数组ToString
 * 基本数据类型数组ToString
 * 自定义数组转化
 * 自定义数组转化为基本数据类型数组
 * 数组转自定义List
 * 数组转自定义Set
 * 数组ForEach
 * int数组转byte数组
 * byte数组转int数组
 * int数组转long数组
 * Collection添加自定义数组
 * 判断Object/int/long/Double数组内是否含有某元素
 * int/long/double数组求和/绝对和
 */
public class UArrays {
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 自定义数组转字符串
     * 可函数
     */
    public static <T> String toString(T[] array, Function<T, String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * int数组转字符串
     * 可函数
     */
    public static String toString(int[] array, IntFunction<String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * long数组转字符串
     * 可函数
     */
    public static String toString(long[] array, LongFunction<String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * double数组转字符串
     * 可函数
     */
    public static String toString(double[] array, DoubleFunction<String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * 自定义数组间转化
     * 可函数
     */
    public static <T, R> R[] toArray(T[] array, Function<T, R> action) {
        if (array == null) return null;
        R[] r = (R[]) Array.newInstance(action.apply(array[0]).getClass(), array.length);
        for (int i = 0; i < array.length; i++) {
            r[i] = action.apply(array[i]);
        }
        return r;
    }

    /**
     * 自定义数组转int数组
     */
    public static <T> int[] toArray(T[] array, ToIntFunction<T> action) {
        if (array == null) return new int[0];
        int[] r = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsInt(array[i]);
        }
        return r;
    }

    /**
     * 自定义数组转long数组
     * 可函数
     */
    public static <T> long[] toArray(T[] array, ToLongFunction<T> action) {
        if (array == null) return new long[0];
        long[] r = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsLong(array[i]);
        }
        return r;
    }

    /**
     * 自定义数组转double数组
     * 可函数
     */
    public static <T> double[] toArray(T[] array, ToDoubleFunction<T> action) {
        if (array == null) return new double[0];
        double[] r = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsDouble(array[i]);
        }
        return r;
    }

    /**
     * 数组ForEach
     *
     * @param array
     * @param consumer
     * @param <T>
     */
    public static <T> void forEach(T[] array, ObjIntConsumer<T> consumer) {
        if (array == null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                consumer.accept(array[i], i);
            }
        }
    }

    /**
     * int数组转byte数组
     */
    public static byte[] I2B(int[] array) {
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = (byte) array[i];
        }
        return bytes;
    }

    /**
     * bytr数组转int数组
     */
    public static int[] B2I(byte[] array) {
        int[] ints = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            ints[i] = array[i];
        }
        return ints;
    }

    /**
     * int数组转long数组
     */
    public static long[] I2L(int[] array) {
        long[] longs = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            longs[i] = (byte) array[i];
        }
        return longs;
    }

    /**
     * 数组转自定义List
     * 生成可变List
     * 若生成不可变List
     * 请使用{@link UArrays#toArray(Object[], Function)}加Arrays.asList(array)
     */
    public static <T, R> List<R> toList(T[] array, Function<T, R> action) {
        if (array == null || action == null) return Collections.emptyList();
        List<R> r = new ArrayList<>(array.length);
        for (T t : array) {
            r.add(action.apply(t));
        }
        return r;
    }

    /**
     * 数组转自定义Set
     * 重复项会被合并
     */
    public static <T, R> Set<R> toSet(T[] array, Function<T, R> action) {
        if (array == null) return Collections.emptySet();
        Set<R> r = new HashSet<>(array.length);
        for (T t : array) {
            r.add(action.apply(t));
        }
        return r;
    }

    /**
     * Collection中添加自定义数组
     *
     * @param action 自定义操作,为null则执行强转(危险)
     *               action返回NULL则不添加
     */
    public static <T, R> void addArrays(Collection<R> list, T[] array, Function<T, R> action) {
        if (array == null || list == null) return;
        for (T t : array) {
            //TODO:泛型怎么检查啊,算了就这样吧
            if (action == null) {
                list.add((R) t);
            } else {
                R r = action.apply(t);
                if (r != null) {
                    list.add(r);
                }
            }
        }
    }

    /**
     * 判断Object数组内是否含有某单个元素
     */
    public static boolean isContain(Object[] mList, Object element) {
        if (mList == null) {
            return false;
        }
        for (Object l : mList) {
            if (l.equals(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断int数组内是否含有某单个元素
     */
    public static boolean isContain(int[] mList, int element) {
        if (mList == null) {
            return false;
        }
        for (int l : mList) {
            if (l == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断long数组内是否含有某单个元素
     */
    public static boolean isContain(long[] mList, long element) {
        if (mList == null) {
            return false;
        }
        for (long l : mList) {
            if (l == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断double数组内是否含有某单个元素
     */
    public static boolean isContain(double[] mList, double element) {
        if (mList == null) {
            return false;
        }
        for (double l : mList) {
            if (l == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算int/long/double数组的和
     */
    public static long sum(int[] array) {
        long sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }


    public static double sum(double[] array) {
        double sum = 0;
        for (double i : array) {
            sum += i;
        }
        return sum;
    }

    /**
     * 计算int/long/double数组的绝对和
     */
    public static long absoluteSum(int[] array) {
        long sum = 0;
        for (int i : array) {
            if (i < 0) i = i * -1;
            sum += i;
        }
        return sum;
    }

    public static BigInteger absoluteSum(long[] array) {
        BigInteger sum = BigInteger.ZERO;
        for (long i : array) {
            if (i <= 0) i = i * -1;
            sum.add(BigInteger.valueOf(i));
        }
        return sum;
    }

    public static double absoluteSum(double[] array) {
        long sum = 0;
        for (double i : array) {
            if (i < 0) i = i * -1;
            sum += i;
        }
        return sum;
    }
}
