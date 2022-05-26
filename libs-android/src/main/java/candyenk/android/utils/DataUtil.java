package candyenk.android.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 数据处理工具类
 */
public class DataUtil {

    /**
     * Long时间转字符串时间
     *
     * @param date    long时间
     * @param pattern 时间正则表达式
     * @return 字符串时间
     */
    public static String DateToString(long date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String DateToString(long date) {
        return DateToString(date, "yyyy-MM-dd HH:mm:ss");
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

    /**
     * 字节long转带单位字符串
     *
     * @param size  字节量大小
     * @param grade 等级0:自动;1:KB等级;2:MB等级;3:GB等级;
     * @return
     */
    public static String FormetFileSize(long size, int grade) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (size < 1024 && grade == 0) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576 && grade == 0 || grade == 1) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824 && grade == 0 || grade == 2) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * int转短单位数字字符串
     * 支持负数
     */
    public static String intToShortString(int value) {
        DecimalFormat df = new DecimalFormat("#.0");
        String number;
        if (value < 1000) {
            number = "" + value;
        } else if (value < 1000000) {
            number = df.format(value * 0.001f) + "k";
        } else if (value < 1000000000) {
            number = df.format(value * 0.000001f) + "m";
        } else {
            number = df.format(value * 0.000000001f) + "b";
        }
        return number;
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
}
