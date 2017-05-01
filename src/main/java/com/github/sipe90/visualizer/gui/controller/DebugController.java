package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.util.Duration;

public class DebugController extends WindowController {

    private BarChart.Series<String, Float> series;
    private Timeline timeline;
    private static final Duration UPDATE_FREQUENCY = Duration.millis(20);

    @FXML private BarChart<String, Float> spectrumChart;
    @FXML private LineChart<Float, Float> signalChart;
    // @FXML private CategoryAxis xAxis;
    // @FXML private NumberAxis yAxis;

    public DebugController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    @Override
    protected void init() {
        series = new BarChart.Series<>();
        spectrumChart.getData().add(series);

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, this::updateCharts),
                new KeyFrame(UPDATE_FREQUENCY)
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateCharts(ActionEvent actionEvent) {
        if (!capture.isCapturing()) {
            series.getData().clear();
            return;
        }

        updateSpectrumChart();
        updateSignalChart();
    }

    private void updateSpectrumChart() {
        float[] bins = capture.getFFTBins();
        float[] amplitudes = capture.getFFTAmplitudes();

        assert  bins.length == amplitudes.length;

        series.getData().clear();
        for (int i = 0; i < amplitudes.length; i++) {
            series.getData().add(new BarChart.Data<>(String.valueOf(bins[i]) + " Hz", amplitudes[i]));
        }
    }

    private void updateSignalChart() {

    }

}
