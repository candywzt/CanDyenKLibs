package candyenk.android.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 安卓应用工具
 * 与自身相关方法需初始化
 * 静态功能:
 * 获取应用包名
 * 获取应用名
 * 获取应用版本名
 * 获取应用版本代码
 * 获取应用图标
 * 获取已安装所有用户应用信息
 */
public class UApp {
    public static String packageName;//自身包名
    public static String appName;//应用名
    public static String versionName;//自身版本名
    public static int versionCode;//自身版本代码
    public static long firstInstallTime;//自身首次安装时间
    public static long lastUpdateTime;//自身最后更新时间
    public static ActivityInfo[] activityInfos;//自身Activity列表
    public static ActivityInfo startActivity;//自身启动Activity

    private static Application app;
    private static PackageManager pm;
    private static PackageInfo info;


    /**********************************************************************************************/
    /******************************************静态方法*********************************************/
    /**********************************************************************************************/
    /**
     * 初始化，在Application调用
     */
    public static void init(Application application) {
        app = application;
        packageName = application.getPackageName();
        pm = app.getPackageManager();
        startActivity = StartActivityInit();
        try {
            info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        } catch (Exception e) {
            //TODO:初始化异常
            return;
        }
        //TODO:需要这么麻烦吗
        appName = app.getResources().getString(info.applicationInfo.labelRes);
        versionName = info.versionName;
        versionCode = info.versionCode;
        firstInstallTime = info.firstInstallTime;
        lastUpdateTime = info.lastUpdateTime;
        activityInfos = info.activities;

    }

    /**
     * 获取当前安装的所有 用户应用 信息
     */
    public static List<PackageInfo> getInstalledPackagesInfo() {
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<PackageInfo> packageInfoList = new ArrayList<>();
        for (int i = 0; i < packageInfos.size(); i++) {
            if ((packageInfos.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                packageInfoList.add(packageInfos.get(i));
            }
        }
        return packageInfoList;
    }


    /**
     * 获取应用包名
     * ???
     */
    public static String getAppPackageName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    /**
     * 获取应用名称
     *
     * @Exception 异常返回null
     */
    public static String getAppName(String packageName) {
        try {
            return pm.getPackageInfo(packageName, 0).applicationInfo.loadLabel(pm).toString();
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 获取应用版本名
     *
     * @Exception 返回null
     */
    public static String getVersionName(String packageName) {
        try {
            return pm.getPackageInfo(packageName, 0).versionName;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取应用版本号
     *
     * @Exception 异常返回-1
     */
    public static int getVersionCode(String packageName) {
        try {
            return pm.getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 获取应用图标
     *
     * @Exception 异常返回null
     */
    public static Drawable getApplicationIcon(String packageName) {
        try {
            return pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm);
        } catch (Exception e) {
            return null;
        }
    }

    /**********************************************************************************************/
    /***************************************私有静态方法*********************************************/
    /**********************************************************************************************/

    private static ActivityInfo StartActivityInit() {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveinfoList = pm.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            return resolveinfo.activityInfo;
        }
        return null;
    }
}
