package ru.aikr.akru.core.impl;

import org.junit.jupiter.api.Test;
import ru.aikr.akru.core.api.MetadataReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MetadataReaderImplTest {

    private final MetadataReader reader = new MetadataReaderImpl();

    /**
     * Для текстовых файлов (без EXIF) метод должен вернуть пустой Optional.
     */
    @Test
    void testNoExifFile() throws Exception {
        Path temp = Files.createTempFile("test", ".txt");
        Optional<LocalDateTime> dt = reader.readDateTime(temp);
        assertTrue(dt.isEmpty(), "Для файлов без EXIF не должна возвращаться дата");
    }

    /**
     * Для JPEG с EXIF-датой съёмки проверяем, что:
     *  1) Optional не пустой
     *  2) Содержит ровно ожидаемую дату-время съёмки
     */
    @Test
    void testWithExifFile() throws Exception {
        Path img = Path.of(
                getClass().getClassLoader()
                        .getResource("photo-with-exif.jpg")
                        .toURI()
        );

        Optional<LocalDateTime> dt = reader.readDateTime(img);
        assertTrue(dt.isPresent(), "EXIF-дата должна быть прочитана");

        LocalDateTime expected = LocalDateTime.of(2025, 5, 1, 9, 30, 0);
        assertEquals(expected, dt.get(), "Дата из EXIF должна точно совпадать с заданной");
    }
}