package candyenk.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.WindowManager;

/**
 * Android设备工具
 */
public class UDrive {
    /**
     * 获取设备硬件分辨率
     */
    public static Rect getSize(Context context) {
        WindowManager wm;
        Rect r;
        if (context instanceof Activity) {
            wm = ((Activity) context).getWindowManager();
        } else {
            wm = USys.getSystemService(context, Context.WINDOW_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            r = wm.getMaximumWindowMetrics().getBounds();
        } else {
            Point p = new Point();
            wm.getDefaultDisplay().getRealSize(p);
            r = new Rect(0, 0, p.x, p.y);
        }
        return r;
    }

    /**
     * 获取设备硬件横向分辨率
     */
    public static int getWidth(Context context) {
        return getSize(context).width();
    }

    /**
     * 获取设备硬件纵向分辨率
     */
    public static int getHeight(Context context) {
        return getSize(context).height();
    }
}
