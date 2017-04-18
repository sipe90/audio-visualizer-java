package com.github.sipe90.visualizer.capture;

public class AudioCaptureException extends RuntimeException {

    public AudioCaptureException(String msg) {
        super(msg);
    }

    public AudioCaptureException(Throwable cause) {
        super(cause);
    }

    public AudioCaptureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
