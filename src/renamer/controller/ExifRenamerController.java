package renamer.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logger.ConsoleAreaAppender;
import org.apache.log4j.Logger;
import renamer.MainApp;
import renamer.controller.preparing.MaskController;
import renamer.model.FileItem;
import renamer.storage.FieldsValuesStorage;
import renamer.storage.FileItemsStorage;


public class ExifRenamerController {

    //создание логгера
    public static final Logger LOGGER = Logger.getLogger(ExifRenamerController.class);

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


    //setters and getters
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


}
