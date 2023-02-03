package candyenk.android.utils;

import candyenk.android.shell.CH;
import candyenk.android.shell.RootShell;

import java.io.File;

/**
 * Android Root工具
 */
public class URoot {
    /**
     * 为当前应用获取Root
     * 会卡住当前线程overtime秒
     * 安装Root管理器将拉起授权弹窗然后返回false
     * 未安装Root管理器或权限拒绝Root授权将直接返回false
     * 应用已被授权Root权限将返回true
     *
     * @param overtime 超时时间(秒)
     *                 超过指定时间未响应将自动返回false
     *                 范围[10~60]
     */
    public static boolean getRoot(int overtime) {
        overtime = Math.max(10, Math.min(60, overtime)) * 1500;
        return new String(CH.exec(new RootShell().setOverTime(overtime), "whoami")).trim().equals("root");
    }

    /**
     * 为当前应用获取Root
     * 默认超时10秒
     */
    public static boolean getRoot() {
        return getRoot(10);
    }


    /**
     * 检查当前设备有没有su文件
     * 局限性很大,system未挂载将无法检查
     */
    public static boolean checkSU() {
        File file = new File("/system/bin/su");
        if (file.exists() && file.canExecute()) return true;
        file = new File("/system/xbin/su");
        return file.exists() && file.canExecute();
    }
}
