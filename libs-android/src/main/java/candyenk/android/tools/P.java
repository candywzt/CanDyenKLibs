package candyenk.android.tools;

import android.Manifest;
import android.app.Activity;
import candyenk.java.utils.UArrays;

/**
 * Android 权限帮助类
 * 权限回调需配合CDKActivity实现
 */
public class P {
    /*************************************静态变量**************************************************/
    public static final P USE_MIC = new P(Manifest.permission.RECORD_AUDIO);//录音权限
    public static final P GET_ACCOUNTS = new P(Manifest.permission.GET_ACCOUNTS);//读取通讯录权限
    public static final P GET_PHONESTATE = new P(Manifest.permission.READ_PHONE_STATE);//拨打电话权限
    public static final P GET_CALENDAR = new P(Manifest.permission.READ_CALENDAR);//读取日历权限
    public static final P USE_CAMERA = new P(Manifest.permission.CAMERA);//拍照录像权限
    public static final P GET_POS = new P(Manifest.permission.ACCESS_FINE_LOCATION);//获取位置信息权限
    public static final P GET_SENSOR = new P(Manifest.permission.BODY_SENSORS);//获取身体传感器权限
    public static final P SET_FILE = new P(Manifest.permission.WRITE_EXTERNAL_STORAGE);//文件读写权限
    public static final P SET_SMS = new P(Manifest.permission.SEND_SMS);//短信权限
    private static int callBackCode = 31636368;
    /*************************************成员变量**************************************************/
    private Activity activity;
    private String[] permissions;
    private PCB callBack;
    /**********************************************************************************************/
    /***********************************公共静态方法*************************************************/
    /**********************************************************************************************/
    /**
     * 从Activity创建权限帮助类
     */
    public static P create(Activity activity) {
        return new P().setACT(activity);
    }
    /**********************************************************************************************/
    /***********************************私有静态方法*************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /***************************************接口****************************************************/
    /**********************************************************************************************/
    /**
     * 权限申请回调
     * Permission CallBack
     */
    public interface PCB {
        void onCallback(String p, boolean isSuccess);
    }
    /**********************************************************************************************/
    /*************************************构造方法**************************************************/
    /**********************************************************************************************/
    private P(Activity activity, String permission) {
        this(permission);
        this.activity = activity;
    }

    private P(String... permission) {
        this.permissions = permission;
    }

    private P() {}
    /**********************************************************************************************/
    /*************************************继承方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /*************************************公共方法**************************************************/
    /**********************************************************************************************/
    /**
     * 设置发起权限帮助的Activity
     */
    public P setACT(Activity a) {
        this.activity = activity;
        return this;
    }

    /**
     * 设置发起的权限
     */
    public P setPER(String... p) {
        this.permissions = p;
        return this;
    }

    /**
     * 添加权限
     */
    public P addPER(String... p) {
        this.permissions = UArrays.merge(this.permissions, p);
        return this;
    }

    /**
     * 设置回调
     * 传入Activity必须为CDKActivity否则无法生效
     */
    public P setCB(PCB pcb) {
        this.callBack = pcb;
        return this;
    }
    /**********************************************************************************************/
    /*************************************私有方法**************************************************/
    /**********************************************************************************************/

    /**********************************************************************************************/
    /**************************************内部类***************************************************/
    /**********************************************************************************************/


}
