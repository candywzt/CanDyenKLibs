# CanDyenKLibs

自用工具库

基于JDK 21

`candyenk.java.io`包
> `IO` 文件IO操作工具类\
> `NIO`文件NIO操作工具类\
> `FileInfo`描述文件基本信息的类\
> `FileType`描述文件类型的枚举类\
> 目前仅用于文件选择器

`candyenk.java.tools`包
> `JS`Java序列化帮助类\
> `L`Java日志工具类（未实现）\
> `R`Java资源工具类\
> `S`Java包多语言工具（未实现）\
> `T`Java耗时操作工具类

`candyenk.java.utils`包
> `RSA`RSA算法帮助类（未完成）\

`candyenk.java.i18n`包
> `Language` 语言条目类\
> `LanguageInterface` 语言条目文件接口\
> `LanguageTable`多层嵌套语言条目类

`candyenk.android.aidl`包
> `BList`用于IPC跨进程通讯传输超长List，不适用于单个超大对象

`candyenk.android.base`包
>`ActivityCDK` CDK专用activity抽象类
> `ActivityCrash`CDK专用崩溃抓捕Activity
> `AdapterRVCDK` CDK专用列表适配器抽象类
> `ApplicationCDK` CDK专用Application类
> `FragmentCDK` CDK专用Fragment抽象类
> `HolderCDK`CDK专用View构造器
> `ServiceWindowsCDK` 啥也不是