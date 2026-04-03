package candyenk.android.aidl;

interface ISuiPM {
    //void destroy() = 16777114; // 由 Shizuku 服务器定义的 Destroy 方法
    //void exit() = 1; // 用户定义的退出方法
    
    /**
     * 授予应用权限
     *
     * @param packageName 应用包名
     * @param uid         唯一应用UID
     * @param op          权限OP值
     */
    void grantPermission(String packageName,int uid,int op) = 11;
   
    /**
     * 撤销权限授予
     *
     * @param packageName 应用包名
     * @param uid         唯一应用UID
     * @param op          权限OP值
     */
    void revokePermission(String packageName,int uid,int op) = 12;
    
    
}
