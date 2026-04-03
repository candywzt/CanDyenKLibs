package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import candyenk.android.base.ActivityCDK;
import candyenk.android.sui.SuiFile;
import candyenk.android.sui.SuiFileInfo;
import candyenk.android.widget.DialogBottomText;
import candyenk.android.widget.DialogFileChooser;
import candyenk.java.io.IO;
import com.candyenk.demo.BuildConfig;
import com.candyenk.demo.service.IUserService;
import candyenk.android.tools.TSui;
import com.candyenk.demo.databinding.ShizukuTestBinding;
import com.candyenk.demo.service.IUS;
import rikka.shizuku.Shizuku;


@SuppressLint("SetTextI18n")
public class ShizukuTest extends ActivityCDK.Default {
    private ShizukuTestBinding bd;
    private TSui sui;
    private IUserService ius;

    @Override
    protected void viewInit() {
        Log.e("AAAAA", "UID:" + Shizuku.getUid());
        bd = ShizukuTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());

        bd.b1.setOnClickListener(v -> {
            if (TSui.hasPermission()) return;
            TSui.ERR err = sui.request();
            if (err != null) bd.b1.setText(err.msg());
        });
        bd.b2.setOnClickListener(v -> {
            if (ius != null) return;
            if (TSui.hasPermission()) sui.bind();
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
            } catch (Exception e) {
                bd.tv.setText(bd.tv.getText() + "\n异常:" + e.getMessage());
                Log.e("AAAAA", "异常", e);
            }
        });
        bd.b4.setOnClickListener(v -> {
            SuiFile.bindSui(BuildConfig.APPLICATION_ID, this::openFileChoose, null);
        });

    }

    private void openFileChoose() {
        DialogFileChooser dfc = new DialogFileChooser(this, SuiFileInfo.create("/sdcard"));
        dfc.show();
        dfc.setShowHideFile(true);
        dfc.setOnFileClickListener((fileInfo, view) -> {
            DialogBottomText dbt = new DialogBottomText(this);
            dbt.setTitle("文件信息");
            dbt.setContent(IO.readString(fileInfo.read()));
            dbt.show();
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
                    bd.tv.setText(bd.tv.getText() + "\n已绑定");
                    ius = IUserService.Stub.asInterface(i);
                })
                .disConnectListener(c -> {
                    bd.b2.setText("重新绑定");
                    ius = null;
                })
                .successRequest(() -> bd.b1.setText("已授权"))
                .failRequest(() -> bd.b1.setText("已拒绝"))
                .build();
        if (TSui.hasPermission()) {
            bd.b1.setText("已授权");
            bd.tv.setText(bd.tv.getText() + "\n开始绑定");
            sui.bind();
        }
    }
}
