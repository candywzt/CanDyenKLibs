package candyenk.android.shell;

/**
 * Shell命令执行器Success回调接口
 * 方便写lambda
 */
public interface ShellS extends ShellCB {
    @Override
    void onSuccess(byte[] msg);

    @Override
    default void onFailed(byte[] msg) {}

    @Override
    default void onOverTime() {}
}
