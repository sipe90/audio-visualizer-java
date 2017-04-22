package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.util.fft.FFT;
import be.tarsos.dsp.util.fft.HammingWindow;
import com.github.sipe90.visualizer.util.ByteRingBuffer;

import javax.sound.sampled.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;

public class AudioCapture {

    private AudioDispatcher dispatcher;

    private FFTAudioProcessor fftProcessor;

    public AudioCapture() {
    }

    public void startCapture(Mixer mixer, int sampleRate, int sampleSize, int channels) {

        int bufferSize = 32;

        AudioFormat format = new AudioFormat(sampleRate, sampleSize, channels, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        fftProcessor = new FFTAudioProcessor(bufferSize, sampleRate, new HammingWindow());

        if (!mixer.isLineSupported(info)) {
            throw new AudioCaptureException("This audio format is not supported by the mixer. Please choose another format.");
        }

        try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(info);
            line.open(format, bufferSize);
            line.start();
            AudioInputStream stream = new AudioInputStream(line);
            TarsosDSPAudioInputStream audioStream = new JVMAudioInputStream(stream);
            dispatcher = new AudioDispatcher(audioStream,bufferSize,bufferSize / 2);

            dispatcher.addAudioProcessor(fftProcessor);
        } catch (LineUnavailableException e) {
            throw new AudioCaptureException(e);
        }

        String threadName = "CaptureWorker";
        Thread worker = new Thread(dispatcher, threadName);
        worker.start();
    }

    public void stopCapture() {
        dispatcher.stop();
    }

    public boolean isCapturing() {
        return dispatcher != null && !dispatcher.isStopped();
    }

    public float[] getFFTBins() {
        return fftProcessor.getBins();
    }

    public float[] getFFTAmplitudes() {
        return fftProcessor.getAmplitudes();
    }
}
