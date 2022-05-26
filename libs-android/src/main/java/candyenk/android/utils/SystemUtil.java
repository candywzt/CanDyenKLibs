package candyenk.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import candyenk.android.activity.CDKActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统相关工具类
 *
 */
public class SystemUtil {
    /**********************************************************************************************/
    /***********************************************工具********************************************/
    /**********************************************************************************************/
    /**
     * 获取已安装应用Info列表
     * 权限QUERY_ALL_PACKAGES
     */
    public static List<PackageInfo> getPackageInfoList(Context context) {
        List<PackageInfo> packageInfos = new ArrayList<>();
        try {
            packageInfos = context.getPackageManager().getInstalledPackages(
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Throwable t) {
            //TODO:异常处理
            t.printStackTrace();
        }
        return packageInfos;
    }

    /**
     * 获取已安装应用包名列表
     * 权限QUERY_ALL_PACKAGES
     */
    public static List<String> getPackageNameList(Context context) {
        List<String> packages = new ArrayList<>();
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            for (PackageInfo info : packageInfos) {
                String pkg = info.packageName;
                packages.add(pkg);
            }
        } catch (Throwable t) {
            //TODO:异常处理
            t.printStackTrace();
        }
        return packages;
    }

    /**
     * 通过包名获取对应应用名
     */
    public static String getAPKName(Context context, String packageName) {
        String pkgName = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            pkgName = pi.applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            //TODO:异常处理
            Log.e("获取应用Info出错", "" + e.getMessage());
        } catch (Exception e) {
            //TODO:异常处理
            Log.e("获取应用Info出错", "" + e.getMessage());
        }
        return pkgName;
    }

    /**
     * 通过包名获取对应应用图标
     */
    public static Drawable getAPKIcon(Context context, String packageName) {
        Drawable icon = null;
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            icon = pi.applicationInfo.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            //TODO:异常处理
            Log.e("获取应用Info出错", e.getMessage());
        } catch (Exception e) {
            //TODO:异常处理
            Log.e("获取应用Info出错", e.getMessage());
        }
        return icon;
    }

    /**
     * 检查权限(组)
     */
    public static boolean checkSelfPermission(Context context, String... permissions) {
        ArrayList<String> mPermissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(context, permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        return mPermissionList.isEmpty();
    }

    /**
     * 申请权限组
     */
    public static void applySelfPermission(CDKActivity activity, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, 1);//这个东西是时间？？2000

    }

    /**
     * 检查申请权限组一条龙
     */
    public static void applyForPermission(CDKActivity activity, String... permissions) {
        ArrayList<String> mPermissionList = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(p);
            }
        }
        if (mPermissionList.isEmpty()) {

        } else {
            applySelfPermission(activity, mPermissionList.toArray(new String[0]));
        }
    }
}
