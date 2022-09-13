package candyenk.java.utils;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Java数组工具
 * 自定义数组ToString
 * 自定义数组转化
 * 数组转自定义List
 * 数组转自定义Set
 * Collection添加自定义数组
 * 判断Object/int/Double数组内是否含有某元素
 * int/long数组求和/绝对和
 */
public class UArrays {
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 自带数据处理的数组ToString
     */
    public static <T> String toString(T[] array, Function<T, String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    public static String toString(int[] array, Function<Integer, String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    public static String toString(long[] array, Function<Long, String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    public static String toString(double[] array, Function<Double, String> action) {
        if (array == null) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * 自定义数组间转化
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
            } else if (action.apply(t) != null) {
                list.add(action.apply(t));
            }
        }
    }

    /**
     * 判断Object数组内是否含有某单个元素
     *
     * @param mList   Object数组
     * @param element 检查Object元素
     * @return 是否含有
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
     *
     * @param mList
     * @param element
     * @return
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
     * 判断double数组内是否含有某单个元素
     *
     * @param mList
     * @param element
     * @return
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
     * 计算int/long数组的和
     */
    public static long sum(int[] array) {
        long sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }

    public static BigInteger sum(long[] array) {
        BigInteger sum = BigInteger.ZERO;
        for (long i : array) {
            sum.add(BigInteger.valueOf(i));
        }
        return sum;
    }

    /**
     * 计算int/long数组的绝对和
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
}
