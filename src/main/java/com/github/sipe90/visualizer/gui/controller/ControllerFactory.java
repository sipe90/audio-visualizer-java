package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.util.Callback;

import java.lang.reflect.Constructor;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final AudioVisualizer app;
    private final AudioCapture capture;

    public ControllerFactory(AudioVisualizer app, AudioCapture capture) {
        this.app = app;
        this.capture = capture;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            Constructor<?> constructor = controllerClass.getConstructor(AudioVisualizer.class, AudioCapture.class);
            return  constructor.newInstance(app, capture);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create controller " + controllerClass, e);
        }
    }
}
