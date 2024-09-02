package com.candyenk.demo.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import candyenk.android.base.ActivityCDK;
import com.candyenk.demo.databinding.ActivityTestBinding;

import java.util.function.Function;


public class ActivityTest extends ActivityCDK.Default {
    private ActivityTestBinding bd;

    @Override
    protected void viewInit() {
        bd = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        WM wm = WM.create(this);
        bd.b1.setOnClickListener(v -> {
            wm.showMainWindow();
        });
        bd.b2.setOnClickListener(v -> {
            wm.showMinWindow();
        });
        bd.b3.setOnClickListener(v -> {
            wm.closeWindow();
        });
    }

    @Override
    protected void contentInit(Bundle save) {
        super.contentInit(save);
    }

    @Override
    protected Bundle saveData(Bundle bundle) {
        return super.saveData(bundle);
    }
}
