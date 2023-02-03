package candyenk.android.shell;

import java.util.ArrayList;
import java.util.List;

/**
 * Shell命令执行器接口
 * 异常关闭可能产生缓存文件
 * Shell频繁创建很耗时耗资源
 */
public interface Shell {
    /*** SH命令执行结果标志 ***/
    int CB_SUCCESS = 1;//SH命令执行成功
    int CB_FAILED = -1;//SH命令执行失败
    int CB_OVERTIME = 0;//SH命令执行超时
    /*** SH命令超时时间标志 ***/
    int OT_NOW = -1;//立即结束,不需要等待返回值
    int OT_DEFAULT = 5000;//默认超时时间
    int OT_MAX = 360000;//允许设置的最大超时时间
    int OT_INFINITY = 0;//不设置超时,永不关闭
    /*** 退出指令 ***/
    byte[] exit = {101, 120, 105, 116, 10};//退出指令

    List<Shell> shellList = new ArrayList<>();


    /**
     * 准备初始化Shell
     * 已初始化直接返回true
     *
     * @return 初始化结果
     */
    boolean ready();

    /**
     * 当前Shell是否以初始化或尚未回收
     */
    boolean isReady();

    /**
     * 设置命令回调
     * 繁忙状态不可修改
     */
    Shell setCallBack(ShellCallBack cb);

    /**
     * 写入一条命令
     *
     * @return 写入成功与否
     */
    boolean writeCmd(String cmd);

    /**
     * 只写入指令
     * 和overTime = -1效果相同
     *
     * @return 写入成功与否
     */
    boolean writeOnly(String cmd);

    /**
     * 设置单条命令执行超时时间(毫秒)
     * 默认5000毫秒
     * 在ready之前设置,ready后不可修改
     *
     * @param time 超时时间:
     *             -1:无视命令输出,用于指令型命令
     *             0:永不超时
     *             >0:设定超时时间,超时无响应则触发超时回调
     */
    Shell setOverTime(int time);

    /**
     * 获取当前设置超时时间
     */
    long getOverTime();

    /**
     * 关闭命令执行器,释放所有资源
     * 最好弄完了执行一下,不然垃圾缓存到处都是
     */
    void close();

    /**
     * 记录当前Shell实例
     */
    default void registerShell() {
        if (!shellList.contains(this)) shellList.add(this);
    }

    /**
     * 关闭所有有记录的Shell
     */
    static void closeAll() {
        for (Shell shell : shellList) {
            if (shell != null) shell.close();
        }
        shellList.clear();
    }


    /**
     * 休眠
     */
    static void sleep(long time) {
        try {Thread.sleep(time);} catch (InterruptedException ignored) {}
    }

}
