package com.github.sipe90.visualizer;

import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.capture.AudioInputSource;
import com.github.sipe90.visualizer.capture.DataLineInputSource;
import com.github.sipe90.visualizer.gui.Controller;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class AudioVisualizer extends Application {

    private Stage primaryStage;
    private FXMLLoader loader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        ByteRingBuffer buffer = new ByteRingBuffer(8192);
        AudioCapture capture = new AudioCapture(buffer);
        Controller controller = new Controller(this, capture);

        URL windowFxml = AudioVisualizer.class.getResource("/window.fxml");
        loader = new FXMLLoader(windowFxml);
        loader.setController(controller);

        primaryStage.setTitle("Audio Visualizer");
        reloadStage();
        primaryStage.show();
    }

    public void reloadStage() {
        try {
            loader.setRoot(null);
            VBox root = loader.load();
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
