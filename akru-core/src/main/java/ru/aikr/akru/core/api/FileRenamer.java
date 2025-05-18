package ru.aikr.akru.core.api;

import java.io.IOException;
import java.nio.file.Path;

public interface FileRenamer {
    /**
     * Переносит файл source → source.getParent()/targetName,
     * применяя стратегию дублирования.
     * @return RenameResult.ok или RenameResult.error
     */
    RenameResult rename(Path source,
                        String targetName,
                        DuplicateStrategy strategy) throws IOException;
}
