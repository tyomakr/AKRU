package ru.aikr.spring.fx.akru.logging;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class ConsoleAreaLogger extends OutputStream {

    private final TextArea textArea;

    public ConsoleAreaLogger(TextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) {
        Platform.runLater(() -> textArea.appendText(String.valueOf((char) b)));
    }
}