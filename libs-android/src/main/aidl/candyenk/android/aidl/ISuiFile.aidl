package candyenk.android.aidl;

interface ISuiFile {
    //void destroy() = 16777114; // 由 Shizuku 服务器定义的 Destroy 方法
    //void exit() = 1; // 用户定义的退出方法
    
    /**
     * 获取文件描述符
     * 用于获取文件IO流
     *
     * @param path 文件绝对路径
     * @param mode 文件访问模式
     * {@link candyenk.android.sui.SuiFile.MODE_READ_ONLY}:只读
     * {@link candyenk.android.sui.SuiFile.MODE_WRITE_ONLY}:只写
     * {@link candyenk.android.sui.SuiFile.MODE_READ_WRITE}:读写
     * {@link candyenk.android.sui.SuiFile.MODE_CREATE}:只创建
     * {@link candyenk.android.sui.SuiFile.MODE_TRUNCATE}:清空
     * {@link candyenk.android.sui.SuiFile.MODE_APPEND}:追加
     * @return 序列化文件描述符
     */
    ParcelFileDescriptor getFD(String path,int mode) = 201;
    
    /**
     * 获取该文件夹下子文件名集合
     * 子文件名并不是绝对路径
     * 需要自行添加前缀
     *
     * @param path 文件夹绝对路径
     * @return 子文件夹集合,不会为Null
     */
    String[] getChilds(String path) = 202;

    /**
     * 判断当前路径是否为文件夹
     * 路径为空或文件不存也返回false
     *
     * @param path 文件夹绝对路径
     * @return 是否为文件夹
     */
    boolean isDirectory(String path) = 203;

    /**
     * 返回文件最后修改时间
     * 路径为空或文件不存在则返回0
     *
     * @param path 文件绝对路径
     * @return 文件最后修改时间
     */
    long getLastModified(String path) = 204;
   
    /**
     * 获取文件大小
     * 文件不存在则为0
     *
     * @param path 文件绝对路径
     * @return 文件大小
     */
    long getSize(String path) = 205;
}
