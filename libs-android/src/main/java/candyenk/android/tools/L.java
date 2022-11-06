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

    public static int v(String tag, Object msg) {
        if (isDebug) return Log.v(tag, msg.toString());
        return 0;

    }


    public static int d(String tag, Object msg) {
        if (isDebug) return Log.d(tag, msg == null ? "NULL" : msg.toString());
        return 0;
    }


    public static int i(String tag, Object msg) {
        if (isDebug) return Log.i(tag, msg == null ? "NULL" : msg.toString());
        return 0;
    }


    public static int w(String tag, Object msg) {
        if (isDebug) return Log.w(tag, msg == null ? "NULL" : msg.toString());
        return 0;
    }


    public static int e(String tag, Object msg) {
        if (msg instanceof Exception) return e(tag, (Exception) msg, null);
        if (isDebug) return Log.e(tag, msg == null ? "NULL" : msg.toString());
        return 0;
    }

    public static int e(String tag, Exception e, Object msg) {
        String mm = msg == null ? "NULL" : msg.toString();
        String ee = e == null ? "" : (":" + e.getClass().getSimpleName() + "-" + e.getMessage());
        if (e != null) e.printStackTrace();
        return e(tag, mm + ee);
    }
}

