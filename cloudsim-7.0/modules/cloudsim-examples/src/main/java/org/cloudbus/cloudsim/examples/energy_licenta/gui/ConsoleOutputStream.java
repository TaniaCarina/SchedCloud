package org.cloudbus.cloudsim.examples.energy_licenta.gui;

import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ConsoleOutputStream extends OutputStream {
    private final TextArea output;

    public ConsoleOutputStream(TextArea output) {
        this.output = output;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> output.appendText(String.valueOf((char) b)));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        String text = new String(b, off, len);
        Platform.runLater(() -> output.appendText(text));
    }
}
