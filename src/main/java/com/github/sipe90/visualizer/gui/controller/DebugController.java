package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.google.common.primitives.Floats;
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
        spectrumContext = spectrumCanvas.getGraphicsContext2D();

        signalContext.setLineWidth(1.0d);

        drawBackgound();

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, this::updateCharts),
                new KeyFrame(UPDATE_FREQUENCY)
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCharts(ActionEvent actionEvent) {
        drawBackgound();
        if (!capture.isCapturing()) {
            return;
        }

        updateSpectrumCanvas();
        updateSignalCanvas();
    }

    private void drawBackgound() {
        final double zeroY = signalCanvas.getHeight() / 2;

        spectrumContext.setFill(Color.BLACK);
        signalContext.setFill(Color.BLACK);

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
    }

    private void updateSpectrumCanvas() {
        float[] bins = capture.getFFTBins();
        float[] amplitudes = capture.getFFTAmplitudes();

        assert bins.length == amplitudes.length;

        final float maxVal = Floats.max(amplitudes);
        final double spacing = 1.0d;
        final double barWidth = (spectrumCanvas.getWidth() - ((bins.length + 1) * spacing)) / bins.length;
        final double heightScale = spectrumCanvas.getHeight() / maxVal;

        spectrumContext.setFill(Color.RED);

        for (int i = 0; i < amplitudes.length; i++) {
            double startX = ((i + 1) * spacing + (i * barWidth));
            double heightY = amplitudes[i] * heightScale;
            double startY = spectrumCanvas.getHeight() - heightY;

            spectrumContext.fillRect(startX, startY, barWidth, heightY);
        }
    }

    private void updateSignalCanvas() {
        float[] buffer = capture.getBuffer();

        final double pointDistance = signalCanvas.getWidth() / buffer.length;
        final double zeroY = signalCanvas.getHeight() / 2;
        final double heightScale = zeroY / 1.0d;
        final double offsetY = 1.0d;

        signalContext.setStroke(Color.RED);

        signalContext.beginPath();
        for (int i = 0; i < buffer.length - 1; i++) {
            double startX = (i * pointDistance);
            double startY = (heightScale * (buffer[i] + offsetY));
            double endX = ((i + 1) * pointDistance);
            double endY = (heightScale * (buffer[i + 1] + offsetY));

            signalContext.moveTo(startX, startY);
            signalContext.lineTo(endX, endY);
        }
        signalContext.closePath();
        signalContext.stroke();
    }
}
