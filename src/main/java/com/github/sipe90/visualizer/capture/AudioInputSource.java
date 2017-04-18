package com.github.sipe90.visualizer.capture;

public interface AudioInputSource {

    void open() throws AudioCaptureException;

    void start() throws AudioCaptureException;

    int read(byte[] bytes, int amount) throws AudioCaptureException;

    void stop() throws AudioCaptureException;

    void close() throws AudioCaptureException;

    String getName();

    boolean isOpen();

    int getFrameSize();

}
