package candyenk.java.utils;

import java.util.Arrays;
import java.util.function.Function;

public class ArraysUtil {

    /**
     * 自带数据处理的数组ToString方法
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
     * 计算数组的和
     */
    public static long sum(int[] array) {
        long sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }

    public static long sum(long[] array) {
        long sum = 0;
        for (long i : array) {
            sum += i;
        }
        return sum;
    }

}
