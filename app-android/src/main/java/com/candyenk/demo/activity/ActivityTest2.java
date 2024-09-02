package com.candyenk.demo.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import candyenk.android.base.ActivityCDK;
import com.candyenk.demo.BuildConfig;
import com.candyenk.demo.IUserService;
import com.candyenk.demo.MyApp;
import com.candyenk.demo.databinding.ActivityTestBinding;
import com.candyenk.demo.service.UserService;
import rikka.shizuku.Shizuku;

import java.io.File;


@SuppressLint("SetTextI18n")
public class ActivityTest2 extends ActivityCDK.Default {
    private ActivityTestBinding bd;
    private IUserService ius;
    private final Shizuku.UserServiceArgs usa = new Shizuku.UserServiceArgs(
            new ComponentName(
                    BuildConfig.APPLICATION_ID,
                    UserService.class.getName()))
            .daemon(false)
            .debuggable(BuildConfig.DEBUG)
            .processNameSuffix("service")
            .version(2);
    private final ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            append("Shizuku服务已绑定\n");
            ius = IUserService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            append("Shizuku服务已解绑\n");
            ius = null;
        }
    };

    @Override
    protected void viewInit() {
        bd = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(bd.getRoot());

        bd.b1.setOnClickListener(v -> {
            if (Shizuku.getVersion() == -1) append("Shizuku未安装\n");
            else Shizuku.requestPermission(0);
        });
        bd.b11.setOnClickListener(v -> {
            if (Shizuku.getVersion() == -1) append("Shizuku未安装\n");
            else Shizuku.unbindUserService(usa, sc, true);
            ius = null;
        });
        bd.b2.setOnClickListener(v -> {
            int i = checkSelfPermission("android.permission.SYSTEM_ALERT_WINDOW");
            Log.e(TAG, "权限检查:" + i);
        });
        bd.b21.setOnClickListener(v -> {
            try {
                Log.e(TAG, "结果:"+ius.run("android.permission.SYSTEM_ALERT_WINDOW"));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void eventInit() {
        Shizuku.addRequestPermissionResultListener((requestCode, grantResult) -> {
            clean();
            append(MyApp.isSui ? "Sui" : "Shizuku");
            append(grantResult == 0 ? "已授权:V" : "未授权:V");
            append(String.valueOf(Shizuku.getVersion()));
            append("\n");
            Shizuku.bindUserService(usa, sc);
        });
    }

    private void append(String str) {
        bd.tv.append(str);
    }

    private void clean() {
        bd.tv.setText("");
    }

    public static class RFile extends File {
        private static IUserService ius;

        public RFile(String pathname) {
            super(pathname);
        }

        @Override
        public boolean isFile() {
            try {
                return ius.isDirector(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isDirectory() {
            try {
                return ius.isDirector(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean exists() {
            try {
                return ius.exists(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public String getParent() {
            try {
                return ius.getParent(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }


        @Override
        public String[] list() {
            try {
                return ius.list(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public long lastModified() {
            try {
                return ius.lastModified(getAbsolutePath());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
