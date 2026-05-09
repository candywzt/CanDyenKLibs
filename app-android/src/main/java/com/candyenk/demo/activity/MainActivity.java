package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import candyenk.android.base.ActivityCDK;
import candyenk.android.tools.CP;
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
        fun(bd.b4, "底部弹窗测试", DialogTest.class);
        
        
        bd.b5.setOnClickListener(v -> {
            CP.CPT cpt = CP.CREATE(this, "com.candyenk.demo");
            ContentValues data = new ContentValues();
            data.put("你好", "你也好");
            data.put("你好1", 12345);
            data.put("你好2", 1.23466);
            int set = cpt.set(data);
            Log.e("CDK", "发送内容：" + CP.toMap(data));
            Log.e("CDK", "发送结果：" + set);
        });
        bd.b6.setOnClickListener(v -> {
            CP.CPT cpt = CP.CREATE(this, "com.candyenk.demo");
            ContentValues b = cpt.get("666");
            StringBuilder sb = new StringBuilder();
            for (String s : b.keySet()) {
                sb.append(s).append(":").append(b.get(s)).append("\n");
            }
            toast(sb.toString());
            Log.e("CDK", "接收内容：" + sb);
        });
        
    }
    
    
    private void fun(Button view, String title, Class<? extends Activity> classz) {
        view.setText(title);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(this, classz);
            startActivity(intent);
        });
    }
}
