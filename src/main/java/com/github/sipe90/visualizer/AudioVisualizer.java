package com.github.sipe90.visualizer;

import com.github.sipe90.visualizer.capture.AudioCapture;
import com.github.sipe90.visualizer.gui.controller.ControllerFactory;
import com.github.sipe90.visualizer.gui.controller.MainController;
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

        setUserAgentStylesheet(STYLESHEET_MODENA);

        initPrimaryWindow( );
        if (debug) {
            initDebugWindow();
        }
    }

    private void initPrimaryWindow() {
        URL windowFxml = AudioVisualizer.class.getResource("/window/main.fxml");

        primaryLoader = new FXMLLoader(windowFxml);
        primaryLoader.setControllerFactory(new ControllerFactory(this, capture));

        primaryWindow.setTitle("Audio Visualizer");

        primaryWindow.setOnCloseRequest((event -> {
            if (capture != null && capture.isCapturing())
                capture.stopCapture();
            destroyDebugWindow();
        }));

        reloadStage(primaryLoader, primaryWindow);
        primaryWindow.show();
    }

    private void initDebugWindow() {
        debugWindow = new Stage();

        URL debugFxml = AudioVisualizer.class.getResource("/window/debug.fxml");
        debugLoader = new FXMLLoader(debugFxml);
        debugLoader.setControllerFactory(new ControllerFactory(this, capture));
        debugWindow.setTitle("Debug");

        debugWindow.setOnCloseRequest((event -> {
            debug = false;
            ((MainController)primaryLoader.getController()).updateDebugCheck();
        }));

        reloadStage(debugLoader, debugWindow);

        debugWindow.setX(primaryWindow.getX() + 40);
        debugWindow.setY(primaryWindow.getY() + 40);

        debugWindow.show();
    }

    private void destroyDebugWindow() {
        if (debugWindow != null) {
            debugWindow.close();
        }
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

    public boolean isDebug() {
        return debug;
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
            loader.setController(null);
            VBox root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadStages() {
        reloadPrimaryStage();
        if (debug) {
            reloadDebugStage();
        }
    }
}
