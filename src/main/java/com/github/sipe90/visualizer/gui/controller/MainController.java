package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.layout.VBox;

public class MainController extends WindowController {

    @FXML private CheckMenuItem debugCheck;

    @FXML private VBox deviceTab;
    @FXML private VBox fileTab;
    @FXML private VBox synthesisTab;

    @FXML private DeviceController deviceTabController;
    @FXML private FileController fileTabController;
    @FXML private SynthesisController synthesisTabController;

    public MainController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    @Override
    protected void init() {
        checkComponentInjected(debugCheck, CheckMenuItem.class, "debugCheck");

        checkComponentInjected(deviceTab, VBox.class, "deviceTab");
        checkComponentInjected(fileTab, VBox.class, "fileTab");
        checkComponentInjected(synthesisTab, VBox.class, "synthesisTab");
        checkComponentInjected(deviceTabController, DeviceController.class, "deviceTabController");
        checkComponentInjected(fileTabController, FileController.class, "fileTabController");
        checkComponentInjected(synthesisTabController, SynthesisController.class, "synthesisTabController");

        updateDebugCheck();
    }

    public final void reloadWindow(ActionEvent actionEvent) {
        app.reloadStages();
    }

    public void toggleDebug(ActionEvent actionEvent) {
        app.setDebug(debugCheck.isSelected());
    }

    public void updateDebugCheck() {
        debugCheck.setSelected(app.isDebug());
    }

    public void closeApp(ActionEvent actionEvent) {
        Platform.exit();
    }
}
