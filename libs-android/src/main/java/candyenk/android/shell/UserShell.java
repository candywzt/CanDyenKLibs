package candyenk.android.shell;

import android.os.Handler;
import android.os.Looper;
import candyenk.android.tools.L;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用户Shell,以应用的身份执行Shell命令
 * 部分命令需要对应权限
 * 不要尝试读取大文件,会被打断
 */
public class UserShell implements Shell {
    protected String TAG = UserShell.class.getSimpleName();
    protected final byte[] exit = {101, 120, 105, 116, 10};
    protected final String[] startCmd = {"sh"};
    protected final Handler handler;
    protected boolean overSign = true;
    protected long[] overtime = {OVER_DEFAULT, 0};
    protected Thread recycler;
    protected Process process;
    protected OutputStream in;
    protected InputStream out;
    protected InputStream err;

    public UserShell(Handler handler) {
        this.handler = handler;
        registerShell();
    }

    public UserShell(ShellCallBack callBack) {
        this(new Handler(Looper.myLooper(), Shell.createHandlerCallback(callBack)));
    }

    @Override
    public boolean ready() {
        if (!overSign) close();
        try {
            process = new ProcessBuilder(startCmd).start();
            in = process.getOutputStream();
            out = process.getInputStream();
            err = process.getErrorStream();
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
        if (overSign) {
            L.e(TAG, "未初始化或进程已回收(" + hashCode() + ")");
            return false;
        }
        allowRun(cmd);
        try {
            byte[] bytes = cmd.getBytes();
            in.write(bytes);
            if (bytes[bytes.length - 1] != 10) in.write(10);
            if (overtime[0] == -1) {//指令型命令,自动退出
                in.write(exit);
            } else if (overtime[0] > 0) {//超时命令,资源回收
                recycler();
            }
            in.flush();
            return true;
        } catch (Exception e) {
            L.e(TAG, "命令写入失败(" + hashCode() + "):" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setOverTime(int time) {
        if (time < 0) time = -1;
        else if (time > OVER_MAX) time = OVER_MAX;
        this.overtime[0] = time;
    }

    @Override
    public void close() {
        L.e(TAG, "关闭Shell进程(" + hashCode() + ")");
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (err != null) err.close();
            if (recycler != null) recycler = null;
        } catch (Exception e) {
        }
        overSign = true;//打开已回收标记
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
                while (!overSign && out != null) {
                    if (out.available() == 0) continue;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (out.available() > 0) {
                        byte[] bytes = new byte[out.available()];
                        int size = out.read(bytes);
                        baos.write(bytes, 0, size);
                    }
                    handler.sendMessage(handler.obtainMessage(Shell.CB_SUCCESS, baos.toByteArray()));
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
                while (!overSign && err != null) {
                    if (err.available() == 0) continue;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (err.available() > 0) {
                        byte[] bytes = new byte[err.available()];
                        int size = err.read(bytes);
                        baos.write(bytes, 0, size);
                    }
                    handler.sendMessage(handler.obtainMessage(Shell.CB_FAILED, baos.toByteArray()));
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

    /*** 命令检查器 ***/
    /*** 在这里面抛异常就行 ***/
    protected void allowRun(String cmd) {
        if (cmd.contains(" su\n") || cmd.contains("\nsu")) {
            throw new RuntimeException("命令不被允许允许");
        }
    }
}
