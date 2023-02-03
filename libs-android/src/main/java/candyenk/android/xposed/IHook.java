package candyenk.android.xposed;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Hook标记接口
 */
public interface IHook {
    XC_MethodHook hook();
}
