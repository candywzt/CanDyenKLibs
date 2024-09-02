package com.candyenk.demo.activity;

import android.content.Intent;
import android.content.pm.IPackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import candyenk.android.base.ActivityCDK;
import com.candyenk.demo.databinding.ActivityTestBinding;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;


public class ActivityTest1 extends ActivityCDK {
    private ActivityTestBinding bd;

    @Override
    protected void intentInit() {

    }

    private static final IPackageManager PACKAGE_MANAGER = IPackageManager.Stub.asInterface(
            new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));

    public static void grantRuntimePermission(String packageName, String permissionName, int userId) {
        try {
            PACKAGE_MANAGER.grantRuntimePermission(packageName, permissionName, userId);
        } catch (RemoteException tr) {
            throw new RuntimeException(tr.getMessage(), tr);
        }
    }

    @Override
    protected void viewInit() {
        bd = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());
        bd.b1.setOnClickListener(v -> {
           setResult(RESULT_OK,new Intent());
        });
        bd.b2.setOnClickListener(v -> {

        });
        bd.b3.setOnClickListener(v -> {

        });
    }

    @Override
    protected void contentInit(Bundle save) {
    }

    @Override
    protected void eventInit() {

    }

    @Override
    protected Bundle saveData(Bundle bundle) {
        return null;
    }
}
