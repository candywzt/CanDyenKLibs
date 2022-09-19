package candyenk.android.shell;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import candyenk.android.tools.L;

public class UserShell implements Shell {
    private static final String TAG = UserShell.class.getName();
    private static final byte[] exit = {101, 120, 105, 116, 10};
    private static final int MAX_OVER = 360000;

    private final Handler handler;
    private boolean overSign;
    private long[] overtime = {5000, 0};
    private Thread recycler;
    private Process process;
    private OutputStream out;
    private BufferedReader in;
    private BufferedReader error;

    public UserShell(Handler handler) {
        this.handler = handler;
    }

    public UserShell(ShellCallBack callBack) {
        this(new Handler(Looper.myLooper(), Shell.createHandlerCallback(callBack)));
    }

    @Override
    public boolean ready() {
        try {
            process = Runtime.getRuntime().exec("sh");
            out = new DataOutputStream(process.getOutputStream());
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            overSign = false;
            Log.e(TAG, hashCode() + "Shell进程创建成功");
            if (handler != null && overtime[0] != -1) {
                //TODO:判断handler内是否有消息处理器
                Log.e(TAG, hashCode() + "启动回调接收器");
                outReader();
                errorReader();
            }
            return true;
        } catch (Exception e) {
            L.e(TAG, hashCode() + "Shell进程创建失败:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean writeCmd(String cmd) {
        if (process == null) ready();
        try {
            byte[] bytes = cmd.getBytes();
            out.write(bytes);
            if (bytes[bytes.length - 1] != 10) out.write(10);
            if (overtime[0] == -1) {//指令型命令,自动退出,不予回调
                out.write(exit);
                close();
            } else if (overtime[0] > 0) {//超时命令,自动退出,资源回收
                out.write(exit);
                recycler();
            }
            out.flush();
        } catch (Exception e) {
            L.e(TAG, hashCode() + "字节流写入失败:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void setOverTime(int time) {
        if (time < 0) time = -1;
        else if (time > MAX_OVER) time = MAX_OVER;
        this.overtime[0] = time;
    }

    @Override
    public String getShellPermissions() {
        return "shell";
    }

    @Override
    public void close() {
        L.e(TAG, hashCode() + "尝试关闭Shell进程");
        if (process != null) {
            process.destroy();
            process = null;
        }
        overSign = true;//打开已回收标记
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (error != null) {
                error.close();
                error = null;
            }
            if (recycler != null) {
                recycler = null;
            }
        } catch (Exception e) {
            L.e(TAG, hashCode() + "Shell关闭失败:" + e.getMessage());
            e.printStackTrace();
        }
        L.e(TAG, hashCode() + "Shell会话已关闭");
    }


    /*** 读取输出流 ***/
    private void outReader() {
        new Thread(() -> {
            try {
                StringBuilder sb = new StringBuilder();
                Log.e(TAG, "输出流已启动");
                while (!overSign && in != null) {
                    String line = in.readLine();
                    if (line != null) {
                        sb.append(line).append('\n');
                    } else if (sb.length() != 0) {
                        handler.sendMessage(handler.obtainMessage(SH_SUCCESS, sb.toString()));
                        close();
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                if (!overSign) {
                    L.e(TAG, hashCode() + "输出流读取异常:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*** 读取错误流 ***/
    private void errorReader() {
        new Thread(() -> {
            try {
                StringBuilder sb = new StringBuilder();
                Log.e(TAG, "错误流已启动");
                while (!overSign && error != null) {
                    String line = error.readLine();
                    if (line != null) {
                        sb.append(line).append('\n');
                    } else if (sb.length() != 0) {
                        handler.sendMessage(handler.obtainMessage(SH_FAILED, sb.toString()));
                        close();
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                if (!overSign) {
                    L.e(TAG, hashCode() + "错误流读取异常:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*** 资源回收器 ***/
    /*
    为什么这么麻烦?
    因为我还抱有着能执行一次读取一次不需要重复创建进程的幻想
    即便重新创建进程只需要几毫秒
    既然这样
    资源回收器就要做到刷新超时时间
     */
    private void recycler() {
        L.e(TAG, hashCode() + "准备回收器");
        long nowTime = System.currentTimeMillis();
        overtime[1] = nowTime + overtime[0];//刷新目标时间
        L.e(TAG, hashCode() + "当前时间:" + nowTime);
        L.e(TAG, hashCode() + "超时时间:" + overtime[0]);
        L.e(TAG, hashCode() + "目标时间:" + overtime[1]);
        if (recycler != null) return;//回收器正在运行,仅刷新目标时间
        L.e(TAG, hashCode() + "启动回收器");
        recycler = new Thread(() -> {
            try {
                do {
                    L.e(TAG, hashCode() + "开始休眠,时长:" + overtime[0]);
                    Thread.sleep(overtime[0]);//休眠
                    if (overtime[0] <= 0 || overSign) {
                        //当前条件不足以运行回收器,或已经被回收,则终止回收器
                        L.e(TAG, hashCode() + "终止回收器");
                        return;
                    }
                    L.e(TAG, hashCode() + "休眠结束");
                } while (System.currentTimeMillis() < overtime[1]);//若休眠期间刷新
                if (handler != null && !overSign) {
                    //达到超时界限,发送超时消息
                    L.e(TAG, hashCode() + "发送超时消息");
                    handler.sendMessage(handler.obtainMessage(SH_OVERTIME, ""));//发送超时消息
                }
                close();//回收资源
                L.e(TAG, hashCode() + "资源回收成功");
            } catch (Exception e) {
                L.e(TAG, hashCode() + "资源回收器工作异常,请手动回收" + e.getMessage());
                e.printStackTrace();
            }
        });
        recycler.start();
    }
}
