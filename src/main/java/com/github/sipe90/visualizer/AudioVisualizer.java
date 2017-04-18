package com.github.sipe90.visualizer;

import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioInputSource;
import com.github.sipe90.visualizer.capture.DataLineInputSource;
import com.github.sipe90.visualizer.util.AudioUtil;
import com.github.sipe90.visualizer.util.ByteRingBuffer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class AudioVisualizer extends Application {

    private Line.Info targetLineInfo = new Line.Info(TargetDataLine.class);

    private static Stage PRIMARY_STAGE;

    public static void main(String[] args) {
        //new AudioVisualizer().run();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        PRIMARY_STAGE = primaryStage;

        PRIMARY_STAGE.setTitle("Audio Visualizer");

        reloadStage();

        PRIMARY_STAGE.show();
    }

    public static void reloadStage() {
        try {
            URL windowFxml = AudioVisualizer.class.getResource("/window.fxml");
            Parent root = FXMLLoader.load(windowFxml);
            PRIMARY_STAGE.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void run() {

        List<Mixer> mixers = AudioUtil.getSupportedMixers();
        for (Mixer mixer : mixers) {
           System.out.println(mixer.getMixerInfo());
        }

        Mixer selected = mixers.get(1);

        System.out.println("Selected: " + selected.getMixerInfo());

        Line.Info[] lineInfo = selected.getTargetLineInfo(targetLineInfo);
        AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

        try {
            TargetDataLine line = (TargetDataLine) selected.getLine(lineInfo[0]);


            System.out.println("Frame size: " + format.getFrameSize());
            System.out.println("Frame rate: " + format.getFrameRate());

            AudioInputSource source = new DataLineInputSource(line, format);
            ByteRingBuffer buffer = new ByteRingBuffer(8192);

            AudioCapture capture = new AudioCapture(source, buffer, 32);

            capture.startCapture();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                capture.stopCapture();
            }

            System.out.println(buffer);

        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

}
