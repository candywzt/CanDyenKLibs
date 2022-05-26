package candyenk.java.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    /**
     * Long时间戳转字符串
     */
    public static String D2S(long time, String formatType) {
        return new SimpleDateFormat(formatType).format(new Date(time));
    }

    public static String D2S(long time) {
        return D2S(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串转Long时间戳
     */
    public static long S2D(String time, String formatType) {
        try {
            return new SimpleDateFormat(formatType).parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long S2D(String time) {
        return S2D(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前时间字符串
     */
    public static String getNowTime(String formatType) {
        return D2S(System.currentTimeMillis(), formatType);
    }

    public static String getNowTime() {
        return D2S(System.currentTimeMillis());
    }

    /**
     * 毫秒量转时间量字符串
     */
    public static String Ms2String(long time, String formatType) {
        final long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        final long hr = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hr);
        final long min = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(min);
        final long sec = TimeUnit.MILLISECONDS.toSeconds(time);
        time -= TimeUnit.SECONDS.toMillis(sec);
        return String.format(formatType, days, hr, min, sec, time);
    }

    public static String Ms2String(long time) {
        return Ms2String(time, "%d days, %02d:%02d:%02d %04d");
    }

    /**
     * 毫秒量转时间量字符串
     * 自动匹配最合适单位
     */
    public static String Ms2StringAuto(long time) {
        return null;
    }

}
