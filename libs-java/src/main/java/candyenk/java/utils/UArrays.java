package candyenk.java.utils;

import candyenk.java.tools.R;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.*;

/**
 * Java数组工具
 * 功能很多不写了
 */
public class UArrays {
    /**********************************************************************************************/
    /**************************************数组判空*************************************************/
    /**********************************************************************************************/
    /**
     * 数组判空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * int数组判空
     */
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * long数组判空
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * double数组判空
     */
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * float数组判空
     */
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * byte数组判空
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * char数组判空
     */
    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * boolean数组判空
     */
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    /**
     * short数组判空
     */
    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }
    /**********************************************************************************************/
    /**************************************数组转字符串**********************************************/
    /**********************************************************************************************/
    /**
     * 数组转字符串
     * 直接调用toString方法
     *
     * @param array 数组
     */
    @SafeVarargs
    public static <T> String toString(T... array) {
        return toString(array, null);
    }


    /**
     * 数组转自定义字符串
     * 不定参数
     *
     * @param array  数组,为Empty则返回空字符串
     * @param action 函数,为null直接调用toString
     */
    @SafeVarargs
    public static <T> String toString(Function<T, String> action, T... array) {
        if (isEmpty(array)) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action == null ? array[i].toString() : action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * 数组转自定义字符串
     *
     * @param array  数组,为Empty则返回空字符串
     * @param action 函数,为null直接调用toString
     */
    public static <T> String toString(T[] array, Function<T, String> action) {
        if (isEmpty(array)) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action == null ? array[i].toString() : action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }


    /**
     * int数组转自定义字符串
     *
     * @param array  数组,为Empty则返回空字符串
     * @param action 函数,为null直接调用toString
     */
    public static String toString(int[] array, IntFunction<String> action) {
        if (isEmpty(array)) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action == null ? String.valueOf(array[i]) : action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * long数组转自定义字符串
     *
     * @param array  数组,为Empty则返回空字符串
     * @param action 函数,为null直接调用toString
     */
    public static String toString(long[] array, LongFunction<String> action) {
        if (isEmpty(array)) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action == null ? String.valueOf(array[i]) : action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }

    /**
     * double数组转自定义字符串
     *
     * @param array  数组,为Empty则返回空字符串
     * @param action 函数,为null直接调用toString
     */
    public static String toString(double[] array, DoubleFunction<String> action) {
        if (isEmpty(array)) return "";
        String[] strs = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            strs[i] = action == null ? String.valueOf(array[i]) : action.apply(array[i]);
        }
        return Arrays.toString(strs);
    }
    /**********************************************************************************************/
    /**************************************数组转化*************************************************/
    /**********************************************************************************************/
    /**
     * 自定义数组间转化
     *
     * @param array  数组,为null返回空数组
     * @param type   目标数组元素类型
     * @param action 函数,为null则强转
     */
    public static <T, R> R[] T2R(T[] array, Class<R> type, Function<T, R> action) {
        R[] r = (R[]) Array.newInstance(type, array == null ? 0 : array.length);
        for (int i = 0; i < (array == null ? 0 : array.length); i++) {
            r[i] = action == null ? (R) array[i] : action.apply(array[i]);
        }
        return r;
    }


    /**
     * 自定义数组转int数组
     *
     * @param array  数组,为null则返回空数组
     * @param action 函数,为null则返回空数组
     */
    public static <T> int[] T2I(T[] array, ToIntFunction<T> action) {
        if (isEmpty(array) || action == null) return new int[0];
        int[] r = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsInt(array[i]);
        }
        return r;
    }

    /**
     * 自定义数组转long数组
     *
     * @param array  数组,为null则返回空数组
     * @param action 函数,为null则返回空数组
     */
    public static <T> long[] T2L(T[] array, ToLongFunction<T> action) {
        if (isEmpty(array) || action == null) return new long[0];
        long[] r = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsLong(array[i]);
        }
        return r;
    }

    /**
     * 自定义数组转double数组
     *
     * @param array  数组,为null则返回空数组
     * @param action 函数,为null则返回空数组
     */
    public static <T> double[] T2D(T[] array, ToDoubleFunction<T> action) {
        if (isEmpty(array) || action == null) return new double[0];
        double[] r = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            r[i] = action.applyAsDouble(array[i]);
        }
        return r;
    }

    /**
     * byte数组转int数组
     *
     * @param array 数组,为null则返回空数组
     */
    public static int[] B2I(byte[] array) {
        if (isEmpty(array)) return new int[0];
        int[] arrays = new int[array.length];
        for (int i = 0; i < array.length; i++) arrays[i] = array[i];
        return arrays;
    }


    /**
     * int数组转long数组
     *
     * @param array 数组,为null则返回空数组
     */
    public static long[] I2L(int[] array) {
        if (isEmpty(array)) return new long[0];
        long[] arrays = new long[array.length];
        for (int i = 0; i < array.length; i++) arrays[i] = (byte) array[i];
        return arrays;
    }

    /**
     * float数组转double数组
     *
     * @param array 数组,为null则返回空数组
     */
    public static double[] F2D(float[] array) {
        if (isEmpty(array)) return new double[0];
        double[] arrays = new double[array.length];
        for (int i = 0; i < array.length; i++) arrays[i] = (byte) array[i];
        return arrays;
    }

    /**********************************************************************************************/
    /***********************************数组ForEach*************************************************/
    /**********************************************************************************************/
    /**
     * 数组ForEach
     */
    public static <T> void forEach(T[] array, Consumer<T> consumer) {
        if (isEmpty(array)) return;
        for (T t : array) consumer.accept(t);
    }

    /**
     * int数组ForEach
     */
    public static void forEach(int[] array, Consumer<Integer> consumer) {
        if (isEmpty(array)) return;
        for (int t : array) consumer.accept(t);
    }

    /**
     * long数组ForEach
     */
    public static void forEach(long[] array, Consumer<Long> consumer) {
        if (isEmpty(array)) return;
        for (long t : array) consumer.accept(t);
    }

    /**
     * double数组ForEach
     */
    public static void forEach(double[] array, Consumer<Double> consumer) {
        if (isEmpty(array)) return;
        for (double t : array) consumer.accept(t);
    }

    /**
     * float数组ForEach
     */
    public static void forEach(float[] array, Consumer<Float> consumer) {
        if (isEmpty(array)) return;
        for (float t : array) consumer.accept(t);
    }

    /**
     * byte数组ForEach
     */
    public static void forEach(byte[] array, Consumer<Byte> consumer) {
        if (isEmpty(array)) return;
        for (byte t : array) consumer.accept(t);
    }

    /**
     * char数组ForEach
     */
    public static void forEach(char[] array, Consumer<Character> consumer) {
        if (isEmpty(array)) return;
        for (char t : array) consumer.accept(t);
    }

    /**
     * boolean数组ForEach
     */
    public static void forEach(boolean[] array, Consumer<Boolean> consumer) {
        if (isEmpty(array)) return;
        for (boolean t : array) consumer.accept(t);
    }

    /**
     * short数组ForEach
     */
    public static void forEach(short[] array, Consumer<Short> consumer) {
        if (isEmpty(array)) return;
        for (short t : array) consumer.accept(t);
    }

    /**********************************************************************************************/
    /***********************************数组转集合*************************************************/
    /**********************************************************************************************/
    /**
     * 数组转自定义List
     * 可变List
     *
     * @param array  数组,为Empty返回空list
     * @param action 函数,为空则强制转换
     */
    public static <T, R> List<R> toList(T[] array, Function<T, R> action) {
        if (isEmpty(array)) return new ArrayList<>();
        List<R> list = new ArrayList<>(array.length);
        for (T t : array) list.add(action == null ? (R) t : action.apply(t));
        return list;
    }

    /**
     * 数组转自定义List
     * 不可变List
     * 重复项会被覆盖
     * 推荐使用{@link UArrays#T2R(Object[], Class, Function)}和{@link Arrays#asList(Object[])}
     *
     * @param array  数组,为Empty返回空list
     * @param action 函数,为空则强制转换
     */
    public static <T, R> List<R> asList(T[] array, Function<T, R> action) {
        if (isEmpty(array)) return Collections.emptyList();
        return Collections.unmodifiableList(toList(array, action));
    }

    /**
     * 数组转自定义Set
     * 可变Set
     * 重复项会被覆盖
     *
     * @param array  数组,为Empty返回空lis
     * @param action 函数,为空则强制转换
     */
    public static <T, R> Set<R> toSet(T[] array, Function<T, R> action) {
        if (isEmpty(array)) return new HashSet<>();
        Set<R> r = new HashSet<>(array.length);
        for (T t : array) r.add(action == null ? (R) t : action.apply(t));
        return r;
    }

    /**
     * 数组转自定义Set
     * 不可变Set
     * 重复项会被覆盖
     * 推荐使用{@link UArrays#T2R(Object[], Class, Function)}和{@link Arrays#asList(Object[])}和{@link Set#addAll(Collection)}
     *
     * @param array  数组,为Empty返回空lis
     * @param action 函数,为空则强制转换
     */
    public static <T, R> Set<R> asSet(T[] array, Function<T, R> action) {
        if (array == null) return Collections.emptySet();
        return Collections.unmodifiableSet(toSet(array, action));
    }

    /**********************************************************************************************/
    /**************************************数组查找*************************************************/
    /**********************************************************************************************/

    /**
     * 元素存在判断
     *
     * @param element 被判断元素
     * @param array   判断数组
     */
    public static <T> boolean isContain(Object element, T... array) {
        return indexOf(array, element) != -1;
    }

    /**
     * 查找元素首次出现索引
     *
     * @param element 被查找元素
     * @param array   查找数组
     */
    @SafeVarargs
    public static <T> int indexOf(T element, T... array) {
        return Arrays.asList(array).indexOf(element);
    }

    /**
     * 查找元素最后一次出现索引
     *
     * @param element 被查找元素
     * @param array   查找数组
     */
    @SafeVarargs
    public static <T> int lastIndexOf(T element, T... array) {
        return Arrays.asList(array).lastIndexOf(element);
    }
    /**********************************************************************************************/
    /*********************************数组计算******************************************************/
    /**********************************************************************************************/
    /**
     * 计算int数组和
     *
     * @param array 数组
     */
    public static long sum(int... array) {
        long sum = 0;
        for (int i : array) sum += i;
        return sum;
    }

    /**
     * 计算long数组和
     *
     * @param array 数组
     */
    public static BigInteger sum(long... array) {
        BigInteger sum = BigInteger.ZERO;
        for (long i : array) sum = sum.add(BigInteger.valueOf(i));
        return sum;
    }

    /**
     * 计算float数组和
     *
     * @param array 数组
     */
    public static double sum(float... array) {
        double sum = 0;
        for (float i : array) sum += i;
        return sum;
    }

    /**
     * 计算double数组和
     *
     * @param array 数组
     */
    public static BigDecimal sum(double... array) {
        BigDecimal sum = BigDecimal.ZERO;
        for (double i : array) sum = sum.add(BigDecimal.valueOf(i));
        return sum;
    }

    /**
     * 计算int数组绝对和
     *
     * @param array 数组
     */
    public static long absoluteSum(int... array) {
        long sum = 0;
        for (int i : array) sum += i < 0 ? i + -1 : i;
        return sum;
    }

    /**
     * 计算long数组绝对和
     *
     * @param array 数组
     */
    public static BigInteger absoluteSum(long... array) {
        BigInteger sum = BigInteger.ZERO;
        for (long i : array) sum = sum.add(BigInteger.valueOf(i < 0 ? i + -1 : i));
        return sum;
    }

    /**
     * 计算float数组绝对和
     *
     * @param array 数组
     */
    public static double absoluteSum(float... array) {
        double sum = 0;
        for (float i : array) sum += i < 0 ? i + -1 : i;
        return sum;
    }

    /**
     * 计算double数组绝对和
     *
     * @param array 数组
     */
    public static BigDecimal absoluteSum(double[] array) {
        BigDecimal sum = BigDecimal.ZERO;
        for (double i : array) sum = sum.add(BigDecimal.valueOf(i < 0 ? i + -1 : i));
        return sum;
    }
    /**********************************************************************************************/
    /**************************************其他操作*************************************************/
    /**********************************************************************************************/
    /**
     * 集合中添加数组
     *
     * @param action 函数,为null则执行强转(危险),返回值为NULL则不添加
     */
    public static <T, R, X extends Collection<R>> X add(X list, T[] array, Function<T, R> action) {
        if (isEmpty(array) || list == null) return list;
        for (T t : array) list.add(action == null ? (R) t : action.apply(t));
        return list;
    }

    /**
     * 集合中添加数组
     */
    public static <T, X extends Collection<T>> X add(X list, T[] array) {
        if (isEmpty(array) || list == null) return list;
        list.addAll(Arrays.asList(array));
        return list;
    }

    /**
     * 计算数组总长度
     *
     * @param os 数组们
     */
    public static int size(Object[]... os) {
        if (os == null) return 0;
        int size = 0;
        for (Object[] o : os) size += o == null ? 0 : o.length;
        return size;
    }

    /**
     * 数组合并
     *
     * @param array 数组们,为Empty时返回null
     */
    @SafeVarargs
    public static <T> T[] merge(T[]... array) {
        if (isEmpty(array)) return null;
        T[] arrays = Arrays.copyOf(array[0], size(array));
        int index = array[0].length;
        for (int i = 1; i < array.length; i++) {
            T[] ts = array[i];
            if (isEmpty(ts)) continue;
            System.arraycopy(ts, 0, arrays, index, ts.length);
            index += ts.length;
        }
        return arrays;
    }
}
