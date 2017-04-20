package com.github.sipe90.visualizer.util;

import javafx.scene.control.Alert;

public class GuiUtil {

    public static void showInfoDialog(String title, String header, String content) {
        showDialog(Alert.AlertType.INFORMATION, title, header, content);
    }

    public static void showWarningDialog(String title, String header, String content) {
        showDialog(Alert.AlertType.WARNING, title, header, content);
    }

    private static void showDialog(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

}
