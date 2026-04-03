package candyenk.android.sui;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
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
    private ParcelFileDescriptor fd;

    @Override
    public FileInputStream read() {
        if (this.isDirectory() || fd == null) return null;
        return new FileInputStream(this.fd.getFileDescriptor());
    }

    @Override
    public FileOutputStream write() {
        if (this.isDirectory() || fd == null) return null;
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
            String[] childs = SuiFile.sui.getChilds(path);
            if (showMove && file.getParent() != null) list.add(superInfo);
            if (childs.length == 0) list.add(emptyInfo);
            else for (String child : childs) {
                SuiFileInfo info = SuiFileInfo.create(new File(file, child).getAbsolutePath());
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
     * 创建SuiFileInfo对象
     * 可用来获取文件基本信息,和获取文件IO流
     *
     * @param path 文件路径
     */
    public static SuiFileInfo create(String path) {
        if (SuiFile.sui == null) throw new RuntimeException("请先调用SuiFIle.bindSui方法绑定Shizuku服务");
        if (path == null || path.isEmpty()) return null;
        SuiFileInfo info = new SuiFileInfo();
        info.file = new File(path);
        info.name = info.file.getName();
        info.path = info.file.getAbsolutePath();
        info.isHide = info.name.startsWith(".");
        try {
            info.type = SuiFile.sui.isDirectory(path) ? FileType.DIRECTORY : FileType.type(info.name);
            if (!info.isDirectory()) {
                info.fd = SuiFile.sui.getFD(path, SuiFile.MODE_READ_ONLY);
                //空指向文件
                if (info.fd == null) return info;
                info.size = info.fd.getStatSize();
                info.lmd = SuiFile.sui.getLastModified(path);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return info;
        }
        return info;
    }
}

