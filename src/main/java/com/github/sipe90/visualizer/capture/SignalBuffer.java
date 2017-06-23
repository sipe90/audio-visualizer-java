package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

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
        float[] eventBuffer = audioEvent.getFloatBuffer();

        if (frameStepSize > buffer.length) {
            System.err.println("Buffer size is smaller than frame step size!");
            return false;
        }

        // On first event, copy the whole buffer
        if(audioEvent.getTimeStamp() == 0){
            // In case the event buffer is bigger than the signal buffer, drop some frames from the beginning
            int droppedFrames = buffer.length < eventBuffer.length ? eventBuffer.length - buffer.length : 0;
            synchronized (lock) {
                System.arraycopy(eventBuffer, droppedFrames, buffer, 0, eventBuffer.length - droppedFrames);
            }
            return true;
        }

        synchronized (lock) {
            System.arraycopy(buffer, 0, buffer, frameStepSize, buffer.length - frameStepSize);
            for (int i = 0; i < frameStepSize; i++) {
                buffer[frameStepSize - (i + 1)] = eventBuffer[i + frameOverlap];
            }
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
