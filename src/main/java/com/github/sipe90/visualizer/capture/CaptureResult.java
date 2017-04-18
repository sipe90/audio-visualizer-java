package com.github.sipe90.visualizer.capture;

public class CaptureResult {

    protected enum Status {
        STOPPED, FAILED
    }

    private Status status;

    public static CaptureResult fromStatus(Status status) {
        return new CaptureResult(status);
    }

    private CaptureResult(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isFailure() {
        return status == Status.FAILED;
    }
}
