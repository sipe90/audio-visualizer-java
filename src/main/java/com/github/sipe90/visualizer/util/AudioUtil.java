package com.github.sipe90.visualizer.util;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.util.ArrayList;
import java.util.List;

public class AudioUtil {

    private static final Line.Info TARGET_LINE_INFO = new Line.Info(TargetDataLine.class);

    public static List<Mixer> getSupportedMixers() {
        List<Mixer> availableMixers = new ArrayList<>();
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(TARGET_LINE_INFO)) {
                availableMixers.add(mixer);
            }
        }
        return availableMixers;
    }

    public static Integer[] getSampleRates() {
        return new Integer[]{8000, 11025, 16000, 22050, 44100, 48000, 96000, 192000};
    }

    public static Integer[] getSampleSizes() {
        return new Integer[]{8, 16, 24};
    }
}
