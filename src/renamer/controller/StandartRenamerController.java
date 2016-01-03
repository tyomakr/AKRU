package renamer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import renamer.MainApp;
import renamer.model.FileItem;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


public class StandartRenamerController {

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;

    //создаем лист для обработки файлов
    private ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();

    //объявляем поля из FXML
    @FXML private TextArea consoleArea;
    @FXML private TextField textFieldFileNameMask;
    @FXML private TextField textFieldFileExtMask;

    @FXML private Button buttonBackToMenu;
    @FXML private Button buttonAddFolderItems;
    @FXML private Button buttonAddItems;
    @FXML private Button buttonRemoveItem;
    @FXML private Button buttonItemUp;
    @FXML private Button buttonItemDown;
    @FXML private Button buttonCleanItemsList;

    @FXML private Button buttonMaskFileName;

    @FXML private TableView<FileItem> tableView;
    @FXML private TableColumn<FileItem, String> columnOldName;
    @FXML private TableColumn<FileItem, String> columnNewName;
    @FXML private TableColumn<FileItem, String> columnFileSize;
    @FXML private TableColumn<FileItem, String> columnFilePath;


    //constructor
    public StandartRenamerController() {}

    //initialize
    private void initialize() {}


    //возврат к главному меню
    public void backToMenu() {
        mainApp.getPrimaryStage().close();
        mainApp.showStartMenuWindow();
    }

    //добавление содержимого папки в таблицу
    public void addFolderItems() {

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.getInitialDirectory();
        File dir = dirChooser.showDialog(mainApp.getPrimaryStage());

        consoleArea.setText("Выбрана папка " + dir.getAbsolutePath());

        //скан и добавление
        createItemList(dir, fileItemsList);

        //заполняем таблицу
        fillTable();

    }

    //добавление файла в таблицу
    public void addItems() {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
        fileItemsList.addAll(files.stream().map(file -> new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath())).collect(Collectors.toList()));
        //заполняем таблицу
        fillTable();

    }

    //удаление файла из таблицы
    public void removeItem() {

        int row = tableView.getSelectionModel().getSelectedIndex();
        if (row >= 0) {
            fileItemsList.remove(row);
        }

    }

    //поднять файл в списке
    public void moveItemUp() {

    }

    //опустить файл в списке
    public void moveItemDown() {

    }

    //запись списка файлов из выбранной папки в лист
    private void createItemList(File dir, ObservableList<FileItem> fileItemsList) {

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                createItemList(file, fileItemsList);
                continue;
            }
            if (file.isFile()) {
                fileItemsList.add(new FileItem(file, file.getName(), file.getName(), file.length(), file.getAbsolutePath()));
            }
        }
    }

    //заполнение таблицы текущими данными из листа
    private void fillTable() {

        columnOldName.setCellValueFactory(new PropertyValueFactory<>("oldFileName"));
        columnNewName.setCellValueFactory(new PropertyValueFactory<>("newFileName"));
        columnFileSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        columnFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

        tableView.setItems(fileItemsList);
    }

    //очистка списка файлов
    public void cleanItemList() {
        fileItemsList.clear();
        consoleArea.appendText("\nСписок файлов очищен");
    }

    /** Маски */
    //чтение маски и превью
    private void applyMasks() {

        consoleArea.setText("Применение маски: ");

        for (FileItem fileItem : fileItemsList) {

            //Получаем полное имя файла
            String oldFileName = fileItem.getOldFileName();

            //Если файл не содержит точку в названии (файлы с именеи без расширения)
            if (!oldFileName.contains(".")) {
                //подменияем символы, согласно шаблонов масок
                String newFileName = replaceFileNameTemplates(oldFileName);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName);
            }

            //Если полное имя файла начинается с точки (файлы только с расширением /nix системы)
            else if (oldFileName.startsWith(".")) {
                //ничего не делаем, записываем новое имя файла таким-же
                fileItem.setNewFileName(oldFileName);
            }

            //В обычных случаях, когда имя и расширение присутствуют
            else {

                //отрезаем расширение файла
                oldFileName = oldFileName.substring(0, oldFileName.lastIndexOf("."));
                //подменияем символы, согласно шаблонов масок
                String newFileName = replaceFileNameTemplates(oldFileName);
                //получаем расширение файла
                int extPosition = fileItem.getOldFileName().lastIndexOf(".") + 1;
                String extension = fileItem.getOldFileName().substring(extPosition);
                //записываем новое имя файла в fileItem
                fileItem.setNewFileName(newFileName + "." + extension);
            }

            consoleArea.appendText("\nНовое имя файла = " + fileItem.getNewFileName());
        }
    }

    //Шаблоны для замены по маске
    private String replaceFileNameTemplates(String oldFileName) {
        //получаем маски с textField
        String maskFileName = textFieldFileNameMask.getText();
        //Применяем шаблоны масок
        String newFileName = maskFileName.replaceAll("\\[N\\]", oldFileName);

        return newFileName;
    }

    //маска имени файла
    public void applyMaskFileName() {
        textFieldFileNameMask.appendText("[N]");
        applyMasks();
    }


    //setters and getters
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}


