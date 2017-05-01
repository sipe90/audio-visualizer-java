package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioCaptureException;
import com.github.sipe90.visualizer.gui.Components;
import com.github.sipe90.visualizer.util.AudioUtil;
import com.github.sipe90.visualizer.util.GuiUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class DeviceController extends WindowController {

    @FXML private ComboBox<Mixer> deviceCombo;
    @FXML private ListView<AudioFormat> formatList;
    @FXML private ListView<String> sampleRateList;
    @FXML private ListView<String> sampleSizeList;
    @FXML private ListView<String> channelsList;
    @FXML private Button captureButton;

    private boolean capturing = false;

    public DeviceController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
        capturing = capture.isCapturing();
    }

    @Override
    protected void init() {

        assert deviceCombo != null : "ComboBox fx:id=\"deviceCombo\" was not injected: check your FXML file.";
        assert formatList != null : "ListView fx:id=\"formatList\" was not injected: check your FXML file.";
        assert sampleRateList != null : "Button fx:id=\"sampleRateList\" was not injected: check your FXML file.";
        assert sampleSizeList != null : "Button fx:id=\"sampleSizeList\" was not injected: check your FXML file.";
        assert channelsList != null : "Button fx:id=\"channelsList\" was not injected: check your FXML file.";
        assert captureButton != null : "Button fx:id=\"captureButton\" was not injected: check your FXML file.";

        initRenderers();

        updateCaptureButton();

        deviceCombo.getItems().addAll(AudioUtil.getSupportedMixers());

        for (int rate : AudioUtil.getSampleRates()) {
            sampleRateList.getItems().add(rate + " Hz");
        }

        for (int size : AudioUtil.getSampleSizes()) {
            sampleSizeList.getItems().add(size + " bit");
        }

        channelsList.getItems().addAll("Mono", "Stereo");

        deviceCombo.getSelectionModel().selectFirst();
        deviceCombo.getOnAction().handle(null);

        sampleRateList.getSelectionModel().selectFirst();
        sampleSizeList.getSelectionModel().selectFirst();
        channelsList.getSelectionModel().selectFirst();
    }

    private void updateCaptureButton() {
        captureButton.setText(capturing ? "Stop" : "Start");
    }

    public void selectDevice(ActionEvent actionEvent) {
        Mixer selected = deviceCombo.getSelectionModel().getSelectedItem();

        formatList.getItems().clear();

        for (Line.Info info : selected.getTargetLineInfo()) {
            for (AudioFormat format : ((DataLine.Info)info).getFormats()) {
                formatList.getItems().add(format);
            }
        }
    }

    public void toggleCapture(ActionEvent actionEvent) {

        if (!capturing) {
            if (deviceCombo.getSelectionModel().isEmpty()) {
                GuiUtil.showInfoDialog("Could not start capture", null, "Please select input device.");
                return;
            }
            if (sampleRateList.getSelectionModel().isEmpty()) {
                GuiUtil.showInfoDialog("Could not start capture", null, "Please select sample rate.");
                return;
            }
            if (sampleSizeList.getSelectionModel().isEmpty()) {
                GuiUtil.showInfoDialog("Could not start capture", null, "Please select the sample size.");
                return;
            }
            if (channelsList.getSelectionModel().isEmpty()) {
                GuiUtil.showInfoDialog("Could not start capture", null, "Please select the amount of channels.");
                return;
            }

            Mixer mixer = deviceCombo.getSelectionModel().getSelectedItem();

            int selectedRateIdx = sampleRateList.getSelectionModel().getSelectedIndex();
            int selectedSizeIdx = sampleSizeList.getSelectionModel().getSelectedIndex();
            int selectedChannelsIdx = channelsList.getSelectionModel().getSelectedIndex();

            int selectedRate = AudioUtil.getSampleRates()[selectedRateIdx];
            int selectedSize = AudioUtil.getSampleSizes()[selectedSizeIdx];
            int selectedChannels = selectedChannelsIdx == 0 ? 1 : 2;

            try {
                capture.startCapture(mixer, selectedRate, selectedSize, selectedChannels);
                capturing = true;
            } catch (AudioCaptureException e) {
                GuiUtil.showWarningDialog("Could not start capture", null, e.getLocalizedMessage());
            }

         } else {
            capture.stopCapture();
            capturing = false;
        }

        updateCaptureButton();
    }

    private void initRenderers() {
        deviceCombo.setButtonCell(new Components.DeviceListCell());
        deviceCombo.setCellFactory((comboBox) -> new Components.DeviceListCell());

        formatList.setCellFactory((comboBox) -> new Components.FormatListCell());
    }
}
