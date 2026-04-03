package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import candyenk.android.base.ActivityCDK;
import com.candyenk.demo.databinding.ActivityMainBinding;


@SuppressLint("SetTextI18n")
public class MainActivity extends ActivityCDK.Default {
    private ActivityMainBinding bd;



    @Override
    protected void viewInit() {

        bd = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        fun(bd.b1, "Shizuku测试", ShizukuTest.class);
        fun(bd.b2, "悬浮窗测试", WindowsTest.class);
        fun(bd.b3, "灵动卡片测试", NVTest.class);
        
    }

    

    private void fun(Button view, String title, Class<? extends Activity> classz) {
        view.setText(title);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(this, classz);
            startActivity(intent);
        });
    }

}
