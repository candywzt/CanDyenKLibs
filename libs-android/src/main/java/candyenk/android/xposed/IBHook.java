package candyenk.android.xposed;

import de.robv.android.xposed.XC_MethodHook;

/**
 * 方法调用前Before
 */
public interface IBHook extends IHook {
    void hook(XC_MethodHook.MethodHookParam param) throws Throwable;

    @Override
    default XC_MethodHook hook() {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hook(param);
            }
        };
    }
}
