package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.event.ActionEvent;

public class SynthesisController extends WindowController {

    public SynthesisController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    @Override
    protected void init() {

    }

    public void toggleCapture(ActionEvent actionEvent) {
    }
}
