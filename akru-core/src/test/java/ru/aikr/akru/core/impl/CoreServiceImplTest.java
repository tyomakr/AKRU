package ru.aikr.akru.core.impl;

import org.junit.jupiter.api.*;
import ru.aikr.akru.core.api.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class CoreServiceImplTest {

    private Path dummyFile1 = Path.of("file1.jpg");
    private Path dummyFile2 = Path.of("file2.png");
    private LocalDateTime fixedDt = LocalDateTime.of(2025,5,1,10,0);

    private ExecutorService oneThreadExec = Executors.newSingleThreadExecutor();

    @AfterEach
    void tearDown() {
        oneThreadExec.shutdownNow();
    }

    @Test
    void testProcessFiles_allSuccess() {
        // 1) Подставляем «заглушки»:
        MetadataReader mdReader = path -> java.util.Optional.of(fixedDt);
        FilenameGenerator filenameGen = (path, dt, tpl, idx, total) ->
                String.format("N%02d_%s", idx, path.getFileName());
        FileRenamer renamer = (path, targetName, strategy) ->
                RenameResult.ok(path, path.resolveSibling(targetName));
        HistoryManager history = res -> {
            // можно собрать в список или просто ничего не делать
        };

        CoreServiceImpl svc = new CoreServiceImpl(
                mdReader, filenameGen, renamer, history, oneThreadExec
        );

        List<RenameResult> results = svc.processFiles(
                List.of(dummyFile1, dummyFile2),
                "{orig}",               // шаблон игнорируется в заглушке
                DuplicateStrategy.SUFFIX,
                ZoneId.of("UTC")
        );

        assertEquals(2, results.size());
        // первый файл
        RenameResult r1 = results.get(0);
        assertTrue(r1.isSuccess());
        assertEquals(dummyFile1, r1.getSource());
        assertEquals("N01_file1.jpg", r1.getTarget().getFileName().toString());

        // второй файл
        RenameResult r2 = results.get(1);
        assertTrue(r2.isSuccess());
        assertEquals(dummyFile2, r2.getSource());
        assertEquals("N02_file2.png", r2.getTarget().getFileName().toString());
    }

    @Test
    void testProcessFiles_renameThrows() {
        MetadataReader mdReader = path -> java.util.Optional.empty();
        FilenameGenerator filenameGen = (p,dt,tpl,i,tot) -> "x";
        FileRenamer renamer = (path, name, strategy) -> {
            throw new java.io.IOException("fail!");
        };
        HistoryManager history = res -> assertFalse(res.isSuccess());

        CoreServiceImpl svc = new CoreServiceImpl(
                mdReader, filenameGen, renamer, history, oneThreadExec
        );

        List<RenameResult> results = svc.processFiles(
                List.of(dummyFile1), "{orig}", DuplicateStrategy.SKIP, ZoneId.systemDefault()
        );

        assertEquals(1, results.size());
        RenameResult r = results.get(0);
        assertFalse(r.isSuccess());
        assertEquals("fail!", r.getErrorMessage());
    }
}
