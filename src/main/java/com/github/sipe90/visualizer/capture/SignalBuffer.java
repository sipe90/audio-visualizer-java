package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

import java.util.Arrays;

public class SignalBuffer implements AudioProcessor {

    private final float[] buffer;

    private final Object lock = new Object();

    public SignalBuffer(int bufferSize) {
        buffer = new float[bufferSize];
    }

    @Override
    public boolean process(AudioEvent audioEvent) {

        int frameOverlap = audioEvent.getOverlap();
        int frameStepSize = audioEvent.getBufferSize() - frameOverlap;

        if(audioEvent.getTimeStamp() == 0){
            frameOverlap = 0;
            frameStepSize = audioEvent.getBufferSize();
        }

        if (frameStepSize > buffer.length) {
            System.err.println("Buffer size is smaller than frame step size!");
            return false;
        }

        synchronized (lock) {
            System.arraycopy(buffer, 0, buffer, frameStepSize - 1, buffer.length - frameStepSize);
            System.arraycopy(audioEvent.getFloatBuffer(), frameOverlap, buffer, 0, frameStepSize);
        }

        return true;
    }

    public float[] getBuffer() {
        float[] ret = new float[buffer.length];
        synchronized (lock) {
            System.arraycopy(buffer, 0, ret, 0, buffer.length);
        }
        return ret;
    }

    @Override
    public void processingFinished() {

    }
}
