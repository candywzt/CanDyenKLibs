package candyenk.android.sui;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import candyenk.android.aidl.ISuiFile;
import candyenk.android.tools.TSui;

import java.io.File;

/**
 * Shiuku绑定
 * 文件操作实现类
 */
public class SuiFile extends ISuiFile.Stub {
    public static ISuiFile sui;
    //只读模式
    public static final int MODE_READ_ONLY = 0x10000000;
    //只写模式
    public static final int MODE_WRITE_ONLY = 0x20000000;
    //读写模式
    public static final int MODE_READ_WRITE = 0x30000000;
    //只创建模式
    public static final int MODE_CREATE = 0x08000000;
    //清空模式
    public static final int MODE_TRUNCATE = 0x4000000;
    //追加模式
    public static final int MODE_APPEND = 0x02000000;

    /**
     * 绑定Shizuku服务
     * 记得申请权限
     * 如果已经绑定,直接触发bindListener
     *
     * @param applicationID   应用包名
     * @param bindListener    绑定成功监听
     * @param disBindListener 解除绑定监听
     */
    public static void bindSui(String applicationID, Runnable bindListener, Runnable disBindListener) {
        if (sui != null) bindListener.run();
        else TSui.create(applicationID, SuiFile.class)
                .argsHandler(usa -> usa
                        .daemon(false)
                        .debuggable(false)
                        .processNameSuffix("service")
                        .version(1))
                .connectListener((c, i) -> {
                    sui = SuiFile.asInterface(i);
                    if (bindListener != null) bindListener.run();
                })
                .disConnectListener(c -> {
                    sui = null;
                    if (disBindListener != null) disBindListener.run();
                })
                .build().bind();

    }

    @Override
    public ParcelFileDescriptor getFD(String path, int mode) throws RemoteException {
        if (path == null || path.isBlank()) return null;
        File file = new File(path);
        if (!file.exists()) return null;
        try {
            return ParcelFileDescriptor.open(file, mode);
        } catch (Exception e) {
            throw new RemoteException(e);
        }
    }

    @Override
    public String[] getChilds(String path) throws RemoteException {
        if (path == null || path.isBlank()) return new String[0];
        File file = new File(path);
        return file.list();
    }

    @Override
    public boolean isDirectory(String path) throws RemoteException {
        if (path == null || path.isBlank()) return false;
        File file = new File(path);
        return file.isDirectory();
    }

    @Override
    public long getLastModified(String path) throws RemoteException {
        if (path == null || path.isBlank()) return 0;
        File file = new File(path);
        return file.lastModified();
    }

    @Override
    public long getSize(String path) throws RemoteException {
        if (path == null || path.isBlank()) return 0;
        File file = new File(path);
        return file.length();
    }
}
