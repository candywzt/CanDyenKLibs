package candyenk.java.utils;

/**
 * Java数据工具类
 * 频率单位自动转换
 * 千进制数据单位自动转换
 * 数据单位自动转换
 * 数据单位强制转换
 * 十六进制字符串转byte[]
 * byte[]转16进制字符串
 * int转十六进制字符串(颜色代码)
 * 十六进制字符串转int(颜色代码)
 */
public class UData {

    private static final int KIBI = 1 << 10;
    private static final int MEBI = 1 << 20;
    private static final int GIBI = 1 << 30;
    private static final long TEBI = 1L << 40;
    private static final long PEBI = 1L << 50;
    private static final long EXBI = 1L << 60;

    private static final int KILO = 1_000;
    private static final int MEGA = 1_000_000;
    private static final int GIGA = 1_000_000_000;
    private static final long TERA = 1_000_000_000_000L;
    private static final long PETA = 1_000_000_000_000_000L;
    private static final long EXA = 1_000_000_000_000_000_000L;

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/

    /**
     * 频率单位自动转换
     */
    public static String HZ2A(long value) {
        return L2A(value, "Hz");
    }

    /**
     * 千进制数据单位自动转换
     * 默认保留2位小数
     *
     * @param unit 单位后缀(频率HZ\还有啥?)
     */
    public static String L2A(long value, String unit) {
        return L2A(value, unit, "%.2f");
    }

    public static String L2A(long value, String unit, String formatType) {
        if (value < KILO) {
            return String.format("%d%s", value, unit).trim();
        } else if (value < MEGA) { // K
            return formatUnits(value, KILO, "K" + unit, formatType);
        } else if (value < GIGA) { // M,formatType
            return formatUnits(value, MEGA, "M" + unit, formatType);
        } else if (value < TERA) { // G,formatType
            return formatUnits(value, GIGA, "G" + unit, formatType);
        } else if (value < PETA) { // T,formatType
            return formatUnits(value, TERA, "T" + unit, formatType);
        } else if (value < EXA) { // P,formatType
            return formatUnits(value, PETA, "P" + unit, formatType);
        } else { // E
            return formatUnits(value, EXA, "E" + unit, formatType);
        }
    }

    /**
     * 1024进制数据单位自动转换(数据B\没了吧)
     * 默认保留2位小数
     */
    public static String B2A(long value) {
        return B2A(value, "%.2f");
    }

    public static String B2A(long value, String formatType) {
        if (value < KIBI) {
            return String.format("%dB", value).trim();
        } else if (value < MEBI) { // K
            return formatUnits(value, KIBI, "KB", formatType);
        } else if (value < GIBI) { // M,formatType
            return formatUnits(value, MEBI, "MB", formatType);
        } else if (value < TEBI) { // G,formatType
            return formatUnits(value, GIBI, "GB", formatType);
        } else if (value < PEBI) { // T,formatType
            return formatUnits(value, TEBI, "TB", formatType);
        } else if (value < EXBI) { // P,formatType
            return formatUnits(value, PEBI, "PB", formatType);
        } else { // E,formatType
            return formatUnits(value, EXBI, "EB", formatType);
        }
    }


    /**
     * 数据单位强制转换
     *
     * @param dp 保留的小数位数
     */
    public static String B2K(long value, int dp) {
        return formatUnits(value, KIBI, "KB", "%." + dp + "f");
    }

    public static String B2M(long value, int dp) {
        return formatUnits(value, MEBI, "MB", "%." + dp + "f");
    }

    public static String B2G(long value, int dp) {
        return formatUnits(value, GIBI, "GB", "%." + dp + "f");
    }

    /**
     * 十六进制字符串转byte数组
     * 取出字节码专用
     *
     * @param strIn 十六进制字符串
     * @return byte[]数组
     */
    public static byte[] hexToBytes(String strIn) {
        try {
            if (strIn == null) return null;
            byte[] arrB = strIn.getBytes();
            int iLen = arrB.length;
            byte[] arrOut = new byte[iLen / 2];
            for (int i = 0; i < iLen; i = i + 2) {
                String strTmp = new String(arrB, i, 2);
                arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
            }
            return arrOut;
        } catch (Exception e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return null;
    }


    /**
     * byte数组转十六进制字符串
     * 存储字节码专用
     *
     * @param b byte[]
     * @return 16进制字符串
     */
    public static String bytesToHex(byte[] b) {
        String stmp;
        StringBuilder sb = new StringBuilder();
        try {
            for (int n = 0; n < b.length; n++) {
                stmp = Integer.toHexString(b[n] & 0xFF);
                sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            }
        } catch (Exception e) {
            //TODO:异常处理
            e.printStackTrace();
        }

        return sb.toString().toUpperCase().trim();
    }

    /**
     * int转十六进制字符串
     * 范围
     * 0x00000000~0x7FFFFFFF(0~2147483647)
     * 0x80000000~0xFFFFFFFF(-2147483648~-1)
     */
    public static String decToHexString(int dec) {
        StringBuilder sb = new StringBuilder(8);
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (dec != 0) {
            sb.append(b[dec & 15]);
            dec = dec >>> 4;
        }
        for (int i = 8 - sb.length(); i > 0; i--) {
            sb.append(b[0]);
        }
        return sb.reverse().toString();
    }

    /**
     * 十六进制字符串转int整数
     * 输入格式0xFFFFFFFF或FFFFFFFF
     * <br><font color=red>无法转换负数</font>
     *
     * @hide
     */
    public static int hexStringToDec(String hexString) {
        //拷贝一份字符串
        String str = hexString;
        //去掉空白字符
        str.replaceAll("\\s*", "");
        //获取十六进制头
        String subHeader = "";
        if (str.length() > 2) {
            subHeader = str.substring(0, 2);
        }
        //找到并去除十六进制开头(0X和0x)
        if (subHeader.equals("0x") || subHeader.equals("0X")) {
            str.replaceAll(subHeader, "");
        }
        //字符串长度大于8，PASS
        if (str.length() > 8) {
            throw new NumberFormatException("数据长度过长");
        }
        //创建输出int
        int dec = Integer.parseInt(hexString, 16);
        return dec;
    }

    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/
    private static String formatUnits(long value, long prefix, String unit, String formatType) {
        if (value % prefix == 0) {
            return String.format("%d%s", value / prefix, unit).trim();
        }
        return String.format(formatType + "%s", (double) value / prefix, unit).trim();
    }
}
