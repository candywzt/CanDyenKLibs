package candyenk.android.handle;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import candyenk.android.tools.L;


/**
 * Shell处理器
 * 用于执行shell命令
 * 单实例含3个线程
 * 线程自动回收
 */
public class ShellHandler {
    private static final String TAG = "SH";
    private static final int SH_SUCCESS = 1;
    private static final int SH_FAILED = -1;
    private static final int SH_OVERTIME = 0;
    private static ShellHandler only;
    private final Handler handler;
    private boolean isOver;
    private long overtime = 5000;
    private Thread recycler;
    private Process process;
    private OutputStream out;
    private BufferedReader in;
    private BufferedReader error;

    /*********************************************************************************************/
    /******************************************公共静态方法*****************************************/
    /*********************************************************************************************/
    /**
     * 创建普通Shell
     */
    public static ShellHandler createShell(Handler handler) {
        ShellHandler sh = new ShellHandler(handler);
        return sh.createProvess("sh") ? sh : null;
    }

    /**
     * 创建普通Shell
     * msg.what = -1 为错误信息
     * msg.what = 1 为正常输出信息
     * msg.what = 0 为超时,输出信息为空字符串
     * msg.obj 为String输出信息
     */
    public static ShellHandler createShell(CallBack callBack) {
        ShellHandler sh = new ShellHandler(new Handler(Looper.myLooper(), createCallBack(callBack)));
        return sh.createProvess("sh") ? sh : null;
    }

    /**
     * 创建RootShell
     */
    public static ShellHandler createRootShell(Handler handler) {
        ShellHandler sh = new ShellHandler(handler);
        return sh.createProvess("su") ? sh : null;
    }

    /**
     * 创建RootShell
     * msg.what=-1为错误信息
     * msg.what=1为输出信息
     * msg.obj为String输出信息
     */
    public static ShellHandler createRootShell(CallBack callback) {
        ShellHandler sh = new ShellHandler(new Handler(Looper.myLooper(), createCallBack(callback)));
        return sh.createProvess("su") ? sh : null;
    }

    /**
     * 设置同时只能有一个Shell正在执行
     * 新建Shell将强制关闭旧的Shell
     *
     * @param isOnly
     */
    public static void setOnly(boolean isOnly) {
        if (isOnly) {
            only = new ShellHandler(null);
        } else {
            only = null;
        }
    }
    /*********************************************************************************************/
    /*********************************************构造方法*****************************************/
    /*********************************************************************************************/
    private ShellHandler(Handler handler) {
        if (only != null) {
            only.close();
            only = this;
        }
        this.handler = handler;
        L.e(TAG, "Shell会话已创建:" + this);
    }
    /*********************************************************************************************/
    /******************************************公共方法*********************************************/
    /*********************************************************************************************/
    /**
     * 设置超时时间(毫秒)
     * 默认5秒
     * 设置为0则永不超时
     * 最大值3600秒
     * 超过设置时间无响应后自动调用超时处理器
     * 不要尝试设置接近业务处理时间的超时,回收器会强制打断正在读取的流
     * TODO:测试不充分
     **/
    public void setOverTime(long time) {
        if (time < 0 || time > 3600000) return;
        this.overtime = time;
    }

    /**
     * 写入一条命令
     * 写入多行请使用writeCmds
     * 占用线程
     *
     * @param cmd 命令
     * @return 返回false写入失败
     */
    public boolean writeCmd(String cmd) {
        if (cmd.isEmpty()) return false;
        int index = cmd.indexOf(10);
        cmd = index == -1 ? cmd : cmd.substring(0, index);
        L.e(TAG, "写入命令:" + cmd);
        return writeBytes(cmd.getBytes(), true);
    }

    /**
     * 手动关闭会话及流线程
     * 防止流线程永不停止然后卡GC
     * 如果执行无返回的指令时添加了消息处理器就会导致无限GC,此时可调用该方法关闭
     */
    public void close() {
        if (process != null) {
            process.destroy();
        }
        isOver = true;//关闭流读取线程
        overtime = 0;//关闭资源回收器
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (Exception e) {
            L.e(TAG, "Shell关闭失败:" + e.getMessage());
            e.printStackTrace();
        }

        L.e(TAG, "Shell会话已关闭:" + this);
    }

    /*********************************************************************************************/
    /******************************************私有方法*********************************************/
    /*********************************************************************************************/
    /***  创建Shell进程 ***/
    private boolean createProvess(String cmd) {
        try {
            process = Runtime.getRuntime().exec(cmd);
            out = new DataOutputStream(process.getOutputStream());
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            if (handler != null) {
                //TODO:判断handler内是否有消息处理器
                outReader();
                errorReader();
            }
            return true;
        } catch (Exception e) {
            L.e(TAG, "Shell进程创建失败:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /*** 写入字节流 ***/
    /*** 参数isEcit是否自动退出 ***/
    private boolean writeBytes(byte[] bytes, boolean isExit) {
        try {
            out.write(bytes);
            out.write(10);
            if (isExit) {
                out.write(new byte[]{101, 120, 105, 116, 10});
            }
            out.flush();
            recycler();//启动资源回收器
            //process.waitFor();
        } catch (Exception e) {
            L.e(TAG, "字节流写入失败:" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*** 读取输出流 ***/
    private void outReader() {
        new Thread(() -> {
            try {
                StringBuilder sb = new StringBuilder();
                while (!isOver && in != null) {
                    String line = in.readLine();
                    if (line != null) {
                        sb.append(line).append('\n');
                    } else if (sb.length() != 0) {
                        handler.sendMessage(handler.obtainMessage(SH_SUCCESS, sb.toString()));
                        close();
                    }
                }
            } catch (Exception e) {
                if (!isOver) {
                    L.e(TAG, "输出流读取异常:" + e.getMessage());
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
                while (!isOver && error != null) {
                    String line = error.readLine();
                    if (line != null) {
                        sb.append(line).append('\n');
                    } else if (sb.length() != 0) {
                        handler.sendMessage(handler.obtainMessage(SH_FAILED, sb.toString()));
                        close();
                    }
                }
            } catch (Exception e) {
                if (!isOver) {
                    L.e(TAG, "错误流读取异常:" + e.getMessage());
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
        if (overtime == 0) return;//无超时则不启动回收器
        L.e(TAG, "准备回收器");
        long nowTime = System.currentTimeMillis();
        L.e(TAG, "当前时间:" + nowTime);
        L.e(TAG, "超时时间:" + overtime);
        if (overtime > 0 && overtime < 3600000) {//首次启动回收器或超时被刷新
            overtime = nowTime + overtime;//将超时设置为超时目标
            L.e(TAG, "刷新目标时间:" + overtime);
        }
        if (recycler != null) return;//资源回收器不重复启动
        L.e(TAG, "启动回收器");
        recycler = new Thread(() -> {
            try {
                do {
                    L.e(TAG, "开始休眠:" + (overtime - nowTime));
                    Thread.sleep(overtime - nowTime);//休眠
                    if (overtime == 0) return;//中途设置超时为0则关闭资源回收器
                    L.e(TAG, "当前休眠结束");
                } while (System.currentTimeMillis() < overtime);//若休眠期间刷新
                if (handler != null) {
                    L.e(TAG, "发送超时");
                    handler.sendMessage(handler.obtainMessage(SH_OVERTIME, ""));//发送超时消息
                }
                L.e(TAG, "资源回收成功:" + this);
                close();//回收
            } catch (Exception e) {
                L.e(TAG, "资源回收器工作异常,请手动回收" + e.getMessage());
                e.printStackTrace();
            }
        });
        recycler.start();
    }
    /*********************************************************************************************/
    /******************************************私有静态方法*****************************************/
    /*********************************************************************************************/
    /*** 创建回调 ***/
    private static Handler.Callback createCallBack(CallBack callBack) {
        return msg -> {
            String message = (String) msg.obj;
            if (!callBack.callBack(msg.what, message)) {
                switch (msg.what) {
                    case SH_SUCCESS:
                        callBack.onSuccess(message);
                        break;
                    case SH_FAILED:
                        callBack.onFailed(message);
                        break;
                    case SH_OVERTIME:
                        callBack.onOverTime();
                        break;
                }
            }
            return true;
        };
    }
    /*********************************************************************************************/
    /******************************************接口************************************************/
    /*********************************************************************************************/
    /**
     * 命令执行回调接口
     */
    public interface CallBack {
        /**
         * 第一层消息处理器
         *
         * @param code 返回代码 :
         *             0:超时
         *             -1:错误
         *             1:成功
         * @param msg  返回的消息,超时消息为空字符串
         * @return 返回true则不进行下一步处理(不调用以下三个方法)
         */
        default boolean callBack(int code, String msg) {
            return false;
        }

        /**
         * 成功回调
         */
        default void onSuccess(String msg) {
        }

        /**
         * 失败回调
         */
        default void onFailed(String msg) {
        }

        /**
         * 超时回调
         */
        default void onOverTime() {
        }
    }

}
