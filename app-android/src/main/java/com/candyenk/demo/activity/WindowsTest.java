package com.candyenk.demo.activity;

import candyenk.android.base.ActivityCDK;
import candyenk.android.widget.FloatWindow;
import com.candyenk.demo.R;
import com.candyenk.demo.databinding.WindowsTestBinding;


public class WindowsTest extends ActivityCDK.Default {
    private WindowsTestBinding bd;

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
    }
}
