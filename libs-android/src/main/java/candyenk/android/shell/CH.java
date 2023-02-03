package candyenk.android.shell;

import androidx.annotation.NonNull;
import candyenk.android.tools.TH;

/**
 * 命令助手
 */
public class CH {
    private static final String TAG = CH.class.getSimpleName();
    private final TH<byte[]> th;
    private final Shell sh;


    /**
     * 创建一个持续读写的命令助手
     * Shell直接new个空的
     */
    public CH(Shell sh) {
        this.th = new TH<>(Shell.OT_DEFAULT, Shell.exit);
        this.sh = sh.setCallBack(th::set);
        if (sh.isReady()) sh.close();
        sh.ready();
    }

    /**
     * 命令执行,并读取本次返回值
     * 别用来执行无返回命令,不会会卡到超时
     */
    public byte[] exec(String cmd) {
        th.reset();
        sh.writeCmd(cmd);
        return th.get();
    }

    /**
     * 指令执行,不管执行
     * 返回执行结果
     */
    public boolean execOnly(String cmd) {
        return sh.writeOnly(cmd);
    }


    /**
     * 指令执行,并读取返回值
     * 执行完毕自动关闭Shell
     * Shell直接new个空的
     */
    public static byte[] exec(@NonNull Shell sh, @NonNull String cmd) {
        TH<byte[]> th = new TH<>((int) sh.getOverTime(), new byte[0]).reset();
        if (sh.isReady()) sh.close();
        if (!sh.ready()) return new byte[0];
        sh.setCallBack((code, msg) -> {
            th.set(code, msg);
            sh.close();
        }).writeCmd(cmd);
        return th.get();
    }

    /**
     * 指令执行,不读取返回值
     * 执行完毕自动关闭Shell
     * Shell直接new个空的即可
     */
    public static boolean execOnly(Shell sh, String cmd) {
        if (sh.isReady()) sh.close();
        if (!sh.ready()) return false;
        new Thread(() -> {
            Shell.sleep(Shell.OT_DEFAULT);
            sh.close();
        }).start();
        return sh.setOverTime(-1).writeOnly(cmd);
    }
}
