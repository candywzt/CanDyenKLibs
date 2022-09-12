package candyenk.java.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Java字符串工具
 * 拼接
 * 判空
 * 静态功能
 * 手机号判断(11)
 * 邮箱判断
 * 纯数字判断
 * 数字加字母判断
 * 身份证判断(15/18)
 * 网址判断
 * IPV4地址判断
 */
public class UString {
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 字符串拼接
     *
     * @param addstr 拼接分隔符
     */
    public static String append(String addstr, String... strs) {
        if (addstr == null) {
            addstr = "";
        }
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
     * @param addstr 拼接分隔符
     */
    public static String appendT(String addstr, String... strs) {
        if (addstr == null) {
            addstr = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]).append(addstr);
        }
        return sb.toString();
    }

    /**
     * 字符串拼接(加头)
     *
     * @param addstr 拼接分隔符
     */
    public static String appendH(String addstr, String... strs) {
        if (addstr == null) {
            addstr = "";
        }
        StringBuilder sb = new StringBuilder(addstr);
        for (int i = 0; i < strs.length - 1; i++) {
            sb.append(strs[i]).append(addstr);
        }
        sb.append(strs[strs.length - 1]);
        return sb.toString();
    }

    /**
     * 字符串拼接(加头尾)
     *
     * @param addstr 拼接分隔符
     */
    public static String appendHT(String addstr, String... strs) {
        if (addstr == null) {
            addstr = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(strs[i]).append(addstr);
        }
        return sb.toString();
    }

    /**
     * 字符串按行分割
     */
    public static void toLine(String str, Consumer<String> action) {
        if (!isEmpty(str) && action == null) {
            str = lineReplace(str);
            for (String s : str.split("\n")) {
                action.accept(s);
            }
        }
    }

    /**
     * 字符串按行转为list
     *
     * @param isVariable 返回List是否可变
     * @return 传入字符串为空则返回null
     */
    public static List<String> toLineList(String str, boolean isVariable) {
        if (!isEmpty(str)) {
            if (isVariable) {
                List<String> list = new ArrayList<>();
                toLine(str, s -> list.add(s));
                return list;
            } else {
                return Arrays.asList(lineReplace(str).split("\n"));
            }
        }
        return null;
    }

    /**
     * 判断字符串是否为NULL或空
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * 判断字符串是否为11位手机号
     * 138xxxxxxxx
     */
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) return false;
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-2,5-9])|(17[0-9]))\\d{8}$");
        return pattern.matcher(phone).matches();
    }

    /**
     * 判断字符串是否为邮箱地址
     * xxxxx@gmail.com
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) return false;
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        return pattern.matcher(email).matches();
    }

    /**
     * 判断字符串是否为纯数字
     * 12345678
     */
    public static boolean isNumer(String number) {
        if (isEmpty(number)) return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(number).matches();

    }

    /**
     * 判断字符串是否为数字或字母
     */
    public static boolean isNumberOrAlpha(String string) {
        if (isEmpty(string)) return false;
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]*");
        return pattern.matcher(string).matches();
    }

    /**
     * 判断字符类型是否是身份证号
     * 好鸡肋...
     */
    public static boolean isIDCard(String idCard) {
        if (!isNumberOrAlpha(idCard)) return false;
        return idCard.length() == 15 || idCard.length() == 18;
    }

    /**
     * 判断字符类型是否是网址
     * 这个也很离谱
     */
    public static boolean isUrl(String url) {
        if (isEmpty(url)) return false;
        url = url.toLowerCase();
        return !url.startsWith("http://") && !url.startsWith("https://") && !url.contains("..") && url.split(".").length == 3;
    }
    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/
    private static String lineReplace(String string) {
        return string.replaceAll("\\r\\n|\\r", "\n")
                .replaceAll("\\t", "    ")
                .replaceAll("\\u00a0", " ")
                .replaceAll("\\u2424", "\n");
    }
}
