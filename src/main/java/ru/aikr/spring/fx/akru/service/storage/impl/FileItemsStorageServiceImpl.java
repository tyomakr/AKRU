package ru.aikr.spring.fx.akru.service.storage.impl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.aikr.spring.fx.akru.domain.AdditionType;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.repository.FileItemsRepository;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileItemsStorageServiceImpl implements FileItemsStorageService {

    private final FileItemsRepository fileItemsRepository;


    @Override
    public ObservableList<FileItemEntity> getFileItemEntitiesList() {
        return FXCollections.observableArrayList(fileItemsRepository.findAll());
    }

    @Override
    public void removeFileItemEntity(int index) {
        fileItemsRepository.delete(fileItemsRepository.getById((long) index));
    }

    @Override
    public void saveFileItemEntity(FileItemEntity fileItemEntity) {
        fileItemsRepository.save(fileItemEntity);
    }

    public void swapPositionsFileItems (FileItemEntity item1, FileItemEntity item2) {

        Optional<FileItemEntity> optionalFIE1 = fileItemsRepository.findById(item1.getId());
        optionalFIE1.ifPresent(fie1 -> {
            Optional<FileItemEntity> optionalFIE2 = fileItemsRepository.findById(item2.getId());
            optionalFIE2.ifPresent(fie2 -> {
                fie1.setId(item2.getId());
                fie2.setId(item1.getId());
                fileItemsRepository.save(fie1);
                fileItemsRepository.save(fie2);
            });
        });
    }


    /** Методы добавления файлов и/или папок */
    public void addFiles(AdditionType additionType, boolean isImagesOnly, Stage stage) {

        switch (additionType) {

            case FOLDER_RECURSIVELY -> {
                File dir = dirChooser(stage);
                scanSubFolders(dir, isImagesOnly);
            }

            case FOLDER ->  {
                File dir = dirChooser(stage);
                try {
                    Files.walk(Paths.get(dir.getAbsolutePath()), 1)
                            .distinct().forEach(filePath -> additionFiles(filePath.toFile(), isImagesOnly));
                } catch (IOException | RuntimeException e) {
                    log.error("ERROR: Unreadable file - " + e.getMessage() + "\nThe file will not be added and processed" );
                }
            }

            case FILES -> {
                FileChooser fileChooser = new FileChooser();
                if (!isImagesOnly) {
                    selectFilesDialog(stage, fileChooser);

                } else {
                    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg");
                    fileChooser.getExtensionFilters().add(extensionFilter);
                    selectFilesDialog(stage, fileChooser);
                }
            }
        }
    }

    private void selectFilesDialog(Stage stage, FileChooser fileChooser) {
        List<File> files = fileChooser.showOpenMultipleDialog(stage);
        if (files != null) {
            fileItemsRepository.saveAll(files
                    .stream()
                    .filter(file ->
                            !file.getName().startsWith(".") && file.isFile())
                    .map(file -> new FileItemEntity(
                            file.getAbsolutePath(), file.getName(), file.getName(), file.length()))
                    .collect(Collectors.toList()));
        }
    }

    //вспомогательный метод диалога выбора папки
    private File dirChooser(Stage stage) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.getInitialDirectory();
        File dir;
        dir = dirChooser.showDialog(stage);

        return dir;
    }

    //вспомогательный метод для добавления содержимого папки
    public void additionFiles(File file, boolean isImagesOnly) {

        //если имя файла не начинается с точки, все ок
        if (!file.getName().startsWith(".") && file.isFile()) {

            if (!isImagesOnly) {                                                                                        // условие добавления всех файлов
                fileItemsRepository.save(new FileItemEntity(file.getAbsolutePath(), file.getName(), file.getName(), file.length()));

            } else {                                                                                                    // условие добавления только изображений
                if (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("jpeg")) {
                    fileItemsRepository.save(new FileItemEntity(file.getAbsolutePath(), file.getName(), file.getName(), file.length()));
                }
            }
        }
    }

    @Override
    public void clearItemListOnDB() {
        fileItemsRepository.deleteAll();
    }

    //вспомогательный метод для добавления содержимого папки и подпапок
    private void scanSubFolders(File dir, boolean isImagesOnly) {
        try {
            Files.walk(Paths.get(dir.getAbsolutePath())).forEach(filePath -> {
                if (!Files.isDirectory(filePath)) {
                    additionFiles(filePath.toFile(), isImagesOnly);
                }
            });
        } catch (RuntimeException e) {
            log.error("ERROR Reading file or folder: " + e.getMessage().substring(36));
        } catch (IOException e) {
            log.error("ERROR: " + e.getMessage());
        }
    }
}