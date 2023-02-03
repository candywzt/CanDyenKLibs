package candyenk.android.shizuku;

import android.content.ComponentName;
import androidx.annotation.NonNull;
import candyenk.android.tools.L;
import rikka.shizuku.Shizuku;

/**
 * 绑定帮助类
 */
public class ArgsBuilder {
    private static final String TAG = ArgsBuilder.class.getSimpleName();
    private Shizuku.UserServiceArgs args;
    private final String pkg;

    ArgsBuilder(@NonNull String pkg) {
        this.pkg = pkg;
    }

    /**
     * 绑定在Shizuku中
     */
    public Shizuku.UserServiceArgs build() {
        return args;
    }

    /**
     * 设置目标类名
     * Class.getName()
     */
    public ArgsBuilder target(@NonNull String cls) {
        this.args = new Shizuku.UserServiceArgs(new ComponentName(pkg, cls));
        suffix("service");
        return this;
    }

    /**
     * 是否启用守护模式
     * {@link Shizuku.UserServiceArgs#daemon(boolean)}
     */
    public ArgsBuilder daemon(boolean daemon) {
        if (args == null) return L.e(TAG, "请先调用target", this);
        args.daemon(daemon);
        return this;
    }

    /**
     * 标签
     */
    public ArgsBuilder tag(@NonNull String tag) {
        if (args == null) return L.e(TAG, "请先调用target", this);
        args.tag(tag);
        return this;
    }

    /**
     * 版本代码
     * 修改一次代码更新一次版本号
     * 免得调用上次的代码
     */
    public ArgsBuilder version(int v) {
        if (args == null) return L.e(TAG, "请先调用target", this);
        args.version(v);
        return this;
    }

    /**
     * 设置进程为Debug
     * 只能在显示所有进程中看到
     */
    public ArgsBuilder debug(boolean b) {
        if (args == null) return L.e(TAG, "请先调用target", this);
        args.debuggable(b);
        return this;
    }

    /**
     * 进程后缀
     * 默认service
     */
    public ArgsBuilder suffix(String s) {
        if (args == null) return L.e(TAG, "请先调用target", this);
        args.processNameSuffix(s);
        return this;
    }

}
