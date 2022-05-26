package candyenk.android.utils;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * App自身应用工具
 */
public class AppUtil {
    public static String packageName;
    public static String versionName;
    public static int versionCode;
    public static long firstInstallTime;
    public static long lastUpdateTime;

    private static AppUtil Instance;
    private static Application application;
    private static ActivityInfo[] activityInfos;


    //获取CrashHandler实例 ,单例模式
    public static AppUtil INSTANCE() {
        if (Instance == null)
            Instance = new AppUtil();
        return Instance;
    }

    /**
     * 初始化，在Application调用
     */
    public void init(Application application) {
        this.application = application;
        this.packageName = application.getPackageName();
        AppinfoInit();
        ActivityInit();
    }
    /**********************************************************************************************/
    /******************************************私有方法*********************************************/
    /**********************************************************************************************/
    private void AppinfoInit() {
        PackageInfo packageInfo = getPackageInfo(0);
        this.versionName = packageInfo.packageName;
        this.versionCode = packageInfo.versionCode;
        this.firstInstallTime = packageInfo.firstInstallTime;
        this.lastUpdateTime = packageInfo.lastUpdateTime;
    }

    private void ActivityInit() {
        PackageInfo packageInfo = getPackageInfo(PackageManager.GET_ACTIVITIES);
        this.activityInfos = packageInfo.activities;
    }

    private PackageInfo getPackageInfo(int flages) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = application.getPackageManager()
                    .getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            //TODO:异常处理
            e.printStackTrace();
        }
        return packageInfo;
    }
    /**********************************************************************************************/
    /******************************************静态方法*********************************************/
    /**********************************************************************************************/
    /**
     * 获取本应用的启动Activity
     * <br><font color = red>可能为空</font>
     */
    public String getStartActivity() {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> resolveinfoList = application.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            return resolveinfo.activityInfo.name;
        }
        return null;
    }

    /**
     * 获取当前应用的活动列表
     */
    public ActivityInfo[] getActivityInfoList() {
        if (activityInfos == null) {
            //TODO:空指针处理
        }
        return this.activityInfos;
    }
}
