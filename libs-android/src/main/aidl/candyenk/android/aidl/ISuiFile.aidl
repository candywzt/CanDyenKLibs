package candyenk.android.aidl;

interface ISuiFile {
    //void destroy() = 16777114; // 由 Shizuku 服务器定义的 Destroy 方法
    //void exit() = 1; // 用户定义的退出方法
    
    //获取文件描述符,可读写
    ParcelFileDescriptor getFD(String path) = 201;
    //获取子文件列表
    String[] getChilds(String path) = 202;
    //是否为文件夹
    boolean isDirectory(String path) = 203;
    //获取文件最后修改日期
    long getLastModified(String path) = 204;
}
