package ru.aikr.akru.core.api;

import java.nio.file.Path;
import java.time.LocalDateTime;

public interface FilenameGenerator {
    /**
     * Генерирует новое имя файла по шаблону.
     * @param imageFile    оригинальный файл
     * @param dateTime     дата/время съёмки (может быть null)
     * @param template     шаблон, например "{date}_{orig}_{seq}.{ext}"
     * @param index        порядковый номер (начиная с 1)
     * @param total        общее количество файлов
     * @return базовое имя (без пути), готовое к сохранению
     */
    String generate(Path imageFile,
                    LocalDateTime dateTime,
                    String template,
                    int index,
                    int total);
}
