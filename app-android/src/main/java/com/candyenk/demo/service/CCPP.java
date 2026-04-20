package com.candyenk.demo.service;

import android.content.ContentValues;
import android.util.Log;
import candyenk.android.tools.CP;
import candyenk.android.utils.UFile;
import candyenk.java.io.IO;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

/**
 * 服务端
 */
public class CCPP extends CP {
    Gson gson;
    
    
    @Override
    public int set(ContentValues data) {
        FileOutputStream aaa = UFile.writeData(getContext(), "aaa", false);
        return IO.write(aaa, gson.toJson(CP.toMap(data)).getBytes()) ? 1 : -1;
    }
    
    @Override
    public ContentValues get(String key) {
        FileInputStream aaa = UFile.readData(getContext(), "aaa");
        Map<String,Object> b = gson.fromJson(new String(IO.read(aaa)), Map.class);
        Log.e("CDK", "Get方法结果：" + b);
        return toCV(b);
    }
    
    
    @Override
    public boolean onCreate() {
        gson = new Gson();
        
        return true;
    }
}
