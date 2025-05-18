package ru.aikr.akru.core.impl;

import ru.aikr.akru.core.api.FilenameGenerator;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FilenameGeneratorImpl implements FilenameGenerator {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Override
    public String generate(Path imageFile,
                           LocalDateTime dateTime,
                           String template,
                           int index,
                           int total) {
        String name = imageFile.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String base = (dot >= 0) ? name.substring(0, dot) : name;
        String ext  = (dot >= 0) ? name.substring(dot+1) : "";

        // динамическая ширина: 1→9 → width=1; 10→99 → width=2; 100→999 → width=3
        int width = String.valueOf(total).length();
        String seq = String.format("%0" + width + "d", index);

        String result = template
                .replace("{date}", dateTime != null ? DATE_FMT.format(dateTime) : "")
                .replace("{orig}", base)
                .replace("{seq}", seq)
                .replace("{ext}", ext);

        // если в шаблоне не указали точку перед {ext}, добавьте её
        if (!result.endsWith("." + ext) && !ext.isEmpty()) {
            result += "." + ext;
        }
        return result;
    }
}