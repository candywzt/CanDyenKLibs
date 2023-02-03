package candyenk.android.shell;

/**
 * RootShell
 */
public class RootShell extends UserShell {
    public RootShell() {
        super();
    }


    public RootShell(ShellCallBack callBack) {
        super(callBack);
    }

    @Override
    protected String startCmd() {
        return "su";
    }

    @Override
    protected void allowRun(String cmd) {
    }
}
