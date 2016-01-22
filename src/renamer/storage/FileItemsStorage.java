package renamer.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import renamer.MainApp;
import renamer.model.FileItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/** Синглтон хранилище списка файлов */

public class FileItemsStorage {

    private static FileItemsStorage instance = new FileItemsStorage();

    //доступ к экземпляру mainApp
    private MainApp mainApp;

    private Logger LOGGER;

    //список, где будут храниться файлы для работы
    private ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();

    /** Методы добавления файлов и/или папок */
    public void addFiles(boolean isAddFolderSubfolder, boolean isAddOnlyFiles, boolean isAddOnlyImages) {


        /** Если добавляем файлы папками */
        if (!isAddOnlyFiles) {
            File dir = dirChooser();

            /** условие добавления подпапок */
            if (isAddFolderSubfolder) {
                scanSubFolders(dir, isAddOnlyImages);
            }
            /** условие добавления без подпапок */
            else if (!isAddFolderSubfolder) {
                try {
                    Files.walk(Paths.get(dir.getAbsolutePath()), 1)
                            .distinct().forEach(filePath -> additionFiles(filePath.toFile(), isAddOnlyImages));
                } catch (IOException | RuntimeException e) {
                    LOGGER.error("ОШИБКА чтения :" + e.getMessage() + "\nФайл НЕ будет добавлен и обработан");
                }
            }
        }

        /** Если добавляем только отдельные файлы */
        else if (isAddOnlyFiles) {
            FileChooser fileChooser = new FileChooser();

            /** условие добавления всех файлов */
            if (!isAddOnlyImages) {
                List<File> files = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
                if (files != null)
                    fileItemsList.addAll(files.stream().filter(file -> !file.getName().startsWith(".") && file.isFile()).map(file -> new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath())).collect(Collectors.toList()));
            }
            /** условие добавления только изображений */ //пока только JPEG
            else if (isAddOnlyImages) {
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg");
                fileChooser.getExtensionFilters().add(extFilter);
                List<File> files = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
                if (files != null)
                    fileItemsList.addAll(files.stream().filter(file -> !file.getName().startsWith(".") && file.isFile()).map(file -> new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath())).collect(Collectors.toList()));
            }
        }
    }

    //вспомогательный метод диалога выбора папки
    private File dirChooser() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.getInitialDirectory();
        File dir;
        dir = dirChooser.showDialog(mainApp.getPrimaryStage());

        return dir;
    }

    //вспомогательный метод для добавления содержимого папки
    private void additionFiles(File file, boolean isAddOnlyImages) {

        //если имя файла не начинается с точки, все ок
        if (!file.getName().startsWith(".") && file.isFile()) {

            /** условие добавления всех файлов */
            if (!isAddOnlyImages) {
                fileItemsList.add(new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath()));
            }

            /** условие добавления только изображений */
            else if (isAddOnlyImages && (
                    file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) ||
                    file.getName().endsWith(".JPG") || file.getName().endsWith(".JPEG")) {
                fileItemsList.add(new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath()));
            }
        }
    }

    //вспомогательный метод для добавления содержимого папки и подпапок
    private void scanSubFolders(File dir, boolean isAddOnlyImages) {

        try {
            Files.walk(Paths.get(dir.getAbsolutePath())).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    additionFiles(filePath.toFile(), isAddOnlyImages);
                }
            });
        } catch (RuntimeException e) {
            LOGGER.error("ОШИБКА ЧТЕНИЯ ФАЙЛА/ПАПКИ - " + e.getMessage().substring(36));
        } catch (IOException e) {
            LOGGER.error("ОШИБКА " + e.getMessage());
        }
    }


    //setters ang getters
    public ObservableList<FileItem> getFileItemsList() {return fileItemsList;}

    public static FileItemsStorage getInstance() {return instance;}

    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}

    public void setLOGGER(Logger LOGGER) {this.LOGGER = LOGGER;}
}
