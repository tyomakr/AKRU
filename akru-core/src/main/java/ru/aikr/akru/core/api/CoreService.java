package ru.aikr.akru.core.api;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.List;

/**
 * Оркестратор всего процесса:
 * 1) читаем EXIF,
 * 2) генерируем имена,
 * 3) переименовываем параллельно,
 * 4) пишем в историю.
 */
public interface CoreService {
    List<RenameResult> processFiles(List<Path> files,
                                    String template,
                                    DuplicateStrategy strategy,
                                    ZoneId zone);
}
