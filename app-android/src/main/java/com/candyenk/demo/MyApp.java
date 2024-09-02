package com.candyenk.demo;

import android.util.Log;
import candyenk.android.base.ApplicationCDK;
import rikka.shizuku.Shizuku;
import rikka.sui.Sui;


public class MyApp extends ApplicationCDK {
    public static boolean isSui = Sui.init(BuildConfig.APPLICATION_ID);
    public static boolean hasAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        Shizuku.addBinderReceivedListenerSticky(() -> {
            hasAPI = true;
            Log.e(TAG, "Shizuku Server 已就绪");
        });
        Shizuku.addBinderDeadListener(() -> {
            hasAPI = false;
            Log.e(TAG, "Shizuku Server 已就绪");
        });
        //LitePal.initialize(this);
        //InitApp();
        //InitActivityManager();
        //QQ.initialize(this, "101513719", null);
//        Operator.initialize(this);
//        LitePalDB ldb = new LitePalDB("test", 1);
//        ldb.addClassName(DB.class.getName());
//        Operator.use(ldb);
    }
}
