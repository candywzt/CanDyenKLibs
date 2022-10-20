package candyenk.android.shell;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Shell命令执行器接口
 * 异常关闭可能产生缓存文件
 */
public interface Shell {
    /*** SH命令执行结果标志 ***/
    int CB_SUCCESS = 1;//SH命令执行成功
    int CB_FAILED = -1;//SH命令执行失败
    int CB_OVERTIME = 0;//SH命令执行超时
    /*** SH命令超时时间标志 ***/
    int OVER_NOW = -1;//立即结束,不需要等待返回值
    int OVER_DEFAULT = 5000;//默认超时时间
    int OVER_MAX = 360000;//允许设置的最大超时时间
    int OVER_INFINITY = 0;//不设置超时,永不关闭

    List<Shell> shellList = new ArrayList<>();


    /**
     * 准备初始化Shell
     *
     * @return 初始化结果
     */
    boolean ready();

    /**
     * 写入一条命令
     *
     * @return 写入成功与否
     */
    boolean writeCmd(String cmd);

    /**
     * 设置超时时间(毫秒)
     * 默认5000毫秒
     *
     * @param time 超时时间:
     *             -1:不调用命令结果回调,用于指令型命令
     *             0:保持Shell持续运行,调用close()手动结束
     *             >0:设定超时时间,超时无响应或触发回调则结束
     */
    void setOverTime(int time);


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
     * 创建一个Handler的回调方法
     */
    static Handler.Callback createHandlerCallback(ShellCallBack callBack) {
        return msg -> {
            byte[] message = (byte[]) msg.obj;
            if (!callBack.callBack(msg.what, message)) {
                switch (msg.what) {
                    case CB_SUCCESS:
                        callBack.onSuccess(new String(message));
                        break;
                    case CB_FAILED:
                        callBack.onFailed(new String(message));
                        break;
                    case CB_OVERTIME:
                        callBack.onOverTime();
                        break;
                }
            }
            return true;
        };
    }

}
