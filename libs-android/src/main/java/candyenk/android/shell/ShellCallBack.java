package candyenk.android.shell;

/**
 * Shell命令执行器回调接口
 * 需要那个重写哪个,方便快捷
 */
public interface ShellCallBack {

    /**
     * 第一层消息处理器
     *
     * @param code 返回代码 :
     *             0:超时
     *             -1:错误
     *             1:成功
     * @param msg  返回的消息,超时消息为空字符串
     * @return 返回true则不进行下一步处理(不调用以下三个方法)
     */
    default boolean callBack(int code, byte[] msg) {
        return false;
    }

    /**
     * 成功回调
     */
    default void onSuccess(String msg) {
    }

    /**
     * 失败回调
     */
    default void onFailed(String msg) {
    }

    /**
     * 超时回调
     */
    default void onOverTime() {
    }

}
