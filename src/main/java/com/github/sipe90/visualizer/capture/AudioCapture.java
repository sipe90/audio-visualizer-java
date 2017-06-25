package com.github.sipe90.visualizer.capture;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioGenerator;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.synthesis.AmplitudeLFO;
import be.tarsos.dsp.synthesis.NoiseGenerator;
import be.tarsos.dsp.synthesis.SineGenerator;
import be.tarsos.dsp.util.fft.HammingWindow;
import be.tarsos.transcoder.Attributes;
import be.tarsos.transcoder.DefaultAttributes;
import be.tarsos.transcoder.Streamer;
import be.tarsos.transcoder.Transcoder;
import be.tarsos.transcoder.ffmpeg.EncoderException;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AudioCapture {

    public enum GENERATOR_TYPE {
        SINE,
        LFO,
        RND
    }

    private final int tarsosBufferSize = 512; // 2048;
    private final int bufferSize = 4096;
    private final int overlap = tarsosBufferSize / 2;

    private final Attributes targetFormat = DefaultAttributes.WAV_PCM_S16LE_MONO_44KHZ.getAttributes();

    private AudioDispatcher dispatcher;
    private AudioGenerator audioGenerator;

    private FFTAudioProcessor fftProcessor;
    private SignalBuffer signalBuffer;

    private AudioPlaybackProcessor audioPlayer;
    private GainProcessor gainProcessor;

    public AudioCapture() {}

    public void startCapture(Mixer mixer, int sampleRate, int sampleSize, int channels, boolean playback) {

        AudioFormat format = new AudioFormat(sampleRate, sampleSize, channels, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!mixer.isLineSupported(info)) {
            throw new AudioCaptureException("This audio format is not supported by the mixer. Please choose another format.");
        }

        try {
            TargetDataLine line = (TargetDataLine) mixer.getLine(info);
            line.open(format, tarsosBufferSize);
            line.start();
            AudioInputStream stream = new AudioInputStream(line);
            TarsosDSPAudioInputStream audioStream = new JVMAudioInputStream(stream);

            dispatcher = new AudioDispatcher(audioStream, tarsosBufferSize, overlap);

            fftProcessor = new FFTAudioProcessor(tarsosBufferSize, sampleRate, new HammingWindow());
            signalBuffer = new SignalBuffer(bufferSize);
            audioPlayer = new AudioPlaybackProcessor(audioStream.getFormat());

            dispatcher.addAudioProcessor(signalBuffer);
            dispatcher.addAudioProcessor(fftProcessor);
            if (playback) {
                dispatcher.addAudioProcessor(audioPlayer);
            }
        } catch (LineUnavailableException e) {
            throw new AudioCaptureException(e);
        }

        CompletableFuture.runAsync(dispatcher).exceptionally((t) -> { t.printStackTrace(); return null;});
    }

    public void startCapture(File audioFile, double volume, Function<Void, Void> finishedCallback) {
        try {
            // The thrown exceptions are suppressed by TarsosTranscoder...
            if (Transcoder.getInfo(audioFile.getAbsolutePath()) == null) {
                throw new AudioCaptureException("The file is not an audio file or it is encoded in an unsupported format. Please choose another file.");
            }

            AudioInputStream inputStream;
            if (Transcoder.transcodingRequired(audioFile.getAbsolutePath(), targetFormat)) {
                inputStream = Streamer.stream(audioFile.getAbsolutePath(), targetFormat);
            } else {
                inputStream = AudioSystem.getAudioInputStream(audioFile);
            }

            TarsosDSPAudioInputStream tarsosInputStream = new JVMAudioInputStream(inputStream);
            System.out.println(tarsosInputStream.getFormat());

            dispatcher = new AudioDispatcher(tarsosInputStream, tarsosBufferSize, overlap);

            fftProcessor = new FFTAudioProcessor(tarsosBufferSize, (int)dispatcher.getFormat().getSampleRate(), new HammingWindow());
            signalBuffer = new SignalBuffer(bufferSize);

            audioPlayer = new AudioPlaybackProcessor(tarsosInputStream.getFormat());
            gainProcessor = new GainProcessor(volume);

            dispatcher.addAudioProcessor(gainProcessor);
            dispatcher.addAudioProcessor(signalBuffer);
            dispatcher.addAudioProcessor(fftProcessor);
            dispatcher.addAudioProcessor(audioPlayer);
        } catch (UnsupportedAudioFileException | EncoderException e) {
            throw new AudioCaptureException("The file is not an audio file or it is encoded in an unsupported format. Please choose another file.");
        } catch (IOException e) {
            throw new AudioCaptureException("Unable to open file.");
        } catch (LineUnavailableException e) {
            throw new AudioCaptureException("Unable to play back file.");
        }

        CompletableFuture.runAsync(dispatcher).exceptionally((t) -> { t.printStackTrace(); return null;}).thenApply(finishedCallback);
    }

    public void startGenerate(GENERATOR_TYPE generator, Map<String, Double> generatorParams) {
        try {
            audioGenerator = new AudioGenerator(tarsosBufferSize, overlap);

            if (generator == GENERATOR_TYPE.SINE) {
                if (generatorParams != null) {
                    double gain = generatorParams.getOrDefault("gain", 1.0d);
                    double freq = generatorParams.getOrDefault("freq", 440.0d);
                    audioGenerator.addAudioProcessor(new SineGenerator(gain, freq));
                } else {
                    audioGenerator.addAudioProcessor(new SineGenerator());
                }
            } else if (generator == GENERATOR_TYPE.RND) {
                if (generatorParams != null) {
                    double gain = generatorParams.getOrDefault("gain", 1.0d);
                    audioGenerator.addAudioProcessor(new NoiseGenerator(gain));
                } else {
                    audioGenerator.addAudioProcessor(new NoiseGenerator());
                }
            } else if (generator == GENERATOR_TYPE.LFO) {
                if (generatorParams != null) {
                    double gainScale = generatorParams.getOrDefault("scale", 0.75d);
                    double freq = generatorParams.getOrDefault("freq", 1.5d);
                    audioGenerator.addAudioProcessor(new AmplitudeLFO(freq, gainScale));
                } else {
                    audioGenerator.addAudioProcessor(new NoiseGenerator());
                }
            }

            fftProcessor = new FFTAudioProcessor(tarsosBufferSize, (int)audioGenerator.getFormat().getSampleRate(), new HammingWindow());
            signalBuffer = new SignalBuffer(bufferSize);
            audioPlayer = new AudioPlaybackProcessor(new AudioFormat(44100, 16, 1, true, false));

            audioGenerator.addAudioProcessor(signalBuffer);
            audioGenerator.addAudioProcessor(fftProcessor);
            audioGenerator.addAudioProcessor(audioPlayer);

        } catch (LineUnavailableException e) {
            throw new AudioCaptureException("Unable to play back sound.");
        }
    }

    public void stopCapture() {
        if (dispatcher != null) dispatcher.stop();
        if (audioGenerator != null) audioGenerator.stop();
    }

    public boolean isCapturing() {
        // AudioGenerator has no #isStopped()!!
        return (dispatcher != null && !dispatcher.isStopped()) || (audioGenerator != null  /* && !audioGenerator.isStopped() */ );
    }

    public float[] getFFTBins() {
        return fftProcessor.getBins();
    }

    public float[] getFFTAmplitudes() {
        return fftProcessor.getAmplitudes();
    }

    public float[] getBuffer() { return signalBuffer.getBuffer(); }

    public void setVolume(double value) {
        if (gainProcessor != null) {
            gainProcessor.setGain(value);
        }
    }
}
