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

        //определяем кол-во потоков (вирт.ядер)
        int qtyThreads = Runtime.getRuntime().availableProcessors();

        //делитель потоков
        int elementsOnThread = fileItemsList.size() / qtyThreads;



    }

    //161304


    //треды
    class renThread extends Thread {

        @Override
        public void run() {
            //TODO
        }
    }

}
