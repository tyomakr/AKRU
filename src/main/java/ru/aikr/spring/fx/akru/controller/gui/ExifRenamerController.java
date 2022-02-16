package ru.aikr.spring.fx.akru.controller.gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;
import ru.aikr.spring.fx.akru.service.preparing.MaskHandlerService;
import ru.aikr.spring.fx.akru.service.preparing.MetadataHandlerService;
import ru.aikr.spring.fx.akru.service.process.RenamerService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;
import ru.aikr.spring.fx.akru.service.storage.GUIFieldsControlService;

import java.nio.file.Paths;

@Slf4j
@Component
@FxmlView("exif-renamer-window.fxml")
public class ExifRenamerController extends AbstractRenamerController {

    private final MetadataHandlerService metadataHandlerService;

    @FXML
    private BorderPane borderPane;

    //объявляем поля из FXML
    @FXML private ImageView imageViewArea;

    @FXML private TextField textFieldDateTimeShooting;
    @FXML private TextField textFieldAuthor;
    @FXML private TextField textFieldCameraModel;
    @FXML private TextField textFieldFocalLength;
    @FXML private TextField textFieldWidth;
    @FXML private TextField textFieldHeight;
    @FXML private TextField textFieldSoftware;

    public ExifRenamerController(
            FileItemsStorageService fileItemsStorageService,
            GUIFieldsControlService guiFieldsControlService,
            MaskHandlerService maskHandlerService,
            RenamerService renamerService,
            MetadataHandlerService metadataHandlerService) {

        super(
                fileItemsStorageService,
                guiFieldsControlService,
                maskHandlerService,
                renamerService);

        this.metadataHandlerService = metadataHandlerService;
    }


    @Override
    public void initialize() {

        Stage stage = new Stage();
        super.setStage(stage);                                                                                          //для функционала кнопки "к меню"

        stage.setScene(new Scene(borderPane));
        stage.setResizable(false);

        super.setImagesOnlyFlag(true);
        super.initialize();

        initEXIFFXControls();

    }

    private void initEXIFFXControls() {
        //отслеживание выделенного файла в таблице для показа превью фотографии
        super.getTableView().getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> showPhotoPreview(newValue))
        );
        //отслеживание метаданных выделенного файла для вывода в информационных полях
        super.getTableView().getSelectionModel().selectedItemProperty().addListener(
                (((observable, oldValue, newValue) -> showMetadata(newValue)))
        );
    }

    @Override
    public void cleanItemList() {
        textFieldDateTimeShooting.clear();
        imageViewArea.setImage(null);
        super.cleanItemList();
    }

    //показ превью фотокарточки)
    private void showPhotoPreview(FileItemEntity fileItem) {

        if (fileItem != null) {
            String previewPhotoPath = Paths.get(fileItem.getFilePath()).toUri().toString();
            Image previewPhoto = new Image(previewPhotoPath);
            imageViewArea.setImage(previewPhoto);
            imageViewArea.setFitHeight(313);
            imageViewArea.setFitWidth(323);
            centerImage();
        }
    }

    //чтение метаданных для вывода в инфо поля
    private void showMetadata(FileItemEntity fileItem) {

        if (fileItem != null) {
            metadataHandlerService.readMetadata(fileItem);

            textFieldDateTimeShooting.setText(fileItem.getMetadataEntity().getEXIFDate() + " " + fileItem.getMetadataEntity().getEXIFTime());
            textFieldAuthor.setText(fileItem.getMetadataEntity().getAuthor());
            textFieldCameraModel.setText(fileItem.getMetadataEntity().getCameraModel());
            textFieldFocalLength.setText(fileItem.getMetadataEntity().getFocalLength());
            textFieldWidth.setText(fileItem.getMetadataEntity().getImageWidth());
            textFieldHeight.setText(fileItem.getMetadataEntity().getImageHeight());
            textFieldSoftware.setText(fileItem.getMetadataEntity().getSoftware());
        }
    }

    //кнопка ДАТА (съемки)
    public void applyMaskEXIFShootingDate() {
        super.getTextFieldFileNameMask().appendText("[EXIF_D]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldDateTimeShooting.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка ВРЕМЯ (съемки)
    public void applyMaskEXIFShootingTime() {
        super.getTextFieldFileNameMask().appendText("[EXIF_T]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldDateTimeShooting.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка автор (artist)
    public void applyMaskEXIFAuthor() {
        super.getTextFieldFileNameMask().appendText("[EXIF_Author]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldAuthor.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка камера
    public void applyMaskEXIFCamera() {
        super.getTextFieldFileNameMask().appendText("[EXIF_Camera]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldCameraModel.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка фокусное расстояние
    public void applyMaskEXIFFocalLength() {
        super.getTextFieldFileNameMask().appendText("[EXIF_FL]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldFocalLength.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка ширина изображения
    public void applyMaskEXIFWidth() {
        super.getTextFieldFileNameMask().appendText("[EXIF_W]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldWidth.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //кнопка высота изображения
    public void applyMaskEXIFHeight() {
        super.getTextFieldFileNameMask().appendText("[EXIF_H]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldHeight.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    public void applyMaskEXIFSoftware() {
        super.getTextFieldFileNameMask().appendText("[EXIF_SOFT]");
        getGUIFieldsControlService().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
        textFieldSoftware.setText(getGUIFieldsControlService().setOfValuesValue());
    }

    //центрование превьюшки в imageView
    private void centerImage() {
        Image img = imageViewArea.getImage();
        if (img != null) {
            double w;
            double h;

            double ratioX = imageViewArea.getFitWidth() / img.getWidth();
            double ratioY = imageViewArea.getFitHeight() / img.getHeight();

            double reduce = Math.min(ratioX, ratioY);
            w = img.getWidth() * reduce;
            h = img.getHeight() * reduce;

            imageViewArea.setX((imageViewArea.getFitWidth() - w) / 2);
            imageViewArea.setY((imageViewArea.getFitHeight() - h) / 2);
        }
    }
}





