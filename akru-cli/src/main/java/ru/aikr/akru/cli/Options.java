package ru.aikr.akru.cli;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Опции переименования:
 *  - handleDuplicates: стратегия для коллизий ("suffix", "overwrite", "skip")
 *  - tz: часовой пояс ("local" или ID зоны, например "UTC" или "Europe/Amsterdam")
 */

@Setter
@Getter
@NoArgsConstructor
public class Options {
    private String handleDuplicates;
    private String tz;
}