package ru.aikr.akru.core.impl;

import ru.aikr.akru.core.api.MetadataReader;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MetadataReaderImpl implements MetadataReader {

    // Формат EXIF-поля DateTimeOriginal
    private static final DateTimeFormatter EXIF_FMT =
            DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Override
    public Optional<LocalDateTime> readDateTime(Path path) {
        try (InputStream is = Files.newInputStream(path)) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);
            ExifSubIFDDirectory dir =
                    metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (dir != null) {
                // читаем именно строку из EXIF и парсим её “как есть”
                String dateStr = dir.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (dateStr != null) {
                    LocalDateTime dt = LocalDateTime.parse(dateStr, EXIF_FMT);
                    return Optional.of(dt);
                }
            }
        } catch (Exception e) {
            // при ошибке чтения EXIF возвращаем пустой Optional
        }
        return Optional.empty();
    }
}