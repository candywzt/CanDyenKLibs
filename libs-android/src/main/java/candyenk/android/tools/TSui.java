package candyenk.android.tools;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import rikka.shizuku.Shizuku;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Shizuku 服务辅助类
 */
public class TSui {

    //服务连接参数
    private Shizuku.UserServiceArgs usa;
    //服务连接监控
    private ServiceConnection usc;

    private TSui() {

    }

    /**
     * 检查是否有Shizuku权限
     */
    public static boolean hasPermission() {
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查Shizuku权限是否为Root
     */
    public static boolean hasRoot() {
        return Shizuku.getUid() == 0;
    }

    /**
     * Sui服务绑定构建器
     *
     * @param applicationId 包名
     * @param classs        服务实现类,不要用匿名类
     */
    public static Build create(String applicationId, Class<? extends IInterface> classs) {
        return new Build(applicationId, classs);
    }

    /**
     * 申请权限
     */
    public ERR request() {
        if (Shizuku.isPreV11()) return ERR.isPreV11;
        if (Shizuku.shouldShowRequestPermissionRationale()) return ERR.Refusal;
        if (hasPermission()) return ERR.isOk;
        Shizuku.requestPermission(9988);
        return null;
    }

    /**
     * 绑定服务
     */
    public boolean bind() {
        try {
            if (Shizuku.getVersion() >= 10) {
                Shizuku.bindUserService(usa, usc);
                return true;
            } else return false;
        } catch (Throwable tr) {
            return false;
        }
    }

    /**
     * 解绑
     */
    public boolean ubind() {
        try {
            if (Shizuku.getVersion() >= 10) {
                Shizuku.unbindUserService(usa, usc, true);
                return true;
            } else return false;
        } catch (Throwable tr) {
            return false;
        }
    }


    /**
     * 构建器
     */
    public static class Build {
        //包名
        private String applicationId;
        //服务类Class实例
        private Class<? extends IInterface> classs;
        //参数处理器
        private Consumer<Shizuku.UserServiceArgs> argHandler;
        //服务连接监听
        private BiConsumer<ComponentName, IBinder> connectListener;
        //服务断开监听
        private Consumer<ComponentName> disConnectListener;
        //服务异常监听
        private BiConsumer<ComponentName, IBinder> errorListener;
        //权限申请成功监听
        private Runnable successRequest;
        //权限申请失败监听
        private Runnable failRequest;


        private Build(String applicationId, Class<? extends IInterface> classs) {
            this.applicationId = applicationId;
            this.classs = classs;
        }

        /**
         * 设置Shizuku参数
         */
        public Build argsHandler(Consumer<Shizuku.UserServiceArgs> handler) {
            this.argHandler = handler;
            return this;
        }

        /**
         * 权限申请成功监听
         */
        public Build successRequest(Runnable listener) {
            this.successRequest = listener;
            return this;
        }

        /**
         * 权限申请失败监听
         */
        public Build failRequest(Runnable listener) {
            this.failRequest = listener;
            return this;
        }

        /**
         * 设置连接成功监听
         */
        public Build connectListener(BiConsumer<ComponentName, IBinder> listneer) {
            this.connectListener = listneer;
            return this;
        }

        /**
         * 设置连接断开监听
         */
        public Build disConnectListener(Consumer<ComponentName> listneer) {
            this.disConnectListener = listneer;
            return this;
        }

        /**
         * 设置连接异常监听
         */
        public Build errorListener(BiConsumer<ComponentName, IBinder> listneer) {
            this.errorListener = listneer;
            return this;
        }

        /**
         * 构建
         */
        public TSui build() {
            TSui tsui = new TSui();
            tsui.usa = new Shizuku.UserServiceArgs(new ComponentName(applicationId, classs.getName()));
            if (argHandler != null) argHandler.accept(tsui.usa);
            tsui.usc = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder ib) {
                    if (ib != null && ib.pingBinder()) {
                        if (connectListener != null) connectListener.accept(name, ib);
                    } else {
                        if (errorListener != null) errorListener.accept(name, ib);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    if (disConnectListener != null) disConnectListener.accept(name);
                }
            };
            Shizuku.OnRequestPermissionResultListener listener = new Shizuku.OnRequestPermissionResultListener() {
                @Override
                public void onRequestPermissionResult(int requestCode, int grantResult) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Shizuku.removeRequestPermissionResultListener(this);
                        if (successRequest != null) successRequest.run();
                    } else {
                        if (failRequest != null) failRequest.run();
                    }
                }
            };
            Shizuku.addRequestPermissionResultListener(listener);
            return tsui;
        }
    }

    public enum ERR {
        isOk("已授权"),
        isPreV11("Shizuku已过时"),
        Refusal("已永久拒绝");
        private String msg;

        ERR(String msg) {
            this.msg = msg;
        }

        public String msg() {
            return msg;
        }
    }


}
