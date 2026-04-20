package com.candyenk.demo.activity;

import candyenk.android.base.ActivityCDK;
import candyenk.android.widget.DialogBottom;
import com.candyenk.demo.databinding.DbTestBinding;

public class DBTest extends ActivityCDK.Default {
    DbTestBinding bd;
    
    @Override
    protected void viewInit() {
        bd = DbTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        bd.b1.setOnClickListener(v -> {
            //DialogBottom
        });
        
        
    }
}
