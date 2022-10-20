package candyenk.android.shell;

/**
 * Shell命令执行器回调接口
 * 方便写lambda
 */
public interface ShellCB extends ShellCallBack {
    /**
     * 消息处理器
     *
     * @param code 返回代码 :
     *             0:超时
     *             -1:错误
     *             1:成功
     * @param msg  返回的消息,超时消息为空字符串
     */
    void CB(int code, byte[] msg);

    @Override
    default boolean callBack(int code, byte[] msg) {
        CB(code, msg);
        return true;
    }
}
