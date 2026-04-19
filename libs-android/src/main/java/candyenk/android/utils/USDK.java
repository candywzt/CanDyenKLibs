package candyenk.android.utils;

import android.os.Build;

/**
 * Android SDK 工具类
 */
public class USDK {
    
    /**
     * Android 11,API30
     */
    public static boolean R() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R;}
    
    /**
     * Android 12,API31
     */
    public static boolean S() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;}
    
    /**
     * Android 13,API33
     */
    public static boolean T() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;}
    
    /**
     * Android 14,API34
     */
    public static boolean A34() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE;}
    
    /**
     * Android 15,API35
     */
    public static boolean A35() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM;}
    
    /**
     * Android 16,API36
     */
    public static boolean A36() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA;}
    
}
