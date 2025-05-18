package ru.aikr.akru.cli;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.String;

/**
 * Результат обработки одного файла:
 *  - orig: исходный путь
 *  - newName: новое имя файла (без пути)
 *  - status: "ok" или "skipped"
 *  - error: сообщение об ошибке при status="skipped"
 */

@Setter
@Getter
@NoArgsConstructor
public class CliResult {
    private String orig;
    private String newName;
    private String status;
    private String error;
}