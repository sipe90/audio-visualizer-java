package com.github.sipe90.visualizer.gui;

import javafx.scene.control.ListCell;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

public class Components {

    public static class DeviceListCell extends ListCell<Mixer> {
        @Override
        protected void updateItem(Mixer item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
            } else {
                setText(item.getMixerInfo().getName());
            }
        }
    }

    public static class FormatListCell extends ListCell<AudioFormat> {
        @Override
        protected void updateItem(AudioFormat item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
            } else {
                setText(item.toString());
            }
        }
    }

}
