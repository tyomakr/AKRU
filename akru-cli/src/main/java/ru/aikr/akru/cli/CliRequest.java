package ru.aikr.akru.cli;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.lang.String;

/**
 * Запрос CLI:
 *  - files: список полных путей к файлам для переименования
 *  - template: шаблон нового имени (например, "{date}_{orig}")
 *  - options: дополнительные параметры работы
 */

@Setter
@Getter
@NoArgsConstructor
public class CliRequest {
    private List<String> files;
    private String template;
    private Options options;
}