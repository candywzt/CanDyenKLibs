package candyenk.android.sui;

import android.os.ParcelFileDescriptor;
import candyenk.android.aidl.ISuiFileInfo;
import candyenk.android.tools.TSui;
import candyenk.java.io.FileInfo;
import candyenk.java.io.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Shizuku专用文件信息类
 */
public class SuiFileInfo extends FileInfo {
    private static ISuiFileInfo sui;
    private ParcelFileDescriptor fd;

    @Override
    public FileInputStream read() {
        if (this.isDirectory()) return null;
        return new FileInputStream(this.fd.getFileDescriptor());
    }

    @Override
    public FileOutputStream write() {
        if (this.isDirectory()) return null;
        return new FileOutputStream(this.fd.getFileDescriptor());
    }

    @Override
    public SuiFileInfo getParent() {
        return SuiFileInfo.create(file.getParent());
    }

    @Override
    public FileInfo[] listInfos(boolean showHide, boolean showMove) {
        try {
            List<FileInfo> list = new ArrayList<>();
            String[] childs = sui.getChilds(path);
            if (showMove && file.getParent() != null) list.add(superInfo);
            if (childs.length == 0) list.add(emptyInfo);
            else for (String child : childs) {
                SuiFileInfo info = SuiFileInfo.create(child);
                if (info == null || (!showHide && info.isHide)) continue;
                list.add(info);
            }
            return list.toArray(new FileInfo[0]);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCustom() {
        return false;
    }


    /**
     * 绑定Shizuku服务
     * 记得申请权限
     *
     * @param applicationID   应用包名
     * @param bindListener    绑定成功监听
     * @param disBindListener 解除绑定监听
     */
    public static void bindSui(String applicationID, Runnable bindListener, Runnable disBindListener) {
        TSui.create(applicationID, SuiFileInfoImpl.class)
                .argsHandler(usa -> usa
                        .daemon(false)
                        .debuggable(false)
                        .processNameSuffix("service")
                        .version(1))
                .connectListener((c, i) -> {
                    SuiFileInfo.sui = SuiFileInfoImpl.asInterface(i);
                    if (bindListener != null) bindListener.run();
                })
                .disConnectListener(c -> {
                    SuiFileInfo.sui = null;
                    if (disBindListener != null) disBindListener.run();
                })
                .build().bind();

    }

    /**
     * 创建SuiFileInfo对象
     * 可用来获取文件基本信息,和获取文件IO流
     *
     * @param path 文件路径
     */
    public static SuiFileInfo create(String path) {
        if (sui == null) throw new RuntimeException("请先调用bindSui方法绑定Shizuku服务");
        if (path == null || path.isEmpty()) return null;
        SuiFileInfo info = new SuiFileInfo();
        info.file = new File(path);
        info.name = info.file.getName();

        info.path = path;
        info.isHide = info.name.startsWith(".");
        try {
            info.fd = sui.getFD(path);
            info.size = info.fd.getStatSize();
            info.lmd = sui.getLastModified(path);
            info.type = sui.isDirectory(path) ? FileType.DIRECTORY : FileType.type(info.name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return info;
    }
}

