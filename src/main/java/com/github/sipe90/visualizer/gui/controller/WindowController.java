package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.fxml.FXML;

public abstract class WindowController {

    protected AudioVisualizer app;
    protected AudioCapture capture;

    public WindowController(AudioVisualizer app, AudioCapture capture) {
        this.app = app;
        this.capture = capture;
    }

    @FXML
    public final void initialize() {
        init();
    }

    protected final void checkComponentInjected(Object component, Class<?> componentClass, String fxId) {
        assert component != null : componentClass.getSimpleName() + " fx:id=\"" + fxId +"\" was not injected: check your FXML file.";
    }

    protected abstract void init();

}
