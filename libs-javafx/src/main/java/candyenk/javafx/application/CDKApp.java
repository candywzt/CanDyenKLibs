package candyenk.javafx.application;

import candyenk.java.tools.R;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX的Application
 */
public abstract class CDKApp extends Application {
    private Stage mainStage = new Stage();
    /******************************************************************************************************************/
    /************************************************构造方法***********************************************************/
    /******************************************************************************************************************/
    public CDKApp() {
        if (!AM.addApp(this)) {
            throw new NullPointerException("重复的Application");
        }
    }
    /******************************************************************************************************************/
    /************************************************重写方法***********************************************************/
    /******************************************************************************************************************/
    @Override
    public void init() throws Exception {
        System.out.println("APP初始化:" + this.getClass().getSimpleName());
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("APP启动:" + this.getClass().getSimpleName());
        Scene scene = initLayout();
        scene.getStylesheets().add(R.getCss("cdk").toString());
        mainStage.setScene(scene);
        mainStage.setOnCloseRequest(event -> System.exit(0));
        mainStage.show();
        initContent();
        initEvent();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("APP停止:" + this.getClass().getSimpleName());
    }

    /******************************************************************************************************************/
    /************************************************私有方法***********************************************************/
    /******************************************************************************************************************/

    /******************************************************************************************************************/
    /************************************************默认方法***********************************************************/
    /******************************************************************************************************************/

    /******************************************************************************************************************/
    /************************************************公共方法***********************************************************/
    /******************************************************************************************************************/
    /**
     * 获取Stage
     */
    public Stage getStage() {
        return mainStage;
    }

    /**
     * 关闭当前页面
     */
    public void close() {
        mainStage.close();
    }
    /******************************************************************************************************************/
    /************************************************抽象方法***********************************************************/
    /******************************************************************************************************************/
    protected abstract Scene initLayout() throws Exception;

    protected abstract void initEvent() throws Exception;

    protected abstract void initContent() throws Exception;

}