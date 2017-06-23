package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class DebugController extends WindowController {

    private Timeline timeline;
    private static final Duration UPDATE_FREQUENCY = Duration.millis(20);

    @FXML private Canvas signalCanvas;
    @FXML private Canvas spectrumCanvas;

    private GraphicsContext signalContext;
    private GraphicsContext spectrumContext;

    public DebugController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    @Override
    protected void init() {
        signalContext = signalCanvas.getGraphicsContext2D();
        signalContext.setStroke(Color.RED);
        signalContext.setLineWidth(1.0d);
        signalContext.setFill(Color.BLACK);
        signalContext.fillRect(0.0d, 0.0d, signalCanvas.getWidth(), signalCanvas.getHeight());

        spectrumContext = spectrumCanvas.getGraphicsContext2D();
        spectrumContext.setStroke(Color.RED);
        spectrumContext.setLineWidth(1.0d);
        spectrumContext.setFill(Color.BLACK);
        spectrumContext.fillRect(0.0d, 0.0d, signalCanvas.getWidth(), signalCanvas.getHeight());

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, this::updateCharts),
                new KeyFrame(UPDATE_FREQUENCY)
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCharts(ActionEvent actionEvent) {

        final double zeroY = signalCanvas.getHeight() / 2;

        signalContext.clearRect(0, 0, signalCanvas.getWidth(), signalCanvas.getHeight());
        signalContext.fillRect(0.0d, 0.0d, signalCanvas.getWidth(), signalCanvas.getHeight());
        spectrumContext.clearRect(0, 0, signalCanvas.getWidth(), signalCanvas.getHeight());
        spectrumContext.fillRect(0.0d, 0.0d, signalCanvas.getWidth(), signalCanvas.getHeight());

        signalContext.setStroke(Color.GRAY);

        signalContext.beginPath();
        signalContext.moveTo(0, zeroY);
        signalContext.lineTo( signalCanvas.getWidth(), zeroY);
        signalContext.stroke();
        signalContext.closePath();

        if (!capture.isCapturing()) {
            return;
        }

        updateSpectrumCanvas();
        updateSignalCanvas();
    }

    private void updateSpectrumCanvas() {
        float[] bins = capture.getFFTBins();
        float[] amplitudes = capture.getFFTAmplitudes();

        assert  bins.length == amplitudes.length;

        spectrumContext.beginPath();
        for (int i = 0; i < amplitudes.length; i++) {

        }
        spectrumContext.closePath();
        spectrumContext.stroke();
    }

    private void updateSignalCanvas() {
        float[] buffer = capture.getBuffer();

        final double pointDistance = signalCanvas.getWidth() / buffer.length;
        final double zeroY = signalCanvas.getHeight() / 2;
        final double heightScale = zeroY / 1.0d;

        signalContext.setStroke(Color.RED);

        signalContext.beginPath();
        for (int i = 0; i < buffer.length - 1; i++) {
            int startX = (int)(i * pointDistance);
            int startY = (int)(heightScale * (buffer[i] + 1.0d));
            int endX = (int)((i + 1) * pointDistance);
            int endY = (int)(heightScale * (buffer[i + 1] + 1.0d));

            signalContext.moveTo(startX, startY);
            signalContext.lineTo(endX, endY);
        }
        signalContext.closePath();
        signalContext.stroke();
    }
}
