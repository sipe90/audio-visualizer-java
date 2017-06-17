package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

import javax.sound.sampled.*;

public class AudioPlaybackProcessor implements AudioProcessor {

    /**
     * The LineWavelet to send sound to. Is also used to keep everything in sync.
     */
    private SourceDataLine line;


    private final AudioFormat format;

    /**
     * Creates a new audio player.
     *
     * @param format
     *            The AudioFormat of the buffer.
     * @throws LineUnavailableException
     *             If no output LineWavelet is available.
     */
    public AudioPlaybackProcessor(final AudioFormat format)	throws LineUnavailableException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
        this.format = format;
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open();
        line.start();
    }

    public AudioPlaybackProcessor(final AudioFormat format, int bufferSize) throws LineUnavailableException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,format,bufferSize);
        this.format = format;
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format,bufferSize*2);
        line.start();
    }

    public AudioPlaybackProcessor(final TarsosDSPAudioFormat format) throws LineUnavailableException {
        this(JVMAudioInputStream.toAudioFormat(format));
    }

    public AudioPlaybackProcessor(final TarsosDSPAudioFormat format, int bufferSize) throws LineUnavailableException {
        this(JVMAudioInputStream.toAudioFormat(format),bufferSize);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        int byteOverlap = audioEvent.getOverlap() * format.getFrameSize();
        int byteStepSize = audioEvent.getBufferSize() * format.getFrameSize() - byteOverlap;

        if(audioEvent.getTimeStamp() == 0){
            byteOverlap = 0;
            byteStepSize = audioEvent.getBufferSize() * format.getFrameSize();
        }

        int bytesWritten = line.write(audioEvent.getByteBuffer(), byteOverlap, byteStepSize);
        if(bytesWritten != byteStepSize){
            System.err.println(String.format("Expected to write %d bytes but only wrote %d bytes",byteStepSize,bytesWritten));
        }
        return true;
    }

    public void processingFinished() {
        line.drain();
        line.stop();
        line.close();
    }
}
