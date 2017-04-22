package com.github.sipe90.visualizer;

import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.gui.DebugController;
import com.github.sipe90.visualizer.gui.PrimaryController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class AudioVisualizer extends Application {

    private Stage primaryWindow;
    private Stage debugWindow;

    private FXMLLoader primaryLoader;
    private FXMLLoader debugLoader;

    private PrimaryController primaryController;
    private DebugController debugController;

    private boolean debug;

    private AudioCapture capture;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        List<String> params =  getParameters().getRaw();
        if (!params.isEmpty()) {
            try {
                checkArgs(params);
            }catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                Platform.exit();
            }
        }

        capture = new AudioCapture();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryWindow = primaryStage;

        initPrimaryWindow( );
        if (debug) {
            initDebugWindow();
        }
    }

    private void initPrimaryWindow() {
        URL windowFxml = AudioVisualizer.class.getResource("/window/window.fxml");

        primaryController = new PrimaryController(this, capture);
        primaryLoader = new FXMLLoader(windowFxml);
        primaryLoader.setController(primaryController);
        primaryWindow.setTitle("Audio Visualizer");

        reloadStage(primaryLoader, primaryWindow);
        primaryWindow.show();
    }

    private void initDebugWindow() {
        debugWindow = new Stage();
        debugController = new DebugController(this, capture);

        URL debugFxml = AudioVisualizer.class.getResource("/window/debug.fxml");
        debugLoader = new FXMLLoader(debugFxml);
        debugLoader.setController(debugController);
        debugWindow.setTitle("Debug");

        debugWindow.setOnCloseRequest((event -> primaryController.resetDebugCheck()));

        reloadStage(debugLoader, debugWindow);
        debugWindow.show();
    }

    private void destroyDebugWindow() {
        debugWindow.close();
        debugController = null;
    }

    private void checkArgs(List<String> args) {
        int i = 0;
        while (i < args.size()) {
            i += checkArg(args, i);
        }
    }

    private int checkArg(List<String> args, int idx) {
        String arg = args.get(idx);
        if ("-d".equals(arg)) {
            debug = true;
            return 1;
        }
        throw new IllegalArgumentException("Unknown argument: " + arg);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        if (debug) {
            initDebugWindow();
        } else {
            destroyDebugWindow();
        }
    }

    public void reloadPrimaryStage() {
        reloadStage(primaryLoader, primaryWindow);
    }

    public void reloadDebugStage() {
        if (!debug) {
            throw new IllegalStateException("Debug window is not enabled");
        }
        reloadStage(debugLoader, debugWindow);
    }

    private void reloadStage(FXMLLoader loader, Stage stage) {
        try {
            loader.setRoot(null);
            VBox root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
