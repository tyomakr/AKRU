package ru.aikr.akru.core.impl;

import ru.aikr.akru.core.api.FileRenamer;
import ru.aikr.akru.core.api.RenameResult;
import ru.aikr.akru.core.api.DuplicateStrategy;

import java.io.IOException;
import java.nio.file.*;

public class FileRenamerImpl implements FileRenamer {

    @Override
    public RenameResult rename(Path source,
                               String targetName,
                               DuplicateStrategy strategy) throws IOException {
        Path dir = source.getParent();
        Path target = dir.resolve(targetName);

        try {
            if (strategy == DuplicateStrategy.SKIP && Files.exists(target)) {
                return RenameResult.error(source, "Target exists, skipped");
            }
            if (strategy == DuplicateStrategy.OVERWRITE) {
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } else { // SUFFIX
                Path candidate = target;
                int count = 1;
                while (Files.exists(candidate)) {
                    String name = targetName;
                    int dot = name.lastIndexOf('.');
                    String base = (dot >= 0) ? name.substring(0, dot) : name;
                    String ext  = (dot >= 0) ? name.substring(dot) : "";
                    candidate = dir.resolve(base + "_" + count++ + ext);
                }
                Files.move(source, candidate);
                target = candidate;
            }
            return RenameResult.ok(source, target);
        } catch (IOException e) {
            return RenameResult.error(source, e.getMessage());
        }
    }
}