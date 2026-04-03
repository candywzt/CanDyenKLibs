package candyenk.android.sui;

import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import candyenk.android.aidl.ISuiFileInfo;

import java.io.File;

/**
 * 具体实现类
 */
public class SuiFileInfoImpl extends ISuiFileInfo.Stub {

    @Override
    public ParcelFileDescriptor getFD(String path) throws RemoteException {
        if (path == null || path.isBlank()) return null;
        File file = new File(path);
        if (!file.exists()) return null;
        try {
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
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
        if (path == null || path.isBlank()) return false;//TODO:这里不合适
        File file = new File(path);
        return file.isDirectory();
    }

    @Override
    public long getLastModified(String path) throws RemoteException {
        if (path == null || path.isBlank()) return 0;//TODO:这里不合适
        File file = new File(path);
        return file.lastModified();
    }
}
