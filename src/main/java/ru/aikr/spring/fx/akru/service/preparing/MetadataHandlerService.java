package ru.aikr.spring.fx.akru.service.preparing;

import ru.aikr.spring.fx.akru.domain.FileItemEntity;

public interface MetadataHandlerService {

    void readMetadata(FileItemEntity fileItem);
}
