package candyenk.android.tools;


import android.util.Log;

public class L {

    private L() {
    }


    public static int v(String tag, Object msg) {
        return Log.v(tag, msg.toString());
    }


    public static int d(String tag, Object msg) {
        return Log.d(tag, msg.toString());
    }


    public static int i(String tag, Object msg) {
        return Log.i(tag, msg.toString());
    }


    public static int w(String tag, Object msg) {
        return Log.w(tag, msg.toString());
    }


    public static int e(String tag, Object msg) {
        return Log.e(tag, msg.toString());
    }
}

