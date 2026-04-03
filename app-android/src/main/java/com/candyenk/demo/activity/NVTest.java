package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import candyenk.android.base.ActivityCDK;
import candyenk.android.tools.NV;
import com.candyenk.demo.databinding.NvTestBinding;


@SuppressLint("SetTextI18n")
public class NVTest extends ActivityCDK.Default {
    private NvTestBinding bd;


    @Override
    protected void viewInit() {

        bd = NvTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        NV.apply(bd.image);


    }

}
