package candyenk.android.shizuku;

import androidx.annotation.NonNull;

/**
 * 帮助类
 * 为了连调
 */
public class ShizukuHelp {
    /**
     * UserServiceArgsBuilder
     *
     * @param pkg
     * @return
     */
    public static ArgsBuilder args(@NonNull String pkg) {
        return new ArgsBuilder(pkg);
    }
}
