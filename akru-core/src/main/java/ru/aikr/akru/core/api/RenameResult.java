package ru.aikr.akru.core.api;

import java.nio.file.Path;

public class RenameResult {
    private final Path source;
    private final Path target;
    private final boolean success;
    private final String errorMessage;

    private RenameResult(Path source, Path target, boolean success, String errorMessage) {
        this.source = source;
        this.target = target;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static RenameResult ok(Path source, Path target) {
        return new RenameResult(source, target, true, null);
    }

    public static RenameResult error(Path source, String errorMessage) {
        return new RenameResult(source, null, false, errorMessage);
    }

    public Path getSource() { return source; }
    public Path getTarget() { return target; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
