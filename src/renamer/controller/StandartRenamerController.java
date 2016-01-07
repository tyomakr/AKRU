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


public class StandartRenamerController implements RenamerController {

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;

    //объявляем экземпляр maskController и в конструкторе передаем ему этот контроллер
    MaskController maskController = new MaskController();

    //создаем лист для обработки файлов
    private ObservableList<FileItem> fileItemsList = FXCollections.observableArrayList();

    //комбобокс
    private ObservableList<String> comboBoxRegistryList = FXCollections.observableArrayList(
            "Без изменений", "ВЕРХНИЙ РЕГИСТР", "нижний регистр");

    //для метода добавления
    boolean isAddFolderSubfolder = true;
    boolean isAddOnlyFiles = true;

    //объявляем поля из FXML
    @FXML private TextArea consoleArea;
    @FXML private TextField textFieldFileNameMask;
    @FXML private TextField textFieldFileExtMask;

    @FXML private TableView<FileItem> tableView;
    @FXML private TableColumn<FileItem, String> columnOldName;
    @FXML private TableColumn<FileItem, String> columnNewName;
    @FXML private TableColumn<FileItem, String> columnFileSize;
    @FXML private TableColumn<FileItem, String> columnFilePath;

    @FXML private Spinner<Integer> spinnerCounterStartTo;
    @FXML private Spinner<Integer> spinnerCounterStep;
    @FXML private Spinner<Integer> spinnerCounterDigits;

    @FXML private ComboBox<String> comboBoxRegistryChanger;


    //constructor
    public StandartRenamerController() {
        maskController.setRenamerController(this);
    }

    //initialize
    public void initialize() {

        //Первоначальное заполнение полей имени и расширения файла
        textFieldFileNameMask.setText("[N]");
        textFieldFileExtMask.setText("[T]");

        //Инициализация спиннеров
        SpinnerValueFactory<Integer> svfStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE - 1000000, 1); // :-)
        spinnerCounterStartTo.setValueFactory(svfStart);
        SpinnerValueFactory<Integer> svfStep = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024);
        spinnerCounterStep.setValueFactory(svfStep);
        SpinnerValueFactory<Integer> svfDigits = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3);
        spinnerCounterDigits.setValueFactory(svfDigits);

        //Заполнение комбобокса
        comboBoxRegistryChanger.setItems(comboBoxRegistryList);
        comboBoxRegistryChanger.setValue(comboBoxRegistryList.get(0));

        //Отслеживание изменения поля, для автоматического применения маски имени файла
        textFieldFileExtMask.textProperty().addListener((observable, oldValue, newValue) -> {
            maskController.applyMasks();
        });

        //Отслеживание изменения поля, для автоматического применения маски расширения файла
        textFieldFileNameMask.textProperty().addListener((observable, oldValue, newValue) -> {
            maskController.applyMasks();
        });

        //Отслеживание изменения полей спиннеров
        spinnerCounterStartTo.valueProperty().addListener((observable, oldValue, newValue) -> {
            maskController.applyMasks();
        });
        spinnerCounterStep.valueProperty().addListener((observable, oldValue, newValue) ->  {
            maskController.applyMasks();
        });
        spinnerCounterDigits.valueProperty().addListener((observable, oldValue, newValue) -> {
            maskController.applyMasks();
        });

        //TODO listener combobox


    }


    //возврат к главному меню
    public void backToMenu() {
        mainApp.getPrimaryStage().close();
        mainApp.showStartMenuWindow();
    }

    //общий метод для добавления файлов и папок
    private void addFiles() {

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
        //заполняем таблицу
        fillTable();
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

    //добавление содержимого папки и подпапок в таблицу
    public void addFolderSubFoldersItems() {
        isAddFolderSubfolder = true;
        isAddOnlyFiles = false;
        addFiles();
    }

    //запись содержимого папки в таблицу
    public void addFolderItems() {
        isAddFolderSubfolder = false;
        isAddOnlyFiles = false;
        addFiles();
    }

    //добавление только выбранных файлов в таблицу
    public void addItems() {
        isAddFolderSubfolder = false;
        isAddOnlyFiles = true;
        addFiles();
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

        int row = tableView.getSelectionModel().getSelectedIndex();
        //проверяем, если элемент не первый в списке
        if (row > 0) {
            FileItem fileItem1 = fileItemsList.get(row);
            FileItem fileItem2 = fileItemsList.get(row - 1);

            fileItemsList.set(row - 1, fileItem1);
            fileItemsList.set(row, fileItem2);
        }
    }

    //опустить файл в списке
    public void moveItemDown() {

        int row = tableView.getSelectionModel().getSelectedIndex();
        //проверяем, если элемент не последний в списке
        if (row != fileItemsList.size() - 1) {
            FileItem fileItem1 = fileItemsList.get(row);
            FileItem fileItem2 = fileItemsList.get(row + 1);

            fileItemsList.set(row + 1, fileItem1);
            fileItemsList.set(row, fileItem2);
        }

    }

    //заполнение таблицы текущими данными из листа
    public void fillTable() {

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
    //маска счетчика (в имени файла)
    public void applyMaskFileNameCounter() {
        textFieldFileNameMask.appendText("[C]");
        maskController.applyMasks();
    }

    //маска имени файла
    public void applyMaskFileName() {
        textFieldFileNameMask.appendText("[N]");
        maskController.applyMasks();
    }

    //маска имени файла по дате
    public void applyMaskFileNameDate() {
        textFieldFileNameMask.appendText("[YMD]");
        maskController.applyMasks();
    }

    //маска имени файла по времени
    public void applyMaskFileNameTime() {
        textFieldFileNameMask.appendText("[hms]");
        maskController.applyMasks();
    }

    //маска расширения файла
    public void applyMaskFileExtType() {
        textFieldFileExtMask.appendText("[T]");
        maskController.applyMasks();
    }

    //маска счетчика (расширение файла)
    public void applyMaskFileExtCounter(){
        textFieldFileExtMask.appendText("[C]");
        maskController.applyMasks();
    }

    //изменение регистра файлов
    public void changeFilesRegistry() {
        //TODO
    }


    /** setters and getters */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public ObservableList<FileItem> getFileItemsList() {
        return fileItemsList;
    }

    public TextField getTextFieldFileNameMask() {
        return textFieldFileNameMask;
    }

    public TextField getTextFieldFileExtMask() {
        return textFieldFileExtMask;
    }

    public Spinner<Integer> getSpinnerCounterStartTo() {
        return spinnerCounterStartTo;
    }

    public Spinner<Integer> getSpinnerCounterStep() {
        return spinnerCounterStep;
    }

    public Spinner<Integer> getSpinnerCounterDigits() {
        return spinnerCounterDigits;
    }
}