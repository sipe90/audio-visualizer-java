package com.github.sipe90.visualizer.gui.controller;

import com.github.sipe90.visualizer.AudioVisualizer;
import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioCaptureException;
import com.github.sipe90.visualizer.util.GuiUtil;
import com.google.common.base.Strings;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;

public class FileController extends WindowController {

    @FXML private Button fileChooserButton;
    @FXML private TextField filePathTextField;
    @FXML private Button captureButton;
    @FXML private Slider volumeSlider;

    private final FileChooser fileChooser = new FileChooser();

    private boolean capturing = false;

    public FileController(AudioVisualizer app, AudioCapture capture) {
        super(app, capture);
    }

    @Override
    protected void init() {
        configureFileChooser();
    }

    private void configureFileChooser() {
        fileChooser.setTitle("Select audio file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Supported formats", "*.wav", "*.wave", "*.mp3", "*.ogg", "*.ogv,", "*.oga", "*.ogx", "*.ogm", "*.spx", "*.opus", "*.aiff", "*.au", "*.snd"),
                new FileChooser.ExtensionFilter("Waveform Audio File Format (WAVE/WAV)", "*.wav", "*.wave"),
                new FileChooser.ExtensionFilter("MPEG-2 Layer 3 (MP3)", "*.mp3"),
                new FileChooser.ExtensionFilter("Ogg Vorbis/Opus (OGG/OPUS)", "*.ogg", "*.ogv,", "*.oga", "*.ogx", "*.ogm", "*.spx", "*.opus"),
                new FileChooser.ExtensionFilter("Audio Interchange File Format (AIFF)", "*.aiff"),
                new FileChooser.ExtensionFilter("Au file format (AU/SND)", "*.au", "*.snd"),
                new FileChooser.ExtensionFilter("All Files (Try your luck)", "*.*")
        );
    }

    public void openFileChooser(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(fileChooserButton.getScene().getWindow());
        if (file == null) {
            return;
        }
        filePathTextField.setText(file.getAbsolutePath());
    }

    public void toggleCapture(ActionEvent actionEvent) {
        if (!capturing) {
            String filePath = filePathTextField.getText();
            if (Strings.isNullOrEmpty(filePath)) {
                GuiUtil.showWarningDialog("Could not start capture", null, "Please select an audio file first.");
                return;
            }
            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                GuiUtil.showWarningDialog("Could not start capture", null, "File does not exist or cannot be read.");
                return;
            }

            try {
                capture.startCapture(file, volumeSlider.getValue() / 100, (v) -> {
                    Platform.runLater(() -> setCapturing(false));
                    return null;
                });
                setCapturing(true);
            } catch (AudioCaptureException e) {
                GuiUtil.showWarningDialog("Could not start capture", null, e.getLocalizedMessage());
            }

        } else {
            capture.stopCapture();
            setCapturing(false);
        }
    }

    private void setCapturing(boolean capturing) {
        this.capturing = capturing;
        updateCaptureButton();
    }

    public void adjustVolume(MouseEvent mouseEvent) { capture.setVolume(volumeSlider.getValue() / 100); }

    private void updateCaptureButton() {
        captureButton.setText(capturing ? "Stop" : "Start");
    }
}
