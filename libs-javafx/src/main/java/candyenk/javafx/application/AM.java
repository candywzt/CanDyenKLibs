package candyenk.javafx.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX的AppLication管理器
 */
public class AM {
    private static final List<Class<? extends CDKApp>> classList = new ArrayList<>();
    private static final Map<Class<? extends CDKApp>, CDKApp> appMap = new HashMap<>();
    /*****************************************************************************************************************/
    /*******************************************默认静态方法************************************************************/
    /*****************************************************************************************************************/
    static boolean addApp(CDKApp app) {
        Class<? extends CDKApp> classz = app.getClass();
        if (!classList.contains(classz)) {
            classList.add(classz);
            appMap.put(classz, app);
            return true;
        }
        return false;
    }
    /*****************************************************************************************************************/
    /*******************************************公共静态方法************************************************************/
    /*****************************************************************************************************************/
    /**
     * 获取当前APP实例列表
     */
    public static Map<Class<? extends CDKApp>, ? extends CDKApp> getAppMap() {
        return Map.copyOf(appMap);
    }

    public static List<Class<? extends CDKApp>> getAppList() {
        return List.copyOf(classList);
    }

    /**
     * 获取指定类APP实例
     */
    public static <T extends CDKApp> T INSTANCE(Class<T> classz) {
        return (T) appMap.get(classz);
    }

    /**
     * 获取当前实例
     */
    public static <T extends CDKApp> T getCurrentApp(Class<T> classz) {
        return (T) getCurrentApp();
    }

    public static CDKApp getCurrentApp() {
        return appMap.get(classList.get(classList.size() - 1));
    }

    /**
     * 获取上一个APP实例
     */
    public static <T extends CDKApp> T getLastApp(Class<T> classz) {
        return (T) getLastApp();
    }

    public static CDKApp getLastApp() {
        if (classList.size() > 2) {
            return appMap.get(classList.get(classList.size() - 2));
        }
        return null;
    }

    /**
     * 关闭指定实例
     */
    public static boolean closeApp(Class<? extends CDKApp> classz) {
        CDKApp app = INSTANCE(classz);
        app.close();
        boolean b1 = appMap.remove(classz, app);
        boolean b2 = classList.remove(classz);
        return b1 && b2;
    }
}
