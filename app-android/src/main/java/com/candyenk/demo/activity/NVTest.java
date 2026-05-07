package com.candyenk.demo.activity;

import candyenk.android.base.ActivityCDK;
import candyenk.android.tools.V;
import com.candyenk.demo.databinding.NvTestBinding;

public class NVTest extends ActivityCDK.Default {
    private NvTestBinding bd;
    
    @Override
    protected void viewInit() {
        
        bd = NvTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        V.LL(bd.image).nimble().refresh();
    }
    
}
