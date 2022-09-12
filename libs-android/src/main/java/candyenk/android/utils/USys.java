package candyenk.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

/**
 * Android系统工具类
 * 在Activity和Fragment中申请单个/多个权限
 * 仅支持AndroidX的Fragment
 */
public class USys {
    public static final String RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;//录音权限
    public static final String GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;//读取通讯录权限
    public static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;//拨打电话权限
    public static final String READ_CALENDAR = Manifest.permission.READ_CALENDAR;//读取日历权限
    public static final String CAMERA = Manifest.permission.CAMERA;//拍照录像权限
    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;//获取位置信息权限
    public static final String BODY_SENSORS = Manifest.permission.BODY_SENSORS;//获取身体传感器权限
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;//文件读写权限
    public static final String SEND_SMS = Manifest.permission.SEND_SMS;//短信权限
    private static final int requestCode = 100;

    /**
     * 检查权限
     */
    public static boolean checkPermission(Activity context) {
        return PermissionChecker.checkSelfPermission(context, RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 申请权限
     */
    public static void requestPermission(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    public static void requestPermission(Fragment fragment, String permission) {
        fragment.requestPermissions(new String[]{permission}, requestCode);
    }

    /**
     * 申请权限组
     */
    public static void applySelfPermission(Activity activity, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void applySelfPermission(Fragment fragment, String... permissions) {
        fragment.requestPermissions(permissions, requestCode);
    }

    /**
     * 申请结果回调
     * 在Activity和Fragment的onRequestPermissionsResult中调用
     */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Consumer<String> successListener, Consumer<String> failListener) {
        if (requestCode == USys.requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED && successListener != null) {
                    successListener.accept(permissions[i]);
                } else if (grantResults[i] != PackageManager.PERMISSION_GRANTED && failListener != null) {
                    failListener.accept(permissions[i]);
                }
            }
        }
    }
}
