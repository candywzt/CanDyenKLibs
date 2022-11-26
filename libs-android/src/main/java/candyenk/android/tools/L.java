package candyenk.android.tools;


import android.util.Log;

/**
 * Android Log 的帮助类
 */
public class L {
    private static boolean isDebug;

    public static void debug(boolean isDebug) {
        L.isDebug = isDebug;
    }

    public static boolean v(String tag, Object msg) {
        if (isDebug) Log.v(tag, msg.toString());
        return false;
    }


    public static boolean d(String tag, Object msg) {
        if (isDebug) Log.d(tag, msg == null ? "NULL" : msg.toString());
        return false;
    }


    public static boolean i(String tag, Object msg) {
        if (isDebug) Log.i(tag, msg == null ? "NULL" : msg.toString());
        return false;
    }


    public static boolean w(String tag, Object msg) {
        if (isDebug) Log.w(tag, msg == null ? "NULL" : msg.toString());
        return false;
    }

    /**
     * 自带false的日志Error打印
     */
    public static boolean e(String tag, Object msg) {
        if (msg instanceof Exception) return e(tag, (Exception) msg, null);
        if (isDebug) Log.e(tag, msg == null ? "NULL" : msg.toString());
        return false;
    }

    /**
     * 自带false的日志Error打印
     */
    public static boolean e(String tag, Exception e, Object msg) {
        String mm = msg == null ? "NULL" : msg.toString();
        String ee = e == null ? "" : (":" + e.getClass().getSimpleName() + "-" + e.getMessage());
        boolean r = e(tag, mm + ee);
        if (e != null) e.printStackTrace();
        return r;
    }

    /**
     * 自带返回值的日志Error打印
     */
    public static <T> T e(String tag, Exception e, Object msg, T o) {
        e(tag, e, msg);
        return o;
    }

    /**
     * 自带返回值的日志Error打印
     */
    public static <T> T e(String tag, Object msg, T o) {
        e(tag, msg);
        return o;
    }
}

