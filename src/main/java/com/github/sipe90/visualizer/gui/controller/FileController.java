package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

public class FileController extends WindowController {

    @FXML
    private Button selectFileButton;

    private final FileChooser fileChooser = new FileChooser();

    public FileController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    public void openFileBrowser(ActionEvent event) {
        // fileChooser.showOpenDialog();
    }

    @Override
    protected void init() {

    }
}
