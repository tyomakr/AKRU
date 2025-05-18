package ru.aikr.akru.core.api;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

public interface MetadataReader {
    /**
     * Читает дату/время съёмки из EXIF.
     * @return Optional.empty(), если нет EXIF или не получилось прочесть.
     */
    Optional<LocalDateTime> readDateTime(Path imageFile) throws IOException;
}
