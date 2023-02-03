package candyenk.android.shell;

/**
 * Shell命令执行器回调接口
 * 需要那个重写哪个,方便快捷
 */
public interface ShellCallBack {
    /**
     * 消息处理器
     *
     * @param code 返回代码 :
     *             0:超时
     *             -1:错误
     *             1:成功
     * @param msg  返回的消息,超时消息为空字符串
     */
    void callBack(int code, byte[] msg);
}
