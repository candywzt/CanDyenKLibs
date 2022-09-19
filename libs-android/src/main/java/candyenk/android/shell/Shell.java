package candyenk.android.shell;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Shell命令执行器接口
 */
public interface Shell {
    int SH_SUCCESS = 1;
    int SH_FAILED = -1;
    int SH_OVERTIME = 0;
    List<Shell> shellList = new ArrayList<>();

    /**
     * 关闭所有有记录的Shell
     */
    static void closeAll() {
        for (Shell shell : shellList) {
            if (shell != null) shell.close();
        }
    }

    /**
     * 创建一个Handler的回调方法
     */
    static Handler.Callback createHandlerCallback(ShellCallBack callBack) {
        return msg -> {
            String message = (String) msg.obj;
            if (!callBack.callBack(msg.what, message)) {
                switch (msg.what) {
                    case SH_SUCCESS:
                        callBack.onSuccess(message);
                        break;
                    case SH_FAILED:
                        callBack.onFailed(message);
                        break;
                    case SH_OVERTIME:
                        callBack.onOverTime();
                        break;
                }
            }
            return true;
        };
    }

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
     * 获取当前Shell拥有的权限级别
     *
     * @return
     */
    String getShellPermissions();

    /**
     * 关闭命令执行器,释放所有资源
     */
    void close();


}
