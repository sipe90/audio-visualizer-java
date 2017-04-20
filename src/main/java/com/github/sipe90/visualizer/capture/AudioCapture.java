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

    public AudioCapture() {}

    public void startCapture(Mixer mixer, int sampleRate, int sampleSize, int channels) {

        AudioFormat format = new AudioFormat(sampleRate, sampleSize, channels, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!mixer.isLineSupported(info)) {
            throw new AudioCaptureException("This audio format is not supported by the mixer. Please choose another format.");
        }

        try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(info);
            line.open(format, 1024);
            line.start();
            AudioInputStream stream = new AudioInputStream(line);
            TarsosDSPAudioInputStream audioStream = new JVMAudioInputStream(stream);
            dispatcher = new AudioDispatcher(audioStream,1024,512);

            dispatcher.addAudioProcessor(new FFTAudioProcessor(1024, new HammingWindow()));
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

}
