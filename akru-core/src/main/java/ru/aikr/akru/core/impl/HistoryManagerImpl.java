package ru.aikr.akru.core.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aikr.akru.core.api.HistoryManager;
import ru.aikr.akru.core.api.RenameResult;

import java.io.IOException;
import java.nio.file.*;

/**
 * Простая реализация HistoryManager, которая
 * сериализует каждый RenameResult в JSON и
 * дописывает его в файл history.json.
 */
public class HistoryManagerImpl implements HistoryManager {

    private static final Path HISTORY_FILE = Paths.get("history.json");
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean initialized = false;

    @Override
    public void record(RenameResult result) {
        try {
            // При первом вызове создаём (или затираем) файл
            if (!initialized) {
                Files.deleteIfExists(HISTORY_FILE);
                Files.createFile(HISTORY_FILE);
                initialized = true;
            }
            // Сериализуем в строку и дописываем как новую строку
            String json = mapper.writeValueAsString(result);
            Files.writeString(HISTORY_FILE,
                    json + System.lineSeparator(),
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось записать историю", e);
        }
    }
}