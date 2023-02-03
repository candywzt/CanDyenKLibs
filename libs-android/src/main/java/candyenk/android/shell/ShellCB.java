package candyenk.android.shell;

/**
 * Shell命令执行器回调接口
 * 方便写lambda
 */
public interface ShellCB extends ShellCallBack {
    @Override
    default void callBack(int code, byte[] msg) {
        switch (code) {
            case Shell.CB_SUCCESS: onSuccess(msg); break;
            case Shell.CB_FAILED: onFailed(msg); break;
            case Shell.CB_OVERTIME: onOverTime(); break;
        }
    }

    /**
     * 成功回调
     */
    void onSuccess(byte[] msg);

    /**
     * 失败回调
     */
    void onFailed(byte[] msg);

    /**
     * 超时回调
     */
    void onOverTime();

}
