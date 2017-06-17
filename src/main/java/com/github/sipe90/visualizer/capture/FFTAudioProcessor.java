package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.util.PitchConverter;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.WindowFunction;
import com.google.common.util.concurrent.AtomicDoubleArray;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;

public class FFTAudioProcessor implements AudioProcessor {

    private final FFT fft;

    private final int fftSize;
    private final int realSize;
    private final int sampleRate;
    private final float[] bins;

    private final Object lock = new Object();

    private float[] amplitudes;

    public FFTAudioProcessor(int fftSize, int sampleRate) {
        this(fftSize, sampleRate, null);
    }

    public FFTAudioProcessor(int fftSize, int sampleRate, WindowFunction windowFunction) {
        this.fftSize = fftSize;
        this.realSize = fftSize / 2;
        this.sampleRate = sampleRate;

        fft = new FFT(fftSize, null);

        bins = new float[realSize];
        amplitudes = new float[realSize];

        generateBins(sampleRate);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioBuffer = audioEvent.getFloatBuffer();
        float[] transformBuffer = new float[audioBuffer.length];
        float[] amplitudes = new float[realSize];

        System.arraycopy(audioBuffer, 0, transformBuffer, 0, audioBuffer.length);

        fft.forwardTransform(transformBuffer);
        fft.modulus(transformBuffer, amplitudes);

        setAmplitudes(amplitudes);

        return true;
    }

    public float[] getBins() {
        return bins;
    }

    private void setAmplitudes(float[] amplitudes) {
        synchronized (lock) {
            this.amplitudes = amplitudes;
        }
    }

    public float[] getAmplitudes() {
        synchronized (lock) {
            float[] ret = new float[amplitudes.length];
            System.arraycopy(amplitudes, 0, ret, 0, amplitudes.length);
            return ret;
        }
    }

    private void generateBins(int sampleRate) {
        for (int i = 0; i < bins.length; i++) {
            bins[i] = (float)fft.binToHz(i, sampleRate);
        }
    }

    @Override
    public void processingFinished() {}
}
