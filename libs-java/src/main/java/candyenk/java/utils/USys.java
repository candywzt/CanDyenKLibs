package candyenk.java.utils;

public class USys {
    /**
     * 获取对象地址值
     *
     * @param o 对象
     * @return 十六进制字符串的地址值
     */
    public static String hashCode(Object o) {
        return Integer.toHexString(hashCodeInt(o));
    }

    /**
     * 获取对象地址值(Int)
     *
     * @param o 对象
     * @return 十进制地址值
     */
    public static int hashCodeInt(Object o) {
        return System.identityHashCode(o);
    }
}
