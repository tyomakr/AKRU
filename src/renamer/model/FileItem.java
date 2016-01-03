package renamer.model;


import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;

public class FileItem {

    private File file;

    private StringProperty oldFileName;
    private StringProperty newFileName;
    private LongProperty fileSize;
    private StringProperty filePath;


    //constructor
    public FileItem(File file, String oldFileName, String newFileName, long fileSize, String filePath) {
        this.file = file;
        this.oldFileName = new SimpleStringProperty(oldFileName);
        this.newFileName = new SimpleStringProperty(newFileName);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.filePath = new SimpleStringProperty(filePath);
    }


    //setters and getters
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getOldFileName() {
        return oldFileName.get();
    }

    public StringProperty oldFileNameProperty() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName.set(oldFileName);
    }

    public String getNewFileName() {
        return newFileName.get();
    }

    public StringProperty newFileNameProperty() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName.set(newFileName);
    }

    public long getFileSize() {
        return fileSize.get();
    }

    public LongProperty fileSizeProperty() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize.set(fileSize);
    }

    public String getFilePath() {
        return filePath.get();
    }

    public StringProperty filePathProperty() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
