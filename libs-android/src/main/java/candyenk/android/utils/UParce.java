package candyenk.android.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Android Object序列化工具
 */
public class UParce {
    /**********************************************************************************************/
    /**
     * 序列化实现接口Parcelable的对象
     */
    public static byte[] serialize(Parcelable parcelable) {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    /**
     * 反序列化实现接口Parcelable的对象
     */
    public static <T> T unserialize(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        return creator.createFromParcel(parcel);
    }
}
