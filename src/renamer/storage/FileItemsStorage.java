package renamer.storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import renamer.model.FileItem;

/** Синглтон - хранилище списка файлов */

public class FileItemsStorage {

    private static FileItemsStorage instance = new FileItemsStorage();

    //список, где будут храниться файлы для работы
    private ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();

    //constructor
    private FileItemsStorage() {}
    


    //setters ang getters
    public ObservableList<FileItem> getFileItemsList() {return fileItemsList;}
    public void setFileItemsList(ObservableList<FileItem> fileItemsList) {this.fileItemsList = fileItemsList;}

    public static void setInstance(FileItemsStorage instance) {FileItemsStorage.instance = instance;}
    public static FileItemsStorage getInstance() {
        return instance;
    }
}
