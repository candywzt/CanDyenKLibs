package candyenk.android.shell;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import candyenk.android.tools.L;
import rikka.shizuku.Shizuku;

/**
 * ShizukuShell实现
 * 默认权限为root(如果有的话)可通过构造函数切换
 * 需拥有Shizuku权限
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ShizukuShell extends UserShell {

    public ShizukuShell(boolean useADB, Handler handler) {
        super(handler);
        TAG = ShizukuShell.class.getSimpleName();
        if (useADB && Shizuku.getUid() == 0) startCmd[0] = "/data/adb/libchid.so 2000";
    }

    public ShizukuShell(boolean useADB, ShellCallBack callBack) {
        this(useADB, new Handler(Looper.myLooper(), Shell.createHandlerCallback(callBack)));
    }

    public ShizukuShell(Handler handler) {
        this(false, handler);
    }

    public ShizukuShell(ShellCallBack callBack) {
        this(false, callBack);
    }

    @Override
    public boolean ready() {
        if (!overSign) close();
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_DENIED) {
            L.e(TAG, "Shizuku未授权");
            return false;
        }
        try {
            process = Shizuku.newProcess(startCmd, null, null);
            in = process.getOutputStream();
            out = process.getInputStream();
            err = process.getErrorStream();
            if (handler != null && overtime[0] > 0) {
                outReader();
                errorReader();
            }
            overSign = false;
            return true;
        } catch (Exception e) {
            L.e(TAG, "Shizuku Shell进程创建失败(" + hashCode() + "):" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
