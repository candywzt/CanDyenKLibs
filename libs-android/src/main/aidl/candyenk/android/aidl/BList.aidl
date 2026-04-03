package candyenk.android.aidl;
/**
 *
 * 通过IPC传输超长列表用
 * 因为系统自带的 ParceledListSlice 不可用
 * 自带AIDL文件
 */
parcelable BList<T>;
