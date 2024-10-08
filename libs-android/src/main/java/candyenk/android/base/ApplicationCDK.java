package candyenk.android.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import candyenk.android.tools.CH;
import candyenk.android.tools.L;
import candyenk.android.utils.UApp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代码初始化LitePal
 * Operator.initialize(this);
 * LitePalDB ldb = new LitePalDB("test", 1);
 * ldb.addClassName(DB.class.getName());
 * Operator.use(ldb);
 */
public class ApplicationCDK extends Application {
    @SuppressLint("StaticFieldLeak")
    protected String TAG;
    private List<Activity> activityList = Collections.EMPTY_LIST;
    private static ApplicationCDK explame;//
    public static final Gson gson = new Gson();//默认GSON

    @Override
    public void onCreate() {
        this.TAG = this.getClass().getSimpleName();
        explame = this;
        super.onCreate();
    }

    /**
     * 获取实例
     */
    public static <T extends ApplicationCDK> T app() {
        return app(null);
    }

    public static <T extends ApplicationCDK> T app(Class<T> c) {
        return (T) explame;
    }

    /**
     * 获取栈顶activity
     * 可用来获取全局Context
     */
    public Activity getCurrentActivity() {
        if (activityList.isEmpty()) throw new RuntimeException("Activity管理器工作异常");
        return activityList.get(0);
    }

    /**
     * 获取当前状态的Activity栈
     *
     * @return
     */
    public List<Activity> getActivityList() {
        if (activityList == null || activityList.isEmpty()) throw new RuntimeException("Activity管理器工作异常");
        return Collections.unmodifiableList(activityList);
    }

    /**
     * 重启应用程序
     */
    public static void restart(Activity activity) {
        Intent intent = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (intent.getComponent() != null) {
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        }
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * 关闭应用程序
     */
    public void close(Activity activity) {
        activity.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * 结束所有Activity并退出应用
     */
    public void finish() {
        for (int i = activityList.size() - 1; i >= 0; i--) {
            activityList.get(i).finish();
            L.e(ApplicationCDK.class.getSimpleName(), "活动(" + activityList.get(i) + ")被销毁");
        }
        activityList.clear();
    }

    /**
     * 结束某个Activity
     */
    public void finish(Class<?> activityClass) {
        for (Activity a : activityList) {
            if (a.getClass().equals(activityClass)) {
                a.finish();
                Log.e(ApplicationCDK.class.getSimpleName(), "活动(" + a + ")被销毁");
            }
        }
    }

    public void finish(String activityName) {
        for (Activity a : activityList) {
            if (a.getClass().toString().equals(activityName)) {
                a.finish();
                Log.e(ApplicationCDK.class.getSimpleName(), "活动(" + a + ")被销毁");
            }
        }
    }
    /**********************************************************************************************/
    /*****************************************工具初始化**********************************************/
    /**********************************************************************************************/
    /**
     * Crash异常捕捉初始化
     * 自动初始化Activity管理器
     */
    protected void InitCrash() {
        CH.INSTANCE().init(this);
        InitActivityManager();
    }

    /**
     * App工具初始化
     */
    protected void InitApp() {
        UApp.init(this);
    }

    /**
     * Activity管理器初始化
     */
    protected void InitActivityManager() {
        activityList = new ArrayList<>();
        //活动管理器
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override//活动创建
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.remove(activity);
                activityList.add(0, activity);
                Log.e(TAG, "活动启动" + activityList);
            }

            @Override//活动开始
            public void onActivityStarted(Activity activity) {
            }

            @Override//活动获得焦点
            public void onActivityResumed(Activity activity) {
                activityList.remove(activity);
                activityList.add(0, activity);
                Log.e(TAG, "活动焦点" + activityList.toString());
            }

            @Override//活动暂停
            public void onActivityPaused(Activity activity) {

            }

            @Override//活动停止
            public void onActivityStopped(Activity activity) {

            }

            @Override//保存实例状态
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override//活动销毁
            public void onActivityDestroyed(Activity activity) {
                activityList.remove(activity);
                Log.e(TAG, "活动销毁" + activityList.toString());
            }
        });
    }
}
