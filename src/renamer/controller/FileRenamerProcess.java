package renamer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import renamer.model.FileItem;

public class FileRenamerProcess {

    //список файлов
    private volatile ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();


    //constructor
    public FileRenamerProcess(ObservableList<FileItem> fileItemsList) {
        this.fileItemsList = fileItemsList;
    }

    //главный метод
    public void rename() {

    }


    //треды
    //161304


}
