package candyenk.android.shell;

import android.os.Handler;
import android.os.Looper;

/**
 * RootShell
 */
public class RootShell extends UserShell {
    public RootShell(Handler handler) {
        super(handler);
        this.TAG = RootShell.class.getSimpleName();
        this.startCmd[0] = "su";
    }

    public RootShell(ShellCallBack callBack) {
        this(new Handler(Looper.myLooper(), Shell.createHandlerCallback(callBack)));
    }

}
