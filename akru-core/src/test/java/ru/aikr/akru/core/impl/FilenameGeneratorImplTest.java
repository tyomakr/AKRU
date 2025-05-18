package ru.aikr.akru.core.impl;

import org.junit.jupiter.api.Test;
import ru.aikr.akru.core.api.FilenameGenerator;

import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FilenameGeneratorImplTest {

    private final FilenameGenerator gen = new FilenameGeneratorImpl();
    private final LocalDateTime dt = LocalDateTime.of(2025,5,1,9,30,0);

    @Test
    void testSequencePadding() {
        String tpl = "{seq}_{orig}.{ext}";
        String name1 = gen.generate(Paths.get("IMG_001.jpg"), dt, tpl, 1, 15);
        String name9 = gen.generate(Paths.get("IMG_009.jpg"), dt, tpl, 9, 15);
        String name10 = gen.generate(Paths.get("IMG_010.jpg"), dt, tpl, 10, 15);
        assertEquals("01_IMG_001.jpg", name1);
        assertEquals("09_IMG_009.jpg", name9);
        assertEquals("10_IMG_010.jpg", name10);
    }

    @Test
    void testTemplateDateAndExt() {
        String tpl = "{date}.{ext}";
        String result = gen.generate(Paths.get("pic.png"), dt, tpl, 1, 1);
        assertTrue(result.startsWith("2025-05-01_09-30-00"));
        assertTrue(result.endsWith(".png"));
    }
}
