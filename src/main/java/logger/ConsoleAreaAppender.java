package logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

public class ConsoleAreaAppender extends WriterAppender {

    private static TextArea textArea = null;

    //добаление сообщения
    public void doAppend(LoggingEvent event) {
        final String message = event.getMessage().toString() + "\r";

        Platform.runLater(() -> textArea.appendText(message + "\n"));
    }



    //setter
    public static void setTextArea(TextArea textArea) {
        ConsoleAreaAppender.textArea = textArea;
    }
}
