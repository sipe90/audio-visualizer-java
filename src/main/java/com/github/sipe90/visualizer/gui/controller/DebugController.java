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

    private XYChart.Series<Float, Float> spectrumSeries;

    private Timeline timeline;
    private static final Duration UPDATE_FREQUENCY = Duration.millis(20);

    @FXML private LineChart<Float, Float> spectrumChart;
    @FXML private Canvas signalCanvas;

    private GraphicsContext signalContext;

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

        spectrumSeries = new LineChart.Series<>();
        spectrumChart.getData().add(spectrumSeries);
        spectrumSeries.getNode().setStyle("-fx-stroke-width: 1;");

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, this::updateCharts),
                new KeyFrame(UPDATE_FREQUENCY)
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCharts(ActionEvent actionEvent) {
        if (!capture.isCapturing()) {
            spectrumSeries.getData().clear();
            return;
        }

        // updateSpectrumChart();
        updateSignalChart();
    }

    private void updateSpectrumChart() {
        float[] bins = capture.getFFTBins();
        float[] amplitudes = capture.getFFTAmplitudes();

        assert  bins.length == amplitudes.length;

        adjustDatapoints(spectrumSeries, bins.length);
        adjustAxisRange((ValueAxis)spectrumChart.getXAxis(), 0.0d,  (double)bins[bins.length - 1]);

        for (int i = 0; i < amplitudes.length; i++) {
            XYChart.Data<Float, Float> dataPoint = spectrumSeries.getData().get(i);
            dataPoint.setXValue(bins[i]);
            dataPoint.setYValue(amplitudes[i]);
        }
    }

    private void updateSignalChart() {
        float[] buffer = capture.getBuffer();

        final double pointDistance = signalCanvas.getWidth() / buffer.length;
        final double zeroY = signalCanvas.getHeight() / 2;
        final double heightScale = zeroY / 1.0d;

        signalContext.clearRect(0, 0, signalCanvas.getWidth(), signalCanvas.getHeight());
        signalContext.fillRect(0.0d, 0.0d, signalCanvas.getWidth(), signalCanvas.getHeight());

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

    private <X, Y> void adjustDatapoints(LineChart.Series<X, Y> series, int newSize) {
        int dataPoints = series.getData().size();
        if (dataPoints == newSize) {
            return;
        }

        int sizeAdjustment = newSize - dataPoints;
        if (sizeAdjustment > 0) {
            for (int i = 0; i < sizeAdjustment; i++) {
                series.getData().add(new XYChart.Data<>());
            }
        } else {
            int toRemove = -sizeAdjustment;
            series.getData().remove(dataPoints - toRemove - 1, dataPoints - 1);
        }
    }

    private <T extends  Number> void adjustAxisRange(ValueAxis<T> axis, double min, double max) {
        axis.setLowerBound(min);
        axis.setUpperBound(max);
    }

}
