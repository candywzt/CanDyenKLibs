package candyenk.java.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Java时间工具
 * 获取当前时间字符串
 * Long时间戳转字符串
 * 字符串转Long时间戳
 * 毫秒量转时间量
 */
public class UTime {
    private static final int DAY = 86400000;
    private static final int HOUR = 3600000;
    private static final int MINUTE = 60000;
    private static final int SECOND = 1000;

    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 获取当前时间字符串
     */
    public static String getNowTime(String formatType) {
        return D2S(System.currentTimeMillis(), formatType);
    }

    public static String getNowTime() {
        return D2S(now());
    }

    /**
     * 获取当前时间
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Long时间戳转字符串
     * 单字符看这里{@link DateTimeFormatter#ofPattern(String)}
     */
    @SuppressWarnings("SimpleDateFormat")
    public static String D2S(long time, String formatType) {
        if (formatType.length() == 1) return DateTimeFormatter.ofPattern(formatType).format(Instant.ofEpochMilli(time));
        return new SimpleDateFormat(formatType).format(new Date(time));
    }

    public static String D2S(long time) {
        return D2S(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串转Long时间戳
     *
     * @return 字符串异常返回0
     */
    public static long S2D(String time, String formatType) {
        try {
            return new SimpleDateFormat(formatType).parse(time).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long S2D(String time) {
        return S2D(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 毫秒量转时间量字符串
     */
    public static String MS2S(long time, String formatType) {
        int[] times = MS2I(time);
        return String.format(formatType, times[0], times[1], times[2], times[3], times[4]);
    }

    public static String MS2S(long time) {
        return MS2S(time, "%d days, %02d:%02d:%02d %04d");
    }

    /**
     * 毫秒量转时间量字符串自动
     * 有点不智能---
     */
    public static String MS2A(long time) {
        final String[] unit = {"days", "h", "mm", "s", "ms"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < MS2I(time).length; i++) {
            if (i > 0) {
                sb.append(i).append(unit[i]).append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * 毫秒量转时间量int数组
     */
    public static int[] MS2I(long time) {
        int[] times = new int[5];
        times[0] = (int) TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(times[0]);
        times[1] = (int) TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(times[1]);
        times[2] = (int) TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(times[2]);
        times[3] = (int) TimeUnit.MILLISECONDS.toSeconds(time);
        times[4] = (int) (time - TimeUnit.SECONDS.toMillis(times[3]));
        return times;
    }
}

