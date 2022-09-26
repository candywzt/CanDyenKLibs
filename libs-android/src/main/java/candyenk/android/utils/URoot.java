package candyenk.android.utils;

import java.io.File;

import candyenk.android.shell.LiteShell;

/**
 * Android Root命令工具
 * 不是很好使
 */
public class URoot {
    /**
     * 为当前应用获取Root
     * 可能会弹窗说未响应,扯淡
     * 授权中会卡线程
     */
    public static boolean getRoot() {
        String result = LiteShell.su().execResult("whoami").result();
        return (result != null && result.contains("root"));
    }

    /**
     * 检查当前设备有没有su文件
     */
    public static boolean checkSU() {
        File f1 = new File("/system/bin/su");
        File f2 = new File("/system/xbin/su");
        if (f1.exists()) {
            String s = LiteShell.sh().execResult("ls -l " + f1.getAbsolutePath()).result();
            char c = s.charAt(3);
            return c == 'x' || c == 's';
        } else if (f2.exists()) {
            String s = LiteShell.sh().execResult("ls -l " + f2.getAbsolutePath()).result();
            char c = s.charAt(3);
            return c == 'x' || c == 's';
        } else {
            return false;
        }
    }

    /**
     * 执行单条Root命令
     *
     * @param cmd root命令
     * @return
     */
    public static boolean execRoot(String cmd) {
        return LiteShell.su().exec(cmd).isok();
    }

    /**
     * 执行Root命令集
     *
     * @param cmd 命令集
     * @return
     */
    public static boolean execRootCmd(String... cmd) {
        return LiteShell.su().exec(cmd).isok();
    }

}
