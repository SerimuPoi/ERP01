package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

public class Maincontroller {
    @FXML private TabPane tabPane;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        // 初始化代码
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}