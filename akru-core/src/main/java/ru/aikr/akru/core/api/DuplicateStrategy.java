package ru.aikr.akru.core.api;

public enum DuplicateStrategy {
    SUFFIX,    // file.jpg → file_1.jpg, file_2.jpg…
    OVERWRITE, // replace existing
    SKIP       // leave existing, skip this one
}