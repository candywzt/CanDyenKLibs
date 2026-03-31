package com.candyenk.demo;

import candyenk.android.aidl.BList;

interface IUserService {
    //void destroy() = 16777114; // 由 Shizuku 服务器定义的 Destroy 方法
    //void exit() = 1; // 用户定义的退出方法


    //获取已安装应用列表
    BList<PackageInfo> getPackages()=10;
    
    //获取指定应用所有意图过滤器
    BList<IntentFilter> getAllIntentFilters(String packageName) = 11;
    
    //获取第几个IntentFilter的String
    String toLongString(int index) = 12;
   
    

    
}