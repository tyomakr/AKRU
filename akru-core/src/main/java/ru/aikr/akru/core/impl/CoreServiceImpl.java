package ru.aikr.akru.core.impl;

import ru.aikr.akru.core.api.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CoreServiceImpl implements CoreService {

    /* ==== зависимости ===== */
    private final MetadataReader metadataReader;
    private final FilenameGenerator filenameGen;
    private final FileRenamer fileRenamer;
    private final HistoryManager history;
    private final ExecutorService exec;

    /* ---- конструкторы ---- */
    /** «Боевой» – создаёт все реализации по-умолчанию. */
    public CoreServiceImpl() {
        this(new MetadataReaderImpl(),
                new FilenameGeneratorImpl(),
                new FileRenamerImpl(),
                new HistoryManagerImpl(),
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()));
    }

    /** Тестовый – даёт подменить любые зависимости. */
    public CoreServiceImpl(MetadataReader metadataReader,
                           FilenameGenerator filenameGen,
                           FileRenamer fileRenamer,
                           HistoryManager history,
                           ExecutorService exec) {
        this.metadataReader = metadataReader;
        this.filenameGen    = filenameGen;
        this.fileRenamer    = fileRenamer;
        this.history        = history;
        this.exec           = exec;
    }

    /* ---- бизнес-метод ---- */
    @Override
    public List<RenameResult> processFiles(List<Path> files,
                                           String template,
                                           DuplicateStrategy strategy,
                                           ZoneId zone) {

        int total = files.size();

        List<Future<RenameResult>> futures = files.stream()
                .map(path -> exec.submit(() -> {
                    try {
                        /* 1. EXIF-дата (опционально) */
                        LocalDateTime dt =
                                metadataReader.readDateTime(path).orElse(null);

                        /* 2. Генерация нового имени */
                        int idx = files.indexOf(path) + 1;
                        String newName =
                                filenameGen.generate(path, dt, template, idx, total);

                        /* 3. Переименование */
                        RenameResult res =
                                fileRenamer.rename(path, newName, strategy);

                        /* 4. История */
                        history.record(res);
                        return res;

                    } catch (Exception ex) {            // любой сбой
                        RenameResult err =
                                RenameResult.error(path, ex.getMessage());
                        history.record(err);
                        return err;
                    }
                }))
                .collect(Collectors.toList());

        /* собираем результаты */
        List<RenameResult> results = futures.stream()
                .map(f -> {
                    try { return f.get(); }
                    catch (Exception e) {
                        return RenameResult.error(null, e.getMessage());
                    }
                }).collect(Collectors.toList());

        exec.shutdown();
        return results;
    }
}
