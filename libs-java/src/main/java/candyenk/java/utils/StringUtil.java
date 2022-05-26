package candyenk.java.utils;

public class StringUtil {
    /**
     * 字符串拼接
     *
     * @param strs 拼接分隔符
     */
    public static String append(String addstr, String... strs) {
        if (addstr == null) {addstr = "";}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length - 1; i++) {
            sb.append(strs[i]).append(addstr);
        }
        sb.append(strs[strs.length - 1]);
        return sb.toString();
    }

    /**
     * 字符串拼接(加尾)
     *
     * @param strs 拼接分隔符
     */
    public static String appendT(String addstr, String... strs) {
        if (addstr == null) {addstr = "";}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]).append(addstr);
        }
        return sb.toString();
    }

    /**
     * 字符串拼接(加头)
     *
     * @param strs 拼接分隔符
     */
    public static String appendH(String addstr, String... strs) {
        if (addstr == null) {addstr = "";}
        StringBuilder sb = new StringBuilder(addstr);
        for (int i = 0; i < strs.length-1; i++) {
            sb.append(strs[i]).append(addstr);
        }
        sb.append(strs[strs.length - 1]);
        return sb.toString();
    }
    /**
     * 字符串拼接(加头尾)
     *
     * @param strs 拼接分隔符
     */
    public static String appendHT(String addstr, String... strs) {
        if (addstr == null) {addstr = "";}
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]).append(addstr);
        }
        return sb.toString();
    }
}
