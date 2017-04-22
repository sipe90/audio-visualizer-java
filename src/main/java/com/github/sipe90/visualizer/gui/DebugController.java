package com.github.sipe90.visualizer.gui;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioCaptureException;
import com.github.sipe90.visualizer.util.AudioUtil;
import com.github.sipe90.visualizer.util.GuiUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class DebugController {

    private final AudioVisualizer app;
    private final AudioCapture capture;

    private BarChart.Series<String, Float> series;
    private Timeline timeline;
    private static final Duration UPDATE_FREQUENCY = Duration.millis(20);

    @FXML private BarChart<String, Float> spectrumChart;
    // @FXML private CategoryAxis xAxis;
    // @FXML private NumberAxis yAxis;

    public DebugController(AudioVisualizer app, AudioCapture capture) {
        this.app = app;
        this.capture = capture;
    }

    @FXML
    public void initialize() {
        series = new BarChart.Series<>();
        spectrumChart.getData().add(series);

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, this::updateChart),
                new KeyFrame(UPDATE_FREQUENCY)
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateChart(ActionEvent actionEvent) {
        if (!capture.isCapturing()) {
            series.getData().clear();
            return;
        }

        float[] bins = capture.getFFTBins();
        float[] amplitudes = capture.getFFTAmplitudes();

        assert  bins.length == amplitudes.length;

        series.getData().clear();
        for (int i = 0; i < amplitudes.length; i++) {
            series.getData().add(new BarChart.Data<>(String.valueOf(bins[i]) + " Hz", amplitudes[i]));
        }
    }

    public void reloadWindow(ActionEvent actionEvent) {
        app.reloadDebugStage();
    }

}
