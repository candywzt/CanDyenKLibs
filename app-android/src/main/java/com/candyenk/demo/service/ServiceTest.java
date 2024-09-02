package com.candyenk.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import candyenk.android.tools.L;
import candyenk.android.widget.DialogBottomText;

public class ServiceTest extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        L.e("aaa", "进来了");
        DialogBottomText db = new DialogBottomText(this);
        db.setContent("啦啦啦");
        db.show();
    }
}
