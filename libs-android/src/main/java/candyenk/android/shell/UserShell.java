package candyenk.android.shell;

import candyenk.android.tools.L;
import candyenk.java.io.IO;
import candyenk.java.utils.UTime;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * 用户Shell,以应用的身份执行Shell命令
 * 部分命令需要对应权限
 * 不要尝试读取大文件,会被打断
 */
public class UserShell implements Shell {
    protected String TAG;
    protected final Sign sign = new Sign();//标记
    protected final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    protected ShellCallBack callback;//回调
    protected Process process;//进程
    protected OutputStream in;//命令输出流
    protected InputStream out;//回执输入流
    protected InputStream err;//异常输入流

    public UserShell() {
        registerShell();
        this.TAG = this.getClass().getSimpleName();
    }

    public UserShell(ShellCallBack cb) {
        this();
        this.setCallBack(cb);
    }


    @Override
    public boolean ready() {
        if (sign.isOk()) return true;
        try {
            process = new ProcessBuilder(startCmd()).start();
            in = process.getOutputStream();
            out = process.getInputStream();
            err = process.getErrorStream();
            //L.e(TAG, "Shell进程创建成功(" + hashCode() + ")");
            sign.ready(true).free(true);//初始化结束,标记就绪,空闲
            outReader();
            errorReader();
            timeOutTimer();
            return true;
        } catch (Exception e) {
            return L.e(TAG, e, "Shell进程创建失败(" + hashCode() + ")");
        }
    }

    @Override
    public boolean isReady() {
        return sign.isOk();
    }

    @Override
    public UserShell setCallBack(ShellCallBack cb) {
        if (!sign.isFree()) return this;
        this.callback = cb;
        return this;
    }

    @Override
    public boolean writeCmd(String cmd) {
        return write(cmd, false);
    }

    @Override
    public boolean writeOnly(String cmd) {
        return write(cmd, true);
    }

    @Override
    public UserShell setOverTime(int time) {
        if (sign.isOk()) return this;//已就绪,不允许修改
        if (time < OT_MAX * -1) time = OT_MAX * -1;
        else if (time > OT_MAX) time = OT_MAX;
        this.sign.over(time);
        return this;
    }

    @Override
    public long getOverTime() {
        return sign.over();
    }


    @Override
    public void close() {
        //L.e(TAG, "关闭Shell进程(" + hashCode() + ")");
        sign.ready(false).remove();//标记回收
        baos.reset();
        process = IO.close(process);
        in = IO.close(in);
        out = IO.close(out);
        err = IO.close(err);
    }

    /**
     * 初始命令
     */
    protected String startCmd() {
        return "sh";
    }

    /**
     * 写入命令
     *
     * @param only 是否指令
     */
    protected boolean write(String cmd, boolean only) {
        if (!sign.isOk()) return L.e(TAG, "Shell未初始化或已回收(" + hashCode() + ")");
        else if (!sign.isFree()) return L.e(TAG, "Shell繁忙,请等待(" + hashCode() + ")");
        else if (!only && callback == null) return L.e(TAG, "Shell未设置消息接收器(" + hashCode() + ")");
        allowRun(cmd);//检查抛异常
        try {
            byte[] bytes = cmd.getBytes();
            if (sign.over() > -1 || only) sign.add().free(false); //设置繁忙,添加任务
            // L.e(TAG, "创建任务ID:" + sign.id());
            in.write(bytes);
            if (bytes[bytes.length - 1] != 10) in.write(10);
            in.flush();
            return true;
        } catch (Exception e) {
            return L.e(TAG, e, "命令写入失败(" + hashCode() + ")");
        }
    }

    /**
     * 读取Shell输出
     */
    protected void outReader() {
        if (sign.over() == -1) return;//指令型命令,无需读取返回
        new Thread(() -> {
            // L.e(TAG, "启动输出接收器" + sign.isOk());
            while (sign.isOk()) { //Shell已就绪
                //  L.e(TAG, "输出接收器已就绪");
                while (sign.isFree()) Shell.sleep(1); //等待接收器打开
                int id = sign.id();
                // L.e(TAG, "开始读取输出..." + id);
                while (!sign.isFree()) readAndSend(out, CB_SUCCESS, id);//等待读取发送
            }
        }).start();
    }

    /**
     * 读取Shell错误
     */
    protected void errorReader() {
        if (sign.over() == -1) return;//指令型命令,无需读取返回
        new Thread(() -> {
            while (sign.isOk()) { //Shell已就绪
                while (sign.isFree()) Shell.sleep(1); //等待接收器打开
                int id = sign.id();
                while (!sign.isFree()) readAndSend(err, CB_FAILED, id);//等待读取发送
            }
        }).start();
    }

    /**
     * 超时定时器
     */
    protected void timeOutTimer() {
        if (sign.over() <= 0) return;//无需定时器
        new Thread(() -> {
            while (sign.isOk()) {//Shell已就绪
                while (sign.isFree()) Shell.sleep(1); //等待接收器打开
                int id = sign.id();
                Shell.sleep(sign.overR());
                if (sign.id() == id) {
                    sign.remove(id);
                    // L.e(TAG, "超时任务ID:" + id);
                    send(CB_OVERTIME, new byte[0]);
                }
            }
        }).start();
    }


    /**
     * 发送消息
     */
    protected void send(int code, byte[] msg) {
        // L.e(TAG, "发送消息(" + code + ")");
        if (callback == null) return;
        callback.callBack(code, msg);
    }

    /**
     * 命令检查器
     * 在这里面抛异常就行
     */
    protected void allowRun(String cmd) {
        if (cmd.contains(" su\n") || cmd.contains("\nsu")) {
            throw new RuntimeException("命令不被允许允许");
        }
    }

    /*** 读取流并发送 ***/
    private void readAndSend(InputStream is, int s, int id) {
        try {
            if (is.available() == 0) return;
            //Shell.sleep((long) is.available() << 3);
            while (is.available() > 0) {
                byte[] bytes = new byte[is.available()];
                int size = is.read(bytes);
                baos.write(bytes, 0, size);
            }
            if (sign.id() == id) {
                sign.remove(id);//移除任务
                //L.e(TAG, "发送任务ID:" + id);
                send(s, baos.toByteArray());
            }
            baos.reset();
        } catch (Throwable e) {
            if (sign.isOk()) {
                L.e(TAG, e, "流读取异常(" + hashCode() + ")");
                close();
            }
        }
    }


    /**
     * 标记类
     */
    public static class Sign {
        private final Random r = new Random();
        private int id;//当前任务ID
        private boolean readySign;//就绪标记
        private boolean freeSign = true;//空闲标记
        private long overTime = Shell.OT_DEFAULT;//超时时间
        private long targetTime;//超时目标时间

        /*** 是否准备就绪 ***/
        public boolean isOk() {
            return readySign;
        }

        /*** 设置就绪 ***/
        public synchronized Sign ready(boolean b) {
            //L.e("SIGN", "就绪" + b);
            this.readySign = b;
            return this;
        }


        /*** 是否空闲 ***/
        public boolean isFree() {
            return freeSign;
        }


        /*** 设置空闲 ***/
        public synchronized Sign free(boolean b) {
            this.freeSign = b;
            return this;
        }

        /*** 获取超时时间 ***/
        public long over() {
            return this.overTime;
        }

        /*** 设置超时时间 ***/
        public Sign over(long time) {
            this.overTime = time;
            return this;
        }

        /*** 获取超时剩余时间 ***/
        public long overR() {
            long t = targetTime - UTime.now();
            if (t < 0) t = 0;
            return t;
        }

        /*** 获取当前任务ID ***/
        public int id() {
            if (isFree()) return 0;
            return id;
        }

        /*** 添加任务,先添加再标记繁忙 ***/
        public synchronized Sign add() {
            this.id = r.nextInt(Integer.MAX_VALUE);
            this.targetTime = UTime.now() + Math.abs(overTime);
            return this;
        }

        /*** 移除指定任务并标记空闲 ***/
        public synchronized Sign remove(int id) {
            if (this.id == id) {
                this.id = 0;
                this.freeSign = true;
            }
            return this;
        }

        /*** 移除当前任务并标记空闲 ***/
        public synchronized Sign remove() {
            this.id = 0;
            this.freeSign = true;
            return this;
        }
    }
}
