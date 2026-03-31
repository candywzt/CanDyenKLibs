package com.candyenk.demo.service;

import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.RemoteException;
import com.candyenk.demo.IUserService;
import rikka.shizuku.SystemServiceHelper;

import java.util.List;

public class IUS extends IUserService.Stub {
    IPackageManager pm;
    List<IntentFilter> list;

    private void init() {
        if (pm == null)
            pm = IPackageManager.Stub.asInterface(SystemServiceHelper.getSystemService("package"));
    }

    @Override
    public candyenk.android.aidl.BList<PackageInfo> getPackages() throws RemoteException {
        try {
            init();
            return new candyenk.android.aidl.BList<>(pm.getInstalledPackages(0, 0));
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public candyenk.android.aidl.BList<IntentFilter> getAllIntentFilters(String packageName) throws RemoteException {
        try {
            init();
            pm = IPackageManager.Stub.asInterface(SystemServiceHelper.getSystemService("package"));
            list = pm.getAllIntentFilters(packageName).getList();
            return new candyenk.android.aidl.BList<>(list);
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public String toLongString(int index) throws RemoteException {
        if (list != null)
            return list.get(index).toLongString();
        return "";
    }


}
