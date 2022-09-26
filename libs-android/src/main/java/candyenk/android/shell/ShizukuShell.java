package candyenk.android.shell;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import java.io.InputStream;

import candyenk.android.tools.L;
import rikka.shizuku.Shizuku;

/**
 * ShizukuShell实现
 * 默认权限为root(如果有的话)
 * 需拥有Shizuku权限
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ShizukuShell extends UserShell {

    public ShizukuShell(boolean useADB, Handler handler) {
        super(handler);
        TAG = ShizukuShell.class.getSimpleName();
        if (useADB && Shizuku.getUid() == SP_ROOT) startCmd[0] = "/data/adb/libchid.so 2000";
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
        if (!overSign) return true;
        try {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_DENIED)
                throw new SecurityException("Shizuku未授权");
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

    @Override
    public boolean writeCmd(String cmd) {
        if (process == null && !ready()) return false;
        if (cmd.contains("su\n") || cmd.contains("\nsu")) return false;//禁止提权(即便能真的)
        try {
            byte[] bytes = cmd.getBytes();
            in.write(bytes);
            if (bytes[bytes.length - 1] != 10) in.write(10);
            if (overtime[0] == -1) {//指令型命令,自动退出,不予回调
                in.write(exit);
                close();
            } else {//超时命令,自动退出,资源回收
                in.write(exit);
                recycler();
            }
            in.flush();
        } catch (Exception e) {
            L.e(TAG, "命令写入失败(" + hashCode() + "):" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 不能设置为0
     * 无法交替读写
     */
    @Override
    public void setOverTime(int time) {
        if (time <= 0) time = -1;
        else if (time > OVER_MAX) time = OVER_MAX;
        this.overtime[0] = time;
    }

    /**
     * 获取当前Shell权限
     *
     * @return
     */
    @Override
    public int getShellPermissions() {
        return startCmd[0].equals("sh") && Shizuku.getUid() == SP_ROOT ? SP_ROOT : SP_ADB;
    }

    @Override
    public void close() {
        super.close();
        try {
            if (out != null) out.close();
            if (err != null) err.close();
        } catch (Exception e) {
        }
        out = null;
        err = null;
    }

    @Override
    protected void outReader() {
        new Thread(() -> {
            try {
                while (!overSign && out != null) {
                    int size = Math.toIntExact(out.available());
                    if (size == 0) continue;
                    Thread.sleep(size);
                    byte[] bytes = new byte[out.available()];
                    out.read(bytes);
                    handler.sendMessage(handler.obtainMessage(Shell.CB_SUCCESS, bytes));
                    if (overtime[0] > 0) close();
                }
            } catch (Exception e) {
                if (!overSign) {
                    L.e(TAG, "输出流读取异常(" + hashCode() + "):" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void errorReader() {
        new Thread(() -> {
            try {
                while (!overSign && err != null) {
                    int size = Math.toIntExact(err.available());
                    if (size == 0) continue;
                    Thread.sleep(size);
                    byte[] bytes = new byte[err.available()];
                    err.read(bytes);
                    handler.sendMessage(handler.obtainMessage(Shell.CB_FAILED, bytes));
                    if (overtime[0] > 0) close();
                }
            } catch (Exception e) {
                if (!overSign) {
                    L.e(TAG, "输出流读取异常(" + hashCode() + "):" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
