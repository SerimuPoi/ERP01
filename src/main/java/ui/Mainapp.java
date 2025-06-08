package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;

public class Mainapp extends Application {
    private void checkResources() {
        checkResource("Mainview.fxml", "/view/Mainview.fxml");
        checkResource("Style.css", "/Style.css");
    }

    private void checkResource(String name, String path) {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("❌ 资源未找到: " + name + " (路径: " + path + ")");
        } else {
            System.out.println("✅ 资源找到: " + name + " -> " + url);
        }
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            // 使用正确的资源路径
            URL fxmlUrl = getClass().getResource("/view/Mainview.fxml");
            if (fxmlUrl == null) {
                throw new RuntimeException("无法找到Mainview.fxml文件，请检查资源路径");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root, 1000, 700);

            // 加载CSS（确保路径正确）
            URL cssUrl = getClass().getResource("/Style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("警告：无法找到CSS文件");
            }

            primaryStage.setTitle("进销存管理系统");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // 显示错误对话框
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("启动错误");
            alert.setHeaderText("无法启动应用程序");
            alert.setContentText("错误原因: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        System.out.println("应用程序启动...");
        System.out.println("当前工作目录: " + System.getProperty("user.dir"));
        launch(args);
    }
}