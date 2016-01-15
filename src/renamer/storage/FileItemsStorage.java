package renamer.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import renamer.MainApp;
import renamer.model.FileItem;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/** Синглтон хранилище списка файлов */

public class FileItemsStorage {

    private static FileItemsStorage instance = new FileItemsStorage();

    //доступ к экземпляру mainApp
    private MainApp mainApp;

    //список, где будут храниться файлы для работы
    private ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();


    /** Методы добавления файлов и/или папок */
    public void addFiles(boolean isAddFolderSubfolder, boolean isAddOnlyFiles) {

        /** условие добавления папки и всех подпапок */
        if (isAddFolderSubfolder && !isAddOnlyFiles) {
            File dir = dirChooser();

            //скан и добавление, включая подпапки
            generateSubFoldersItemList(dir, fileItemsList);
        }

        /** условие добавления содержимого только текущей папки */
        else if (!isAddFolderSubfolder && !isAddOnlyFiles) {
            File dir = dirChooser();

            for (File file : dir.listFiles()) {
                //если имя файла не начинается с точки, все ок
                if (!file.getName().startsWith(".") && file.isFile()) {
                    fileItemsList.add(new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath()));
                }
            }
        }

        /** условие добавления только файлов */
        else if (!isAddFolderSubfolder) {

            FileChooser fileChooser = new FileChooser();
            List<File> files = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
            fileItemsList.addAll(files.stream().filter(file -> !file.getName().startsWith(".") && file.isFile()).map(file -> new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath())).collect(Collectors.toList()));
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

    //вспомогательный метод для добавления содержимого папки и подпапок
    private void generateSubFoldersItemList(File dir, ObservableList<FileItem> fileItemsList) {

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                generateSubFoldersItemList(file, fileItemsList);
                continue;
            }
            if (file.isFile()) {
                //если имя файла не начинается с точки, все ок
                if (!file.getName().startsWith(".")) {
                    fileItemsList.add(new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath()));
                }
            }
        }
    }


    //setters ang getters
    public ObservableList<FileItem> getFileItemsList() {return fileItemsList;}

    public static FileItemsStorage getInstance() {return instance;}

    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}
}
