package candyenk.android.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import candyenk.android.asbc.ActivityCDK;
import candyenk.android.asbc.FragmentCDK;

import java.util.Random;

/**
 * Android共享工具
 * 处理安卓应用间数据交换工具
 */
public class UShare {
    /**
     * 方便的启动新activity
     * 传参请自行创建
     */
    public static void startActivity(Context context, Class<? extends Activity> classz) {
        Intent intent = new Intent(context, classz);
        context.startActivity(intent);
    }

    public static void startActivity(ActivityCDK activity, Intent intent, ActivityCDK.ActivityCallBack callBack) {
        startActivity(activity, intent, null, callBack);
    }

    public static void startActivity(ActivityCDK activity, Intent intent, Bundle data, ActivityCDK.ActivityCallBack callBack) {
        int requestCode = new Random().nextInt(65535);
        if (activity.addActiveCallback(requestCode, callBack)) {
            activity.startActivityForResult(intent, requestCode, data);
        } else startActivity(activity, intent, data, callBack);
    }

    public static void startActivity(FragmentCDK fragment, Intent intent, ActivityCDK.ActivityCallBack callBack) {
        startActivity(fragment, intent, null, callBack);
    }

    public static void startActivity(FragmentCDK fragment, Intent intent, Bundle data, ActivityCDK.ActivityCallBack callBack) {
        int requestCode = new Random().nextInt(65535);
        if (fragment.addActiveCallback(requestCode, callBack)) {
            fragment.startActivityForResult(intent, requestCode, data);
        } else startActivity(fragment, intent, data, callBack);
    }

    /**
     * 通用浏览器打开页面
     */
    public static void startBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 写入字符数据到系统剪切板
     */
    public static void writeClipboard(Context context, CharSequence text) {
        ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("label", text);
        cm.setPrimaryClip(cd);
    }

    /**
     * 读取系统剪切板最近一项(字符序列形式)
     */
    public static CharSequence readClipboard(Context context) {
        CharSequence text = "";
        try {
            ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
            ClipData clip = cm.getPrimaryClip();
            ClipData.Item item = clip.getItemAt(0);
            text = item.coerceToText(context);
        } catch (Exception ignored) {}
        return text;
    }

    public static ClipData.Item[] readAllClipboard(Context context) {
        ClipboardManager cm = USys.getSystemService(context, Context.CLIPBOARD_SERVICE);
        ClipData clip = cm.getPrimaryClip();
        ClipData.Item[] items = new ClipData.Item[clip.getItemCount()];
        for (int i = 0; i < items.length; i++) items[i] = clip.getItemAt(i);
        return items;
    }
}
