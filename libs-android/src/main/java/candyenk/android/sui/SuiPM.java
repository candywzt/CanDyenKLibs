package candyenk.android.sui;

import android.os.RemoteException;
import candyenk.android.aidl.ISuiPM;
import candyenk.android.tools.TSui;

public class SuiPM extends ISuiPM.Stub {
    public static ISuiPM sui;

    /**
     * 绑定Shizuku服务
     * 记得申请权限
     *
     * @param applicationID   应用包名
     * @param bindListener    绑定成功监听
     * @param disBindListener 解除绑定监听
     */
    public static void bindSui(String applicationID, Runnable bindListener, Runnable disBindListener) {
        TSui.create(applicationID, SuiPM.class)
                .argsHandler(usa -> usa
                        .daemon(false)
                        .debuggable(false)
                        .processNameSuffix("service")
                        .version(1))
                .connectListener((c, i) -> {
                    sui = asInterface(i);
                    if (bindListener != null) bindListener.run();
                })
                .disConnectListener(c -> {
                    sui = null;
                    if (disBindListener != null) disBindListener.run();
                })
                .build().bind();

    }


    @Override
    public void grantPermission(String packageName, int uid, int op) throws RemoteException {

    }


    @Override
    public void revokePermission(String packageName, int uid, int op) throws RemoteException {

    }


}
