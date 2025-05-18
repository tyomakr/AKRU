package ru.aikr.akru.core.api;

@FunctionalInterface
public interface HistoryManager {
    /**
     * Записывает результат переименования в историю.
     */
    void record(RenameResult result);
}