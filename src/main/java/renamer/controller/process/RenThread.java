package renamer.controller.process;

import javafx.collections.ObservableList;
import org.apache.log4j.Logger;
import renamer.model.FileItem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RenThread extends Thread {

    private int startThreadElementIndex;
    private int endThreadElementIndex;
    private ObservableList<FileItem> fileItemsList;
    private Logger LOGGER;
    private int brokenFilesCounter = 0;

    @Override
    public void run() {

        //перебираем элементы, соответствующие переданным индексам
        for (int i = startThreadElementIndex; i <= endThreadElementIndex; i++) {

            Path folder = Paths.get(fileItemsList.get(i).getFile().getParent());

            File original = folder.resolve(fileItemsList.get(i).getOldFileName()).toFile();
            File newFile = folder.resolve(fileItemsList.get(i).getNewFileName()).toFile();

            if (original.exists() & original.isFile() & original.canWrite()) {
                original.renameTo(newFile);
                LOGGER.info("Файл " + original.getAbsolutePath() + " успешно переименован в " + newFile.getName());
            }
            else {
                LOGGER.warn("Проблема с переименованием файла " + original.getAbsolutePath() + " Возможно файл уже не существует или имя файла содержит недопустимые символы. ПРОПУСК ФАЙЛА");
                brokenFilesCounter ++;
            }
        }
    }


    public void setStartThreadElementIndex(int startThreadElementIndex) {this.startThreadElementIndex = startThreadElementIndex;}

    public void setEndThreadElementIndex(int endThreadElementIndex) {this.endThreadElementIndex = endThreadElementIndex;}

    public void setFileItemsList(ObservableList<FileItem> fileItemsList) {
        this.fileItemsList = fileItemsList;
    }

    public void setLOGGER(Logger LOGGER) {this.LOGGER = LOGGER;}

    public int getBrokenFilesCounter() {return brokenFilesCounter;}
}