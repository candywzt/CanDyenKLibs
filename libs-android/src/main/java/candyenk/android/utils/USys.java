package candyenk.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import candyenk.android.asbc.ActivityCDK;
import candyenk.android.asbc.FragmentCDK;

import java.util.Random;

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
     * 检查权限
     */
    public static boolean checkPermission(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 申请权限
     */
    public static void requestPermission(Activity activity, int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void requestPermission(Fragment fragment, int requestCode, String... permissions) {
        fragment.requestPermissions(permissions, requestCode);
    }

    public static void reauestPermission(ActivityCDK activity, ActivityCDK.PermissionsCallBack callback, String... permissions) {
        int requestCode = new Random().nextInt(65535);
        if (activity.addPermissionCallback(requestCode, callback)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else reauestPermission(activity, callback, permissions);
    }

    public static void reauestPermission(FragmentCDK fragment, ActivityCDK.PermissionsCallBack callback, String... permissions) {
        int requestCode = new Random().nextInt(65535);
        if (fragment.addPermissionCallback(requestCode, callback)) {
            fragment.requestPermissions(permissions, requestCode);
        } else reauestPermission(fragment, callback, permissions);
    }

    /**
     * Android 11所有文件读取权限申请
     * MF权限:MANAGE_EXTERNAL_STORAGE
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void requestAllFiles(Activity activity) {
        if (hasAllFiles()) return;
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    /**
     * Android 11所有文件读取权限判断
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static boolean hasAllFiles() {
        return Environment.isExternalStorageManager();
    }



}
