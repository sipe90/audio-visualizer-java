package com.github.sipe90.visualizer.capture;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class DataLineInputSource implements AudioInputSource {

    private final TargetDataLine line;
    private final AudioFormat format;

    public DataLineInputSource(TargetDataLine line, AudioFormat format) {
        this.line = line;
        this.format = format;
    }

    @Override
    public void open() throws AudioCaptureException {
        try {
            line.open(format);
        } catch (LineUnavailableException e) {
            throw new AudioCaptureException(e);
        }
    }

    @Override
    public void start() throws AudioCaptureException {
        line.start();
    }

    @Override
    public int read(byte[] bytes, int amount) throws AudioCaptureException {
        return line.read(bytes, 0, amount);
    }

    @Override
    public void stop() throws AudioCaptureException {
        line.stop();
    }

    @Override
    public void close() throws AudioCaptureException {
        line.close();
    }

    @Override
    public int getFrameSize() {
        return line.getFormat().getFrameSize();
    }

    @Override
    public String getName() {
        return line.getClass().getSimpleName() + "-"
                + format.getEncoding() + "-"
                + format.getSampleRate() + "Hz-"
                + format.getSampleSizeInBits() + "bit-"
                + format.getChannels() + "ch";
    }

    @Override
    public boolean isOpen() {
        return line.isOpen();
    }
}
