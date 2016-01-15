package renamer.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import logger.ConsoleAreaAppender;
import org.apache.log4j.Logger;
import renamer.MainApp;
import renamer.controller.preparing.MaskController;
import renamer.controller.process.FileRenamerProcess;
import renamer.model.FileItem;
import renamer.storage.FieldsValuesStorage;
import renamer.storage.FileItemsStorage;


public class StandartRenamerController {

    //создание логгера
    public static final Logger LOGGER = Logger.getLogger(StandartRenamerController.class);

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;

    //объявляем экземпляр maskController и в конструкторе передаем ему этот контроллер
    MaskController maskController = new MaskController();

    //создаем лист для обработки файлов
    private ObservableList<FileItem> fileItemsList = FileItemsStorage.getInstance().getFileItemsList();
    //комбобокс
    private ObservableList<String> comboBoxRegisterList = FieldsValuesStorage.getInstance().getComboBoxRegisterList();
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

    @FXML private ComboBox<String> comboBoxRegisterChanger;


    //initialize
    public void initialize() {

        //Инициализация логгера
        ConsoleAreaAppender consoleAreaAppender = new ConsoleAreaAppender();
        ConsoleAreaAppender.setTextArea(consoleArea);
        LOGGER.addAppender(consoleAreaAppender);


        //Первоначальное заполнение полей имени и расширения файла
        textFieldFileNameMask.setText("[N]");
        textFieldFileExtMask.setText("[T]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
        FieldsValuesStorage.getInstance().setTextFieldFileExtMask(textFieldFileExtMask);


        //Отслеживание изменения поля, для автоматического применения маски имени файла
        textFieldFileNameMask.textProperty().addListener((observable, oldValue, newValue) -> {
            FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
            maskController.applyMasks();
        });
        //Отслеживание изменения поля, для автоматического применения маски расширения файла
        textFieldFileExtMask.textProperty().addListener((observable, oldValue, newValue) -> {
            FieldsValuesStorage.getInstance().setTextFieldFileExtMask(textFieldFileExtMask);
            maskController.applyMasks();
        });


        //Инициализация спиннеров
        SpinnerValueFactory<Integer> svfStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE - 1000000, 1); // :-)
        spinnerCounterStartTo.setValueFactory(svfStart);
        FieldsValuesStorage.getInstance().setSpinnerCounterStartTo(spinnerCounterStartTo);
        SpinnerValueFactory<Integer> svfStep = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024);
        spinnerCounterStep.setValueFactory(svfStep);
        FieldsValuesStorage.getInstance().setSpinnerCounterStep(spinnerCounterStep);
        SpinnerValueFactory<Integer> svfDigits = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3);
        spinnerCounterDigits.setValueFactory(svfDigits);
        FieldsValuesStorage.getInstance().setSpinnerCounterDigits(spinnerCounterDigits);

        //Отслеживание изменения полей спиннеров
        spinnerCounterStartTo.valueProperty().addListener((observable, oldValue, newValue) -> {
            FieldsValuesStorage.getInstance().setSpinnerCounterStartTo(spinnerCounterStartTo);
            maskController.applyMasks();
        });
        spinnerCounterStep.valueProperty().addListener((observable, oldValue, newValue) ->  {
            FieldsValuesStorage.getInstance().setSpinnerCounterStep(spinnerCounterStep);
            maskController.applyMasks();
        });
        spinnerCounterDigits.valueProperty().addListener((observable, oldValue, newValue) -> {
            FieldsValuesStorage.getInstance().setSpinnerCounterDigits(spinnerCounterDigits);
            maskController.applyMasks();
        });


        //Заполнение комбобокса
        comboBoxRegisterChanger.setItems(comboBoxRegisterList);
        comboBoxRegisterChanger.setValue(comboBoxRegisterList.get(0));

        //Отслеживание изменения значений комбобокса
        comboBoxRegisterChanger.valueProperty().addListener(((observable, oldValue, newValue) -> {
            FieldsValuesStorage.getInstance().setComboBoxRegisterList(comboBoxRegisterList);
            maskController.applyRegister(newValue);}));

        LOGGER.info("Готов к работе");
    }


    //возврат к главному меню
    public void backToMenu() {
        mainApp.backToMenu();
    }

    //кнопка выполнить
    public void runFileRenamerProcess() {

        if (fileItemsList.size() != 0) {

            LOGGER.info("Переименование " + fileItemsList.size() + " файлов");

            FileRenamerProcess frp = new FileRenamerProcess(LOGGER);
            frp.rename();

            LOGGER.info("* ВЫПОЛНЕНО *");
            cleanItemList();

        }

    }

    //добавление содержимого папки и подпапок в таблицу
    public void addFolderSubFoldersItems() {
        isAddFolderSubfolder = true;
        isAddOnlyFiles = false;
            //передаем параметры этого пункта в метод добавления
        FileItemsStorage.getInstance().addFiles(isAddFolderSubfolder, isAddOnlyFiles);
            //заполняем таблицу
        fillTable();

    }

    //запись содержимого папки в таблицу
    public void addFolderItems() {
        isAddFolderSubfolder = false;
        isAddOnlyFiles = false;
            //передаем параметры этого пункта в метод добавления
        FileItemsStorage.getInstance().addFiles(isAddFolderSubfolder, isAddOnlyFiles);
            //заполняем таблицу
        fillTable();
    }

    //добавление только выбранных файлов в таблицу
    public void addItems() {
        isAddFolderSubfolder = false;
        isAddOnlyFiles = true;
            //передаем параметры этого пункта в метод добавления
        FileItemsStorage.getInstance().addFiles(isAddFolderSubfolder, isAddOnlyFiles);
            //заполняем таблицу
        fillTable();
    }

    //удаление файла из таблицы
    public void removeItem() {

        int row = tableView.getSelectionModel().getSelectedIndex();
        if (row >= 0) {
            fileItemsList.remove(row);
        }
        LOGGER.info("В списке " + fileItemsList.size() + " файлов");
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

        LOGGER.info("В списке " + fileItemsList.size() + " файлов");
    }

    //очистка списка файлов
    public void cleanItemList() {
        fileItemsList.clear();
        FieldsValuesStorage.getInstance().setDefaultValuesFields();
        LOGGER.info("Список файлов очищен");
    }


    /** Маски */
    //маска счетчика (в имени файла)
    public void applyMaskFileNameCounter() {
        textFieldFileNameMask.appendText("[C]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла
    public void applyMaskFileName() {
        textFieldFileNameMask.appendText("[N]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла по дате
    public void applyMaskFileNameDate() {
        textFieldFileNameMask.appendText("[YMD]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла по времени
    public void applyMaskFileNameTime() {
        textFieldFileNameMask.appendText("[hms]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска расширения файла
    public void applyMaskFileExtType() {
        textFieldFileExtMask.appendText("[T]");
        FieldsValuesStorage.getInstance().setTextFieldFileExtMask(textFieldFileExtMask);
    }
    //маска счетчика (расширение файла)
    public void applyMaskFileExtCounter(){
        textFieldFileExtMask.appendText("[C]");
        FieldsValuesStorage.getInstance().setTextFieldFileExtMask(textFieldFileExtMask);
    }


    /** setters and getters */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}