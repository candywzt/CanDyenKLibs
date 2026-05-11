package com.candyenk.demo.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import candyenk.android.aidl.ISuiPM;
import candyenk.android.base.ActivityCDK;
import candyenk.android.utils.USys;
import candyenk.android.widget.FloatWindow;
import com.candyenk.demo.R;
import com.candyenk.demo.databinding.WindowsTestBinding;


public class WindowsTest extends ActivityCDK.Default {
    private WindowsTestBinding bd;
    private ISuiPM pm;

    @Override
    protected void viewInit() {
        bd = WindowsTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        FloatWindow fw = FloatWindow.create(this);
        fw.setContent(R.layout.windows_test);
        bd.b1.setOnClickListener(v -> {
            fw.showMainWindow();
        });
        bd.b2.setOnClickListener(v -> {
            fw.showMinWindow();
        });
        bd.b3.setOnClickListener(v -> {
            fw.dismiss();
        });
        bd.b4.setOnClickListener(v -> {
            try {
                if (pm == null) return;
                PackageManager pm;
                //pm.install
                Log.e("GD", "已授权");
                Log.e("GD", "监察权限:" + USys.checkPermission(this, "android.permission.SYSTEM_ALERT_WINDOW"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
           
        });
    }

    @Override
    protected void contentInit(Bundle save) {
//        TSui.create(BuildConfig.APPLICATION_ID, ISuiPMImpl.class)
//                .argsHandler(usa -> usa
//                        .daemon(false)
//                        .debuggable(BuildConfig.DEBUG)
//                        .processNameSuffix("service")
//                        .version(BuildConfig.VERSION_CODE))
//                .connectListener((c, i) -> {
//                    Log.e("GD", "已绑定");
//                    pm = ISuiPM.Stub.asInterface(i);
//                })
//                .disConnectListener(c -> {
//                    pm = null;
//                })
//                .build().bind();
    }

    
}
