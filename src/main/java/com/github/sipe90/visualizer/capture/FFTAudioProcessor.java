package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.WindowFunction;

public class FFTAudioProcessor implements AudioProcessor {

    private final FFT fft;
    private final float[] amplitudes;

    public FFTAudioProcessor(int bufferSize) {
        this(bufferSize, null);
    }

    public FFTAudioProcessor(int bufferSize, WindowFunction windowFunction) {
        fft = new FFT(bufferSize, windowFunction);
        amplitudes = new float[bufferSize / 2];
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        float[] transformBuffer = new float[audioBuffer.length];

        System.arraycopy(audioBuffer, 0, transformBuffer, 0, audioBuffer.length);

        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        return true;
    }

    @Override
    public void processingFinished() {}
}
