package candyenk.android.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

    public static void startActivity(Activity activity, Class<? extends Activity> classz) {
        Intent intent = new Intent(activity, classz);
        activity.startActivityForResult(intent, classz.hashCode());
    }

    public static void startActivity(Activity activity, Class<? extends Activity> classz, Bundle b) {
        Intent intent = new Intent(activity, classz);
        activity.startActivityForResult(intent, classz.hashCode(), b);
    }

    /**
     * 写入字符数据到系统剪切板
     */
    public static void writeClipboard(Activity activity, CharSequence text) {
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("label", text);
        cm.setPrimaryClip(cd);
    }

    /**
     * 读取系统剪切板最近一项(字符序列形式)
     */
    public static CharSequence readClipboard(Activity activity) {
        CharSequence text = "";
        try {
            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = cm.getPrimaryClip();
            ClipData.Item item = clip.getItemAt(0);
            text = item.coerceToText(activity);
        } catch (Exception e) {
        }
        return text;
    }
}
