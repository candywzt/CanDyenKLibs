package candyenk.javafx;

import candyenk.javafx.application.AM;
import candyenk.javafx.application.CDKApp;

public class CDK {
    /**
     * 启动新窗口并关闭旧窗口
     */
    public static <T extends CDKApp> void startApp(CDKApp o, Class<T> classz) {
        AM.closeApp(o.getClass());
        startApp(classz);
    }

    /**
     * 启动新窗口
     */
    public static <T extends CDKApp> void startApp(Class<T> classz) {
        try {
            T app = classz.getDeclaredConstructor().newInstance();
            app.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
