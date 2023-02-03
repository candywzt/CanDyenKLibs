package candyenk.android.shell;

import android.content.pm.PackageManager;
import candyenk.android.tools.L;
import rikka.shizuku.Shizuku;

import java.io.ByteArrayOutputStream;

/**
 * ShizukuShell实现
 * 默认权限为root(如果有的话)可通过构造函数切换
 * 需拥有Shizuku权限
 */
//@RequiresApi(api = Build.VERSION_CODES.O)
public class ShizukuShell extends UserShell {
    public ShizukuShell() {
        super();
    }

    public ShizukuShell(ShellCallBack cb) {
        super(cb);
    }

    @Override
    public boolean ready() {
        if (sign.isOk()) return true;
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_DENIED) {
            L.e(TAG, "Shizuku未授权");
            return false;
        }
        try {
            process = Shizuku.newProcess(new String[]{startCmd()}, null, null);
            in = process.getOutputStream();
            out = process.getInputStream();
            err = process.getErrorStream();
            sign.ready(true).free(true);//初始化结束,标记就绪,空闲
            outReader();
            errorReader();
            timeOutTimer();
            return true;
        } catch (Exception e) {
            return L.e(TAG, e, "Shizuku Shell进程创建失败(" + hashCode() + ")");
        }
    }

    @Override
    public ShizukuShell setOverTime(int time) {
        super.setOverTime(time);
        return this;
    }

    @Override
    public ShizukuShell setCallBack(ShellCallBack cb) {
        super.setCallBack(cb);
        return this;
    }
}
