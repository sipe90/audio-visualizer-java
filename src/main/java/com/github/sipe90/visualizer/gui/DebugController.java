package com.github.sipe90.visualizer.gui;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioCaptureException;
import com.github.sipe90.visualizer.util.AudioUtil;
import com.github.sipe90.visualizer.util.GuiUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class DebugController {

    private final AudioVisualizer app;
    private final AudioCapture capture;

    public DebugController(AudioVisualizer app, AudioCapture capture) {
        this.app = app;
        this.capture = capture;
    }

    @FXML
    public void initialize() {}

    public void reloadWindow(ActionEvent actionEvent) {
        app.reloadDebugStage();
    }

}
