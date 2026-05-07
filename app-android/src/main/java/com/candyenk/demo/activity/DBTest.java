package com.candyenk.demo.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import candyenk.android.base.ActivityCDK;
import candyenk.android.tools.RC;
import candyenk.android.tools.V;
import candyenk.android.viewgroup.SmoothLayout;
import candyenk.android.widget.DialogFileChooser;
import candyenk.android.widget.DialogLoading;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

public class DBTest extends ActivityCDK.Default {
    
    @Override
    protected void viewInit() {
        ScrollView sv = new ScrollView(this);
        V.FL(sv).size(-1, -1).refresh();
        setContentView(sv);
        LinearLayout l = new LinearLayout(this);
        V.FL(l).size(-1, -1).orientation(1).padding(10).margin(50).parent(sv);
        
        MaterialCardView c = new MaterialCardView(this);
        V.LL(c).size(-2, -2).radiusDP(36).margin(50).ele(-1).add(createIV()).parent(l);
        
        SmoothLayout sf = new SmoothLayout(this);
        V.LL(sf).size(-2, -2).radiusDP(36).margin(50).add(createIV()).parent(l);
        
        ShapeableImageView iv = new ShapeableImageView(this);
        V.LL(iv).size(832, 830).margin(50).drawable(candyenk.android.R.drawable.aaa).radiusDP(36).parent(l);
        
        sf.setOnClickListener(v -> {
            DialogFileChooser db = new DialogFileChooser(this, new File("/sdcard"));
            db.show();
        });
        
        c.setOnClickListener(v -> {
            DialogLoading dl = new DialogLoading(this);
            dl.setTitle("还在中。。。");
          
            dl.show();
        });
    }
    
    private View createIV() {
        ImageView iv = new ImageView(this);
        V.LP(iv).size(832, 830).refresh();
        iv.setImageResource(candyenk.android.R.drawable.aaa);
        return iv;
    }
    
}
