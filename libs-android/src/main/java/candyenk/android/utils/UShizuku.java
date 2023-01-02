package candyenk.android.utils;

import rikka.shizuku.Shizuku;
import rikka.sui.Sui;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class UShizuku {
    /**
     * 检查Shizuku权限
     *
     * @return -2:不支持Shizuku;
     * -1:没有权限;
     * 0:拥有权限;
     * 1:拒绝授权;
     */
    public static int checkPermission() {
        if (Shizuku.isPreV11()) return -2;
        try {
            if (Shizuku.checkSelfPermission() == PERMISSION_GRANTED) return 0;
            if (Shizuku.shouldShowRequestPermissionRationale()) return 1;
            return -1;
        } catch (Exception e) {return -2;}
    }

    /**
     * 初始化Sui
     *
     * @param pkgName 包名
     * @return 是否安装SUI
     */
    public static boolean initSui(String pkgName) {
        try {
            return Sui.init(pkgName);
        } catch (Exception e) {
            return false;
        }
    }
}
