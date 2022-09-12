package candyenk.javafx.pane;

import candyenk.java.tools.R;
import candyenk.javafx.application.AM;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;


public class WindowTitle extends HBox {
    public Button b1, b2, closeView;//右侧三个按钮
    public Label titleView;//左侧标题
    private Stage stage;

    public WindowTitle(Stage stage) {
        super();
        this.stage = stage;
        initNode();
        initStyle();
        initEvent();
    }

    private void initStyle() {
        getStyleClass().setAll("windowtitle");
    }

    private void initNode() {
        titleView = createTitleView();
        b1 = createButton(createImageView("ip"));
        b2 = createButton(createImageView("ip"));
        closeView = createButton(createImageView("ip"));
        getChildren().addAll(titleView, b1, b2, closeView);
        setAlignment(Pos.CENTER_LEFT);
    }

    private void initEvent() {
        closeView.setOnAction(event -> {
            //stage.close();
            System.exit(0);
        });
        b1.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("\nAPP列表:");
            AM.getAppList().forEach(aClass -> {
                sb.append(aClass.toString()).append("\n");
            });
            sb.append("\nAPPMap:");
            AM.getAppMap().forEach((aClass, app) -> {
                sb.append(aClass.toString()).append(":").append(app).append("\n");
            });
            sb.append("\n当前APP:").append(AM.getCurrentApp()).append("\n");
            sb.append("\n上一个APP").append(AM.getLastApp());

            System.out.println(sb);
        });
        b2.setOnAction(e -> {
            System.out.println(AM.getCurrentApp());
        });
    }

    private Label createTitleView() {
        ImageView iv = createImageView("ip");
        Label label = new Label("应用程序", iv);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setPadding(new Insets(5, 5, 5, 5));
        setHgrow(label, Priority.ALWAYS);
        return label;
    }

    private Button createButton(ImageView iv) {
        Button button = new Button();
        button.setPadding(new Insets(5, 5, 5, 5));
        button.graphicProperty().set(iv);
        return button;
    }

    private ImageView createImageView(String fileName) {
        ImageView iv = new ImageView(R.getIcon(fileName).toString());
        iv.setFitHeight(20);
        iv.setFitWidth(20);
        return iv;
    }
    /******************************************************************************************************************/
    /******************************************公共方法*****************************************************************/
    /******************************************************************************************************************/
    /**
     * 设置CSS属性文件
     *
     * @param fileName css文件名(位于jar包/resources/css目录)
     */
    public void setCss(String fileName) {
        getStylesheets().add(R.getCss(fileName).toString());
    }

    /**
     * 设置标题与图标
     */
    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(Node node) {
        titleView.graphicProperty().set(node);
    }

    public void setTitle(String title, Node node) {
        setTitle(title);
        setTitle(node);
    }
}
