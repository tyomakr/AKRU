package ru.aikr.spring.fx.akru.logging;

import ch.qos.logback.core.OutputStreamAppender;

import java.io.FilterOutputStream;
import java.io.OutputStream;

public class StaticOutputStreamAppender<E> extends OutputStreamAppender<E> {

    private static final DelegatingOutputStream DELEGATING_OUTPUT_STREAM = new DelegatingOutputStream();

    @Override
    public void start() {
        setOutputStream(DELEGATING_OUTPUT_STREAM);
        super.start();
    }

    public static void setStaticOutputStream(OutputStream outputStream) {
        DELEGATING_OUTPUT_STREAM.setOutputStream(outputStream);
    }

    private static class DelegatingOutputStream extends FilterOutputStream {
        public DelegatingOutputStream() {
            super(new OutputStream() {
                @Override
                public void write(int b) {
                }
            });
        }

        void setOutputStream(OutputStream outputStream) {
            this.out = outputStream;
        }
    }
}