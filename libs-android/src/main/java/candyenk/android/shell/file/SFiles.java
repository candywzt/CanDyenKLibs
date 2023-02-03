package candyenk.android.shell.file;

import candyenk.android.shell.*;
import candyenk.android.tools.TH;
import com.android.internal.org.bouncycastle.util.Arrays;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * ShellFile工具类
 * SFile的文件操作全部由该类完成
 */
public class SFiles {
    static Shell shell;//文件操作的Shell
    static final TH<byte[]> th = new TH<>(200, Shell.exit);
    static final ShellCallBack cb = th::set;

    static void initShell(boolean isShizuku) {
        if (SFiles.shell != null) SFiles.shell.close();
        SFiles.shell = isShizuku ? new ShizukuShell().setCallBack(cb) : new RootShell().setCallBack(cb);
        shell.setOverTime(-5000);//单条命令超时5秒
        shell.ready();
    }

    /**
     * 测试专用
     */
    public static String cmd(String cmd) {
        return new String(run(cmd));
    }

    /*** 写入一条命令并等待返回值,卡线程 ***/
    private static byte[] run(String cmd) {
        runOnly(cmd);
        return th.reset().get();
    }

    /*** 写入一条无需返回的命令,不会卡线程 ***/
    private static void runOnly(String cmd) {
        shell.writeCmd(cmd);
    }

    /*** 获取指定路径文件权限int组 ***/
    static int[] getPermissions(String str) {
        return null;
    }

    /*** 货值之高ing路径文件自文件路径组 ***/
    static String[] getList() {
        return null;
    }
    /*** ***/
}

