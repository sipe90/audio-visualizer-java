package com.github.sipe90.visualizer.gui;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.util.AudioUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class Controller {

    private final AudioVisualizer app;
    private final AudioCapture capture;

    @FXML
    private ComboBox<Mixer> deviceCombo;
    @FXML
    private ListView<AudioFormat> formatList;
    @FXML
    private ListView<String> sampleRateList;
    @FXML
    private ListView<String> sampleSizeList;
    @FXML
    private ListView<String> channelsList;
    @FXML
    private Button captureButton;

    private boolean capturing = false;

    public Controller(AudioVisualizer app, AudioCapture capture) {
        this.app = app;
        this.capture = capture;
    }

    @FXML
    public void initialize() {

        assert deviceCombo != null : "ComboBox fx:id=\"deviceCombo\" was not injected: check your FXML file.";
        assert formatList != null : "ListView fx:id=\"formatList\" was not injected: check your FXML file.";
        assert sampleRateList != null : "Button fx:id=\"sampleRateList\" was not injected: check your FXML file.";
        assert sampleSizeList != null : "Button fx:id=\"sampleSizeList\" was not injected: check your FXML file.";
        assert channelsList != null : "Button fx:id=\"channelsList\" was not injected: check your FXML file.";
        assert captureButton != null : "Button fx:id=\"captureButton\" was not injected: check your FXML file.";

        initRenderers();

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
                // TODO: Error msg
                return;
            }
            if (sampleRateList.getSelectionModel().isEmpty()) {
                // TODO: Error msg
                return;
            }
            if (sampleSizeList.getSelectionModel().isEmpty()) {
                // TODO: Error msg
                return;
            }
            if (channelsList.getSelectionModel().isEmpty()) {
                // TODO: Error msg
                return;
            }

            Mixer mixer = deviceCombo.getSelectionModel().getSelectedItem();

            int selectedRateIdx = sampleRateList.getSelectionModel().getSelectedIndex();
            int selectedSizeIdx = sampleSizeList.getSelectionModel().getSelectedIndex();
            int selectedChannelsIdx = channelsList.getSelectionModel().getSelectedIndex();

            int selectedRate = AudioUtil.getSampleRates()[selectedRateIdx];
            int selectedSize = AudioUtil.getSampleSizes()[selectedSizeIdx];
            int selectedChannels = selectedChannelsIdx == 0 ? 1 : 2;

            System.out.println(mixer.getMixerInfo().getName() + ", " + selectedRate + ", " + selectedSize + ", " + selectedChannels);

            //TODO: Start capture
            System.out.println("Started capturing");

            capturing = true;
         } else {
            //TODO: Stop capture
            System.out.println("Stopped capturing");
            capturing = false;
        }

        captureButton.setText(capturing ? "Stop" : "Start");
    }

    public void reloadWindow(ActionEvent actionEvent) {
        app.reloadStage();
    }

    private void initRenderers() {
        deviceCombo.setButtonCell(new Components.DeviceListCell());
        deviceCombo.setCellFactory((comboBox) -> new Components.DeviceListCell());

        formatList.setCellFactory((comboBox) -> new Components.FormatListCell());
    }
}
