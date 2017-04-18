package com.github.sipe90.visualizer.capture;

import com.github.sipe90.visualizer.util.ByteRingBuffer;

import java.util.concurrent.CompletableFuture;

public class AudioCapture {

    private AudioInputSource source;
    private ByteRingBuffer buffer;
    private int readFrames;
    private boolean capturing = false;

    public AudioCapture(AudioInputSource source, ByteRingBuffer buffer) {
        setInputSource(source);
        setTargetBuffer(buffer);
    }

    public AudioCapture(AudioInputSource source, ByteRingBuffer buffer, int readFrames) {
        setInputSource(source);
        setTargetBuffer(buffer);
        setReadFrames(readFrames);
    }

    public void setInputSource(AudioInputSource source) {
        this.source = source;
    }

    public void setTargetBuffer(ByteRingBuffer buffer) {
        this.buffer = buffer;
    }

    public void setReadFrames(int readFrames) {
        this.readFrames = readFrames;
    }

    public void startCapture() {

        String threadName = "CaptureWorker-" + source.getName();

        source.open();
        capturing = true;

            Runnable task = () -> {
                int bytesToRead = readFrames * source.getFrameSize();

                try {
                    source.start();

                    Thread.sleep(1000);

                    while (capturing && source.isOpen()) {
                        byte[] bytes = new byte[bytesToRead];
                        int actualRead = source.read(bytes, bytesToRead);
                        buffer.write(bytes, 0, actualRead);
                    }
                    source.stop();
                    System.out.println("Thread " + threadName + " stopped");
                } catch (Exception e) {
                    System.err.println("Exception raised in thread " + threadName);
                    e.printStackTrace();
                } finally {
                    capturing = false;
                    source.close();
                }
            };

        Thread worker = new Thread(task, threadName);
        worker.start();
    }

    public void stopCapture() {
        capturing = false;
    }

}
