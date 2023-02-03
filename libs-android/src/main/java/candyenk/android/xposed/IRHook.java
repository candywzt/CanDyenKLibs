package candyenk.android.xposed;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;

/**
 * 方法替换
 */
public interface IRHook extends IHook {
    /**
     * 预提供的空白方法
     */
    IRHook EMPTY = param -> null;


    Object hook(XC_MethodHook.MethodHookParam param) throws Throwable;

    @Override
    default XC_MethodHook hook() {
        return new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                return hook(param);
            }
        };
    }
}
