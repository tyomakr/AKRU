package ru.aikr.spring.fx.akru.controller.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import ru.aikr.spring.fx.akru.domain.AdditionType;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.logging.ConsoleAreaLogger;
import ru.aikr.spring.fx.akru.logging.StaticOutputStreamAppender;
import ru.aikr.spring.fx.akru.service.preparing.MaskHandlerService;
import ru.aikr.spring.fx.akru.service.process.RenamerService;
import ru.aikr.spring.fx.akru.service.storage.GUIFieldsControlService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;


@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractRenamerController {

    private final FileItemsStorageService fileItemsStorageService;
    private final GUIFieldsControlService GUIFieldsControlService;
    private final MaskHandlerService maskHandlerService;
    private final RenamerService renamerService;

    private Stage stage;

    private ObservableList<FileItemEntity> fileItemEntitiesList;                                                        //создаем лист для обработки файлов
    private ObservableList<String> comboBoxRegisterList;

    private boolean isImagesOnlyFlag = false;

    //объявляем поля из FXML
    @FXML private TextArea consoleArea;
    @FXML private TextField textFieldFileNameMask;
    @FXML private TextField textFieldFileExtMask;

    @FXML public TableView<FileItemEntity> tableView;
    @FXML private TableColumn<FileItemEntity, String> columnOldName;
    @FXML private TableColumn<FileItemEntity, String> columnNewName;
    @FXML private TableColumn<FileItemEntity, String> columnFileSize;
    @FXML private TableColumn<FileItemEntity, String> columnFilePath;

    @FXML private Spinner<Integer> spinnerCounterStartTo;
    @FXML private Spinner<Integer> spinnerCounterStep;
    @FXML private Spinner<Integer> spinnerCounterDigits;

    @FXML private ComboBox<String> comboBoxRegisterChanger;

    public void initialize() {

        fileItemEntitiesList = fileItemsStorageService.getFileItemEntitiesList();
        comboBoxRegisterList = GUIFieldsControlService.getComboBoxRegisterList();

        initFXControls();                                                                                               //инициализация графических компонентов и их листенеров
        initDragAndDropFilesFeature();                                                                                  //реализация добавления файлов в список перетаскиванием
        cleanItemList();
        log.info("READY");

    }

    private void initFXControls() {
        textFieldFileNameMask.setText("[N]");                                                                            //Первоначальное заполнение полей имени и расширения файла
        textFieldFileExtMask.setText("[T]");
        GUIFieldsControlService.setFieldToDefaultValues();

        //Отслеживание изменения поля, для автоматического применения маски имени файла
        textFieldFileNameMask.textProperty().addListener((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setTextFieldFileNameMask(textFieldFileNameMask);
            maskHandlerService.applyMasks();
            fillTable();
        });
        //Отслеживание изменения поля, для автоматического применения маски расширения файла
        textFieldFileExtMask.textProperty().addListener((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setTextFieldFileExtMask(textFieldFileExtMask);
            maskHandlerService.applyMasks();
            fillTable();
        });

        //Инициализация спиннеров
        SpinnerValueFactory<Integer> svfStart = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE - 1000000, 1); // :-)
        spinnerCounterStartTo.setValueFactory(svfStart);
        GUIFieldsControlService.setSpinnerCounterStartTo(spinnerCounterStartTo);
        SpinnerValueFactory<Integer> svfStep = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1024);
        spinnerCounterStep.setValueFactory(svfStep);
        GUIFieldsControlService.setSpinnerCounterStep(spinnerCounterStep);
        SpinnerValueFactory<Integer> svfDigits = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3);
        spinnerCounterDigits.setValueFactory(svfDigits);
        GUIFieldsControlService.setSpinnerCounterDigits(spinnerCounterDigits);

        //Отслеживание изменения полей спиннеров
        spinnerCounterStartTo.valueProperty().addListener((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setSpinnerCounterStartTo(spinnerCounterStartTo);
            maskHandlerService.applyMasks();
            fillTable();
        });
        spinnerCounterStep.valueProperty().addListener((observable, oldValue, newValue) ->  {
            GUIFieldsControlService.setSpinnerCounterStep(spinnerCounterStep);
            maskHandlerService.applyMasks();
            fillTable();
        });
        spinnerCounterDigits.valueProperty().addListener((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setSpinnerCounterDigits(spinnerCounterDigits);
            maskHandlerService.applyMasks();
            fillTable();
        });


        //Заполнение комбобокса
        comboBoxRegisterChanger.setItems(comboBoxRegisterList);
        comboBoxRegisterChanger.setValue(comboBoxRegisterList.get(0));

        //Отслеживание изменения значений комбобокса
        comboBoxRegisterChanger.valueProperty().addListener(((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setComboBoxRegisterList(comboBoxRegisterList);
            maskHandlerService.applyRegister(newValue);}));

        //Заполнение комбобокса
        comboBoxRegisterChanger.setItems(comboBoxRegisterList);
        comboBoxRegisterChanger.setValue(comboBoxRegisterList.get(0));

        //Отслеживание изменения значений комбобокса
        comboBoxRegisterChanger.valueProperty().addListener(((observable, oldValue, newValue) -> {
            GUIFieldsControlService.setComboBoxRegisterList(comboBoxRegisterList);
            maskHandlerService.applyRegister(newValue);
            fillTable();}));


        StaticOutputStreamAppender.setStaticOutputStream(new ConsoleAreaLogger(consoleArea));                           //лог в textarea
    }

    private void initDragAndDropFilesFeature() {

        getTableView().setOnDragDetected(event -> {
            Dragboard dragboard = getTableView().startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            if (getTableView().getSelectionModel().getSelectedItem() != null) {
                content.putString(getTableView().getSelectionModel().getSelectedItem().getFilePath());
                dragboard.setContent(content);
            }
        });
        getTableView().setOnDragEntered(event -> getTableView().setBlendMode(BlendMode.EXCLUSION));
        getTableView().setOnDragExited(event -> getTableView().setBlendMode(null));
        getTableView().setOnDragOver(event -> event.acceptTransferModes(TransferMode.LINK));

        //добавление перетаскиванием
        getTableView().setOnDragDropped(event -> {
            //если перетащили как минимум 1 элемент или больше
            if (event.getDragboard().getFiles().size() >= 1) {
                //проход по этим элементам
                for (int i = 0; i < event.getDragboard().getFiles().size(); i++) {
                    File currentFile = event.getDragboard().getFiles().get(i);
                    try {
                        Files.walk(Paths.get(currentFile.getAbsolutePath())).forEach(filePath -> {
                            if (!Files.isDirectory(filePath)) {
                                fileItemsStorageService.additionFiles(filePath.toFile(), isImagesOnlyFlag);
                            }
                        });
                    } catch (Exception e) {
                        log.error("ERROR - " + e.getMessage());
                    }
                }
            }
            fillTable();
            event.consume();
        });
    }

    public void fillTable() {                                                                                           //заполнение таблицы текущими данными из листа

        columnOldName.setCellValueFactory(new PropertyValueFactory<>("oldFileName"));
        columnNewName.setCellValueFactory(new PropertyValueFactory<>("newFileName"));
        columnFileSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        columnFilePath.setCellValueFactory(new PropertyValueFactory<>("filePath"));

        fileItemEntitiesList = fileItemsStorageService.getFileItemEntitiesList();                                       //refresh
        tableView.setItems(fileItemEntitiesList);
    }

                                                                                                                        //возврат к главному меню
    public void backToStartMenu() {
        cleanItemList();
        stage.close();
    }

    public void addFolderSubFoldersItems() {
        fileItemsStorageService.addFiles(AdditionType.FOLDER_RECURSIVELY, isImagesOnlyFlag, stage);
        fillTable();
    }

    public void addFolderItems() {
        fileItemsStorageService.addFiles(AdditionType.FOLDER, isImagesOnlyFlag, stage);
        fillTable();
    }

    public void addFileItem() {
        fileItemsStorageService.addFiles(AdditionType.FILES, isImagesOnlyFlag, stage);
        fillTable();
    }

    public void removeFileItem() {
        int row = tableView.getSelectionModel().getSelectedIndex();
        if (row >= 0) {
            fileItemsStorageService.removeFileItemEntity(row);
        }
        fillTable();
    }

    //поднять файл в списке
    public void moveItemUp() {

        int row = tableView.getSelectionModel().getSelectedIndex();
        //проверяем, если элемент не первый в списке
        if (row > 0) {
            FileItemEntity fileItemEntity1 = fileItemEntitiesList.get(row);
            FileItemEntity fileItemEntity2 = fileItemEntitiesList.get(row - 1);

            fileItemEntitiesList.set(row - 1, fileItemEntity1);
            fileItemEntitiesList.set(row, fileItemEntity2);

            fileItemsStorageService.swapPositionsFileItems(fileItemEntity1, fileItemEntity2);
        }
    }

    //опустить файл в списке
    public void moveItemDown() {

        int row = tableView.getSelectionModel().getSelectedIndex();
        //проверяем, если элемент не последний в списке
        if (row != fileItemEntitiesList.size() - 1) {
            FileItemEntity fileItemEntity1 = fileItemEntitiesList.get(row);
            FileItemEntity fileItemEntity2 = fileItemEntitiesList.get(row + 1);

            fileItemEntitiesList.set(row + 1, fileItemEntity1);
            fileItemEntitiesList.set(row, fileItemEntity2);

            fileItemsStorageService.swapPositionsFileItems(fileItemEntity2, fileItemEntity1);
        }
    }

    //очистка списка файлов
    public void cleanItemList() {
        fileItemEntitiesList.clear();
        fileItemsStorageService.clearItemListOnDB();
        GUIFieldsControlService.setFieldToDefaultValues();
    }

    //кнопка выполнить
    public void runFileRenamerProcess() {
        if (fileItemEntitiesList.size() != 0) {
            log.info("Renaming " + fileItemEntitiesList.size() + " files...");
            renamerService.startRenameProcess();
            log.info("* DONE *");
            cleanItemList();
        }
    }


    /** Маски */
    //маска счетчика (в имени файла)
    public void applyMaskFileNameCounter() {
        textFieldFileNameMask.appendText("[C]");
        GUIFieldsControlService.setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла
    public void applyMaskFileName() {
        textFieldFileNameMask.appendText("[N]");
        GUIFieldsControlService.setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла по дате
    public void applyMaskFileNameDate() {
        textFieldFileNameMask.appendText("[YMD]");
        GUIFieldsControlService.setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска имени файла по времени
    public void applyMaskFileNameTime() {
        textFieldFileNameMask.appendText("[hms]");
        GUIFieldsControlService.setTextFieldFileNameMask(textFieldFileNameMask);
    }
    //маска расширения файла
    public void applyMaskFileExtType() {
        textFieldFileExtMask.appendText("[T]");
        GUIFieldsControlService.setTextFieldFileExtMask(textFieldFileExtMask);
    }
    //маска счетчика (расширение файла)
    public void applyMaskFileExtCounter(){
        textFieldFileExtMask.appendText("[C]");
        GUIFieldsControlService.setTextFieldFileExtMask(textFieldFileExtMask);
    }
}
