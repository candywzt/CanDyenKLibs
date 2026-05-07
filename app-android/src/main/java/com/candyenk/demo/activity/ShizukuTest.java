package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import candyenk.android.base.ActivityCDK;
import candyenk.android.sui.SuiFile;
import candyenk.android.sui.SuiFileInfo;
import candyenk.android.tools.TSui;
import candyenk.android.widget.DialogBottomText;
import candyenk.android.widget.DialogFileChooser;
import candyenk.java.io.IO;
import com.candyenk.demo.BuildConfig;
import com.candyenk.demo.databinding.ShizukuTestBinding;
import rikka.shizuku.Shizuku;


@SuppressLint("SetTextI18n")
public class ShizukuTest extends ActivityCDK.Default {
    private TSui sui;
    //private IUserService ius;
    
    @Override
    protected void viewInit() {
        Log.e("AAAAA", "UID:" + Shizuku.getUid());
        ShizukuTestBinding bd = ShizukuTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        
        bd.b1.setOnClickListener(v -> {
            
        });
        bd.b2.setOnClickListener(v -> {
            
        });
        bd.b3.setOnClickListener(v -> {
            
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
        
    }
}
