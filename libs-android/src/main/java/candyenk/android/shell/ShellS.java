package candyenk.android.shell;

/**
 * Shell命令执行器Success回调接口
 * 方便写lambda
 */
public interface ShellS extends ShellCallBack {
    void onSuccess(String msg);
}
