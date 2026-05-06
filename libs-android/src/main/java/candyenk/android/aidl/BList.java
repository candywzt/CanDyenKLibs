package candyenk.android.aidl;

import android.os.*;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 通过IPC传输超长列表用
 * 因为系统自带的 ParceledListSlice 不可用
 * 自带AIDL文件
 */
@SuppressWarnings("rawtypes")
public class BList<T extends Parcelable> implements Parcelable {
    public static final ClassLoaderCreator<BList> CREATOR = new ClassLoaderCreator<>() {
        @Override
        public BList createFromParcel(Parcel in) {
            return new BList(in, getClass().getClassLoader(), getClass());
        }
        
        @Override
        public BList[] newArray(int size) {
            return new BList[size];
        }
        
        @Override
        public BList createFromParcel(Parcel in, ClassLoader loader) {
            return new BList(in, loader, getClass());
        }
    };
    private static final String TAG = "BList";
    private static final boolean DEBUG = false;
    private static final int MAX_IPC_SIZE = IBinder.getSuggestedMaxIpcSizeBytes();
    private final List<T> mList;
    private int mInlineCountLimit = Integer.MAX_VALUE;
    
    public BList(List<T> list) {
        mList = list;
    }
    
    private BList(Parcel p, ClassLoader loader, Class<?> classz) {
        final int N = p.readInt();
        mList = new ArrayList<T>(N);
        if (DEBUG) Log.d(TAG, "取回 " + N + " 个项目");
        if (N <= 0) {
            return;
        }
        
        Creator<?> creator = readParcelableCreator(p, loader, classz);
        Class<?> listElementClass = null;
        
        int i = 0;
        while (i < N) {
            if (p.readInt() == 0) {
                break;
            }
            listElementClass = readVerifyAndAddElement(creator, p, loader, listElementClass);
            if (DEBUG) Log.d(TAG, "内容读取 #" + i + ": " + mList.get(mList.size() - 1));
            i++;
        }
        if (i >= N) {
            return;
        }
        final IBinder retriever = p.readStrongBinder();
        while (i < N) {
            if (DEBUG) Log.d(TAG, "读取更多 @" + i + " of " + N + ": 读取器=" + retriever);
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            data.writeInt(i);
            try {
                retriever.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0);
            } catch (RemoteException e) {
                Log.w(TAG, "失效回收阵列;仅接收 " + i + " of " + N, e);
                return;
            }
            while (i < N && reply.readInt() != 0) {
                listElementClass = readVerifyAndAddElement(creator, reply, loader, listElementClass);
                if (DEBUG) Log.d(TAG, "读取更多 #" + i + ": " + mList.get(mList.size() - 1));
                i++;
            }
            reply.recycle();
            data.recycle();
        }
    }
    
    private static void verifySameType(final Class<?> expected, final Class<?> actual) {
        if (!actual.equals(expected)) {
            throw new IllegalArgumentException("无法解包类型 " + actual.getName() + " 类型列表 " + (expected == null ? null : expected.getName()));
        }
    }
    
    public List<T> getList() {
        return mList;
    }
    
    /**
     * 设定数组中将包含的最大条目数量限制
     * 在该对象的初始分组中内嵌。
     *
     * @param maxCount 最大数量
     */
    public void setInlineCountLimit(int maxCount) {
        mInlineCountLimit = maxCount;
    }
    
    @Override
    public int describeContents() {
        int contents = 0;
        final List<T> list = getList();
        for (int i = 0; i < list.size(); i++) {
            contents |= list.get(i).describeContents();
        }
        return contents;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final int N = mList.size();
        final int callFlags = flags;
        dest.writeInt(N);
        if (DEBUG) Log.d(TAG, "写入 " + N + " 个项目");
        if (N > 0) {
            final Class<?> listElementClass = mList.get(0).getClass();
            writeParcelableCreator(mList.get(0), dest);
            int i = 0;
            while (i < N && i < mInlineCountLimit && dest.dataSize() < MAX_IPC_SIZE) {
                dest.writeInt(1);
                
                final T parcelable = mList.get(i);
                verifySameType(listElementClass, parcelable.getClass());
                writeElement(parcelable, dest, callFlags);
                
                if (DEBUG) Log.d(TAG, "内容写入 #" + i + ": " + mList.get(i));
                i++;
            }
            if (i < N) {
                dest.writeInt(0);
                Binder retriever = new Binder() {
                    @Override
                    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                        if (code != FIRST_CALL_TRANSACTION) {
                            return super.onTransact(code, data, reply, flags);
                        }
                        int i = data.readInt();
                        if (DEBUG) Log.d(TAG, "写入更多 @" + i + " of " + N);
                        while (i < N && reply.dataSize() < MAX_IPC_SIZE) {
                            reply.writeInt(1);
                            
                            final T parcelable = mList.get(i);
                            verifySameType(listElementClass, parcelable.getClass());
                            writeElement(parcelable, reply, callFlags);
                            
                            if (DEBUG) Log.d(TAG, "写入更多 #" + i + ": " + mList.get(i));
                            i++;
                        }
                        if (i < N) {
                            if (DEBUG) Log.d(TAG, "违反 @" + i + " of " + N);
                            reply.writeInt(0);
                        }
                        return true;
                    }
                };
                if (DEBUG) Log.d(TAG, "Breaking违反 @" + i + " of " + N + ": 读取器=" + retriever);
                dest.writeStrongBinder(retriever);
            }
        }
    }
    
    protected void writeElement(T parcelable, Parcel reply, int callFlags) {
        parcelable.writeToParcel(reply, callFlags);
    }
    
    protected void writeParcelableCreator(T parcelable, Parcel dest) {
        dest.writeParcelableCreator(parcelable);
    }
    
    protected Creator<?> readParcelableCreator(Parcel from, ClassLoader loader, Class<?> classz) {
        return from.readParcelableCreator(loader, classz);
    }
    
    private Class<?> readVerifyAndAddElement(Creator<?> creator, Parcel p, ClassLoader loader, Class<?> listElementClass) {
        final T parcelable = readCreator(creator, p, loader);
        if (listElementClass == null) {
            listElementClass = parcelable.getClass();
        } else {
            verifySameType(listElementClass, parcelable.getClass());
        }
        mList.add(parcelable);
        return listElementClass;
    }
    
    @SuppressWarnings("unchecked")
    private T readCreator(Creator<?> creator, Parcel p, ClassLoader loader) {
        if (creator instanceof ClassLoaderCreator<?>) {
            ClassLoaderCreator<?> classLoaderCreator = (ClassLoaderCreator<?>) creator;
            return (T) classLoaderCreator.createFromParcel(p, loader);
        }
        return (T) creator.createFromParcel(p);
    }
}