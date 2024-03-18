package candyenk.android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.PermissionChecker;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Android系统工具类
 * 在Activity和Fragment中申请单个/多个权限
 * 仅支持AndroidX的Fragment
 */
public class USys {
    public static final String USE_MIC = Manifest.permission.RECORD_AUDIO;//录音权限
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;//读取通讯录权限
    public static final String GET_PHONESTATE = Manifest.permission.READ_PHONE_STATE;//拨打电话权限
    public static final String GET_CALENDAR = Manifest.permission.READ_CALENDAR;//读取日历权限
    public static final String USE_CAMERA = Manifest.permission.CAMERA;//拍照录像权限
    public static final String GET_POS = Manifest.permission.ACCESS_FINE_LOCATION;//获取位置信息权限
    public static final String GET_SENSOR = Manifest.permission.BODY_SENSORS;//获取身体传感器权限
    public static final String SET_FILE = Manifest.permission.WRITE_EXTERNAL_STORAGE;//文件读写权限
    public static final String SET_SMS = Manifest.permission.SEND_SMS;//短信权限


    /**
     * 获取系统服务
     */
    public static <T> T getSystemService(Context context, String name) {
        return (T) context.getSystemService(name);
    }

    /**
     * 获取系统服务(强转为指定服务)
     */
    public static <T> T getSystemService(Context context, String name, Class<T> c) {
        return getSystemService(context, name);
    }

    /**
     * 弹出输入法
     */
    public static void showIM(Activity activity, View edit) {
        InputMethodManager imm = getSystemService(activity, Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 收回输入法
     */
    public static void hideIM(Activity activity) {
        InputMethodManager imm = getSystemService(activity, Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 获取NightMode,深色模式状态
     * 详细{@link UiModeManager#getNightMode()}
     */
    public int getNightMode(Context context) {
        return getSystemService(context, Context.UI_MODE_SERVICE, UiModeManager.class).getNightMode();
    }

    /**
     * 检查权限
     */
    public static boolean checkPermission(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 申请单个权限
     * ResultAPI
     *
     * @param permissions 权限
     * @param callback    申请结果
     */
    public static void requestPermission(@NonNull ComponentActivity activity, Consumer<Boolean> callback, @NonNull String permissions) {
        UShare.register(activity, new ActivityResultContracts.RequestPermission(), callback == null ? null : callback::accept).launch(permissions);
    }

    /**
     * 申请多个权限
     * ResultAPI
     *
     * @param permissions 权限组
     * @param callback    申请结果组
     */
    public static void requestPermission(@NonNull ComponentActivity activity, Consumer<Map<String, Boolean>> callback, @NonNull String... permissions) {
        if (permissions.length == 0) return;
        UShare.register(activity, new ActivityResultContracts.RequestMultiplePermissions(), callback == null ? null : callback::accept).launch(permissions);
    }

    /**
     * Android 11所有文件读取权限申请
     * MF权限:MANAGE_EXTERNAL_STORAGE
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void requestAllFiles(@NonNull ComponentActivity activity, Consumer<Boolean> callback) {
        if (hasAllFiles() || callback != null) callback.accept(true);
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        UShare.start(activity, intent, callback == null ? (b, i) -> {} : (i, d) -> callback.accept(i));
    }

    /**
     * Android 11所有文件读取权限判断
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean hasAllFiles() {
        return Environment.isExternalStorageManager();
    }


}
