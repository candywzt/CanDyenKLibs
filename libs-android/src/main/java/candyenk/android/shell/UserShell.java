package candyenk.android.shell;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import candyenk.android.tools.L;
import candyenk.android.utils.UFile;

/**
 * 用户Shell,以应用的身份执行Shell命令
 * 部分命令取药对应权限
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class UserShell implements Shell {
    private static final String TAG = UserShell.class.getSimpleName();
    protected static final byte[] exit = {101, 120, 105, 116, 10};

    protected final Handler handler;
    protected boolean overSign = true;
    protected long[] overtime = {OVER_DEFAULT, 0};
    protected Thread recycler;
    protected Process process;
    protected OutputStream in;
    protected FileInputStream out;
    protected FileInputStream err;
    protected File outFile;
    protected File errFile;

    public UserShell(Handler handler) {
        this.handler = handler;
        registerShell();
    }

    public UserShell(ShellCallBack callBack) {
        this(new Handler(Looper.myLooper(), Shell.createHandlerCallback(callBack)));
    }

    @Override
    public boolean ready() {
        if (!overSign) return true;
        try {
            outFile = UFile.createTmp(TAG);
            errFile = UFile.createTmp(TAG);
            process = new ProcessBuilder("sh")
                    .redirectOutput(outFile)
                    .redirectError(errFile)
                    .start();
            in = process.getOutputStream();

            L.e(TAG, "Shell进程创建成功(" + hashCode() + ")");
            if (handler != null && overtime[0] != -1) {
                outReader();
                errorReader();
            }
            overSign = false;
            return true;
        } catch (Exception e) {
            L.e(TAG, "Shell进程创建失败(" + hashCode() + "):" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean writeCmd(String cmd) {
        if (process == null && !ready()) return false;
        if (cmd.contains("su\n") || cmd.contains("\nsu")) return false;//禁止提权(会卡死)
        try {
            byte[] bytes = cmd.getBytes();
            in.write(bytes);
            if (bytes[bytes.length - 1] != 10) in.write(10);
            if (overtime[0] == -1) {//指令型命令,自动退出,不予回调
                in.write(exit);
                close();
            } else if (overtime[0] > 0) {//超时命令,资源回收
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

    @Override
    public void setOverTime(int time) {
        if (time < 0) time = -1;
        else if (time > OVER_MAX) time = OVER_MAX;
        this.overtime[0] = time;
    }

    @Override
    public int getShellPermissions() {
        return SP_USER;
    }

    @Override
    public void close() {
        L.e(TAG, "尝试关闭Shell进程(" + hashCode() + ")");
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (err != null) err.close();
            if (recycler != null) recycler = null;
        } catch (Exception e) {
        }
        overSign = true;//打开已回收标记
        if (outFile != null) outFile.delete();
        if (errFile != null) errFile.delete();
        if (process != null) process.destroy();
        process = null;
        in = null;
        out = null;
        err = null;
    }


    /*** 读取Shell输出 ***/
    protected void outReader() {
        new Thread(() -> {
            try {
                out = new FileInputStream(outFile);
                FileChannel fc = out.getChannel();
                while (!overSign && out != null) {
                    int size = Math.toIntExact(fc.size() - fc.position());
                    if (size == 0) continue;
                    Thread.sleep(size);
                    byte[] bytes = new byte[Math.toIntExact(fc.size() - fc.position())];
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

    /*** 读取Shell错误 ***/
    protected void errorReader() {
        new Thread(() -> {
            try {
                err = new FileInputStream(errFile);
                FileChannel fc = err.getChannel();
                while (!overSign && err != null) {
                    int size = Math.toIntExact(fc.size() - fc.position());
                    if (size == 0) continue;
                    Thread.sleep(size);
                    byte[] bytes = new byte[Math.toIntExact(fc.size() - fc.position())];
                    err.read(bytes);
                    handler.sendMessage(handler.obtainMessage(Shell.CB_FAILED, bytes));
                    if (overtime[0] > 0) close();
                }
            } catch (Exception e) {
                if (!overSign) {
                    L.e(TAG, "错误流读取异常(" + hashCode() + "):" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*** 资源回收器 ***/
    protected void recycler() {
        long nowTime = System.currentTimeMillis();
        overtime[1] = nowTime + overtime[0];//刷新目标时间
        if (recycler != null) return;//回收器正在运行,仅刷新目标时间
        L.e(TAG, "启动回收器(" + hashCode() + ")");
        recycler = new Thread(() -> {
            try {
                do {
                    Thread.sleep(overtime[0]);//休眠
                    if (overtime[0] <= 0 || overSign) {
                        //当前条件不足以运行回收器,或已经被回收,则终止回收器
                        return;
                    }
                } while (System.currentTimeMillis() < overtime[1]);//若休眠期间刷新
                if (handler != null && !overSign) {
                    //达到超时界限,发送超时消息
                    handler.sendMessage(handler.obtainMessage(CB_OVERTIME, new byte[0]));//发送超时消息
                }
                close();//回收资源
            } catch (Exception e) {
                L.e(TAG, "资源回收器工作异常,请手动回收(" + hashCode() + "):" + e.getMessage());
                e.printStackTrace();
            }
        });
        recycler.start();
    }
}
