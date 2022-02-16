package ru.aikr.spring.fx.akru.service.storage;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import ru.aikr.spring.fx.akru.domain.AdditionType;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;

import java.io.File;

public interface FileItemsStorageService{

    void addFiles(AdditionType additionType, boolean isImagesOnly, Stage stage);

    void additionFiles(File file, boolean isImagesOnly);

    void clearItemListOnDB();

    ObservableList<FileItemEntity> getFileItemEntitiesList();

    void removeFileItemEntity(int index);

    void saveFileItemEntity(FileItemEntity fileItemEntity);

    void swapPositionsFileItems (FileItemEntity fileItemEntity1, FileItemEntity fileItemEntity2);
}