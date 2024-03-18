package candyenk.android.tools;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;

import candyenk.android.asbc.ApplicationCDK;
import candyenk.android.asbc.ActivityCrash;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告<br>
 * 使用本工具必须继承CDKApplication类<br>
 * 异常展示Activity带上标签 android:process=":error_activity"<br>
 * 传递Intent附带数据<br>
 * 错误异常 crashReport(Throwable)<br>
 * 错误异常产生的Activity CurrentActivity(Class<?>)<br>
 */
public class CH implements UncaughtExceptionHandler {
    // CrashHandler实例
    private static CH Instance;
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Application mApplication;
    //崩溃日志跳转activity
    private Class<?> crashActivity = ActivityCrash.class;


    //获取CrashHandler实例 ,单例模式
    public static CH INSTANCE() {
        if (Instance == null)
            Instance = new CH();
        return Instance;
    }

    /**
     * 初始化，在Application调用
     */
    public void init(Application application, Class<?> crashActivity) {
        this.crashActivity = crashActivity;
        init(application);
    }

    public void init(Application application) {
        mApplication = application;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        //获取处理结果
        boolean b = handleException(ex);
        // 如果自定义的没有处理则让系统默认的异常处理器来处理
        if (!b && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }
    /**********************************************************************************************/
    /**********************************************************************************************/
    /**********************************************************************************************/
    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null || mApplication == null)
            return false;
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Intent intent = new Intent(mApplication, crashActivity);
                intent.putExtra("crashReport", ex);
                intent.putExtra("CurrentActivity", ApplicationCDK.app().getCurrentActivity().getClass());
                ApplicationCDK.app().getCurrentActivity().startActivity(intent);
                Looper.loop();
            }
        }.start();
        return true;
    }
}