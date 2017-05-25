package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

import javax.sound.sampled.*;
import java.util.Arrays;

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
        int channels = format.getChannels();
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

    public boolean setVolume(double volume) {
        if (volume < 0.0d || volume > 1.0d) {
            throw new IllegalArgumentException("Volume must be between 1.0 and 0.0 (was " + volume + ")");
        }
        if (!line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return false;
        }
        FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(20f * (float) Math.log10(volume));
        return true;
    }

    public void processingFinished() {
        line.drain();
        line.stop();
        line.close();
    }
}
