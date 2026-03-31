package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import candyenk.android.base.ActivityCDK;
import candyenk.android.widget.DialogBottomText;
import com.candyenk.demo.BuildConfig;
import com.candyenk.demo.IUserService;
import candyenk.android.tools.TSui;
import com.candyenk.demo.databinding.ShizukuTestBinding;
import com.candyenk.demo.service.IUS;


@SuppressLint("SetTextI18n")
public class ShizukuTest extends ActivityCDK.Default {
    private ShizukuTestBinding bd;
    private TSui sui;
    private IUserService ius;


    @Override
    protected void viewInit() {

        bd = ShizukuTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());

        bd.b1.setOnClickListener(v -> {
            if (sui.hasPermission()) return;
            TSui.ERR err = sui.request();
            if (err != null) bd.b1.setText(err.msg());
        });
        bd.b2.setOnClickListener(v -> {
            if (ius != null) return;
            if (sui.hasPermission()) sui.bind();
        });
        bd.b3.setOnClickListener(v -> {
            try {
                StringBuilder sb = new StringBuilder("应用:");
                sb.append("小米相册\n");
                int size = ius.getAllIntentFilters("com.miui.gallery").getList().size();
                sb.append("==================");

                for (int i = 0; i < size; i++) {

                    String longString = ius.toLongString(i);
                    sb.append("\n:::::::::::::::::::::\n").append(longString);
                }
                DialogBottomText dbt = new DialogBottomText(this);
               dbt.setTitle("执行结果:");
                dbt.setContent(sb);
                dbt.show();
                //bd.tv.setText(sb);
            } catch (Exception e) {
                Log.e("AAAAA", "异常:" + e.getMessage());
            }
        });

    }

    @Override
    protected void contentInit(Bundle save) {
        this.sui = TSui.create(BuildConfig.APPLICATION_ID, IUS.class)
                .argsHandler(usa -> usa
                        .daemon(false)
                        .debuggable(BuildConfig.DEBUG)
                        .processNameSuffix("service")
                        .version(BuildConfig.VERSION_CODE))
                .connectListener((c, i) -> {
                    bd.b2.setText("已绑定");
                    ius = IUserService.Stub.asInterface(i);
                })
                .disConnectListener(c -> {
                    bd.b2.setText("重新绑定");
                    ius = null;
                })
                .successRequest(() -> bd.b1.setText("已授权"))
                .failRequest(() -> bd.b1.setText("已拒绝"))
                .build();
        if (sui.hasPermission()) {
            bd.b1.setText("已授权");
            sui.bind();
        }
    }
}
