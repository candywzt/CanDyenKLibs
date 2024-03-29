package candyenk.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.LayoutRes;

import java.util.function.Consumer;

/**
 * Android布局工具
 * 是不是横屏
 * 分辨率DP->PX
 * 分辨率PX->DP
 * View转Bitmap(截图形式,丢失功能)
 */
public class ULay {
    /**
     * 是不是横屏
     */
    public static boolean isLand(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    /**
     * 获取可显示屏幕分辨率(Android11以下不包括状态栏)
     * TODO:分屏小窗似乎异常
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
            r = wm.getCurrentWindowMetrics().getBounds();
        } else {
            r = new Rect();
            wm.getDefaultDisplay().getRectSize(r);
        }
        return r;
    }

    /**
     * 获取可显示屏幕横向分辨率
     * TODO:分屏小窗似乎异常
     */
    public static int getWidth(Context context) {
        return getSize(context).width();
    }

    /**
     * 获取可显示屏幕横向分辨率
     *  TODO:分屏小窗似乎异常
     */
    public static int getHeight(Context context) {
        return getSize(context).height();
    }


    /**
     * 根据手机的分辨率
     * 从dp转成为px
     * 支持负数
     */
    public static float dp2px(Context context, float dp) {
        float num = dp < 0 ? -1 : 1;
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + (0.5f * num);
    }

    /**
     * 根据手机的分辨率
     * 从px转成为dp
     */
    public static float px2dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px / scale + 0.5f;
    }

    /**
     * 解析XML布局
     * 自带布局绑定
     */
    public static <T extends View> T parseXML(Context context, @LayoutRes int id, Consumer<T> c) {
        T view = (T) LayoutInflater.from(context).inflate(id, null);
        if (c != null) c.accept(view);
        return view;
    }

    /**
     * View转Bitmap
     */
    public static Bitmap ViewToBitmap(View view) {
        // 把一个View转换成图片
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //测量在屏幕上宽和高
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //确定View的大小和位置的,然后将其绘制出来
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
}
