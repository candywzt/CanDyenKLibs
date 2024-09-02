package com.candyenk.demo.service;

import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.content.Context;
import android.content.integrity.IAppIntegrityManager;
import android.content.pm.*;
import android.debug.IAdbManager;
import android.os.*;
import android.permission.IPermissionManager;
import android.permission.PermissionManager;
import candyenk.android.utils.UFile;
import candyenk.java.tools.T;
import com.android.internal.os.IShellCallback;
import com.candyenk.demo.BuildConfig;
import com.candyenk.demo.IUserService;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.USER_SERVICE;

public class UserService extends IUserService.Stub {
    @Override
    public void destroy() {
        System.exit(0);
    }

    @Override
    public void exit() {
        destroy();
    }

    @Override
    public String readFile(String path) throws RemoteException {
        File file = new File(path);
        if (!file.exists()) return "NULL";
        return UFile.readString(file);
    }


    @Override
    public boolean writeFile(String path, byte[] bytes) throws RemoteException {
        File file = new File(path);
        if (!file.exists() && !UFile.createFile(file)) return false;
        return UFile.writeBytes(file, bytes, false);
    }

    @Override
    public boolean isFile(String path) throws RemoteException {
        return new File(path).isFile();
    }

    @Override
    public boolean isDirector(String path) throws RemoteException {
        return new File(path).isDirectory();
    }

    @Override
    public long length(String path) throws RemoteException {
        return new File(path).length();
    }

    @Override
    public boolean exists(String path) throws RemoteException {
        return new File(path).exists();
    }

    @Override
    public String getParent(String path) throws RemoteException {
        return new File(path).getParent();
    }

    @Override
    public String[] list(String path) throws RemoteException {
        return new File(path).list();
    }

    @Override
    public long lastModified(String path) throws RemoteException {
        return new File(path).lastModified();
    }

    @Override
    public String run(String arg) throws RemoteException {
       
        return String.valueOf(666);
    }
}
