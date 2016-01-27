package renamer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import renamer.controller.preparing.MetadataController;
import renamer.model.FileItem;
import renamer.storage.FieldsValuesStorage;


public class ExifRenamerController extends AbstractRenamerController{

    //объявляем поля из FXML
    @FXML private ImageView imageViewArea;
    @FXML private Rectangle imageRectangle;

    @FXML private TextField textFieldDateTimeShooting;
    @FXML private TextField textFieldAuthor;
    @FXML private Button buttonExifDate;
    @FXML private Button buttonExifTime;



    @Override
    public void initialize() {

        super.initialize();
        isAddOnlyImages = true;


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
    private void showPhotoPreview(FileItem fileItem) {

        if (fileItem != null) {
            String previewPhotoPath = fileItem.getFile().toURI().toString();
            Image previewPhoto = new Image(previewPhotoPath);
            imageViewArea.setImage(previewPhoto);
            imageViewArea.setFitHeight(313);
            imageViewArea.setFitWidth(323);

            centerImage();
        }
    }

    //чтение метаданных для вывода в инфополя
    private void showMetadata(FileItem fileItem) {

        if (fileItem != null) {
            MetadataController.showMetadataPreview(fileItem);

            textFieldDateTimeShooting.setText(fileItem.getMetadata().getEXIFDate() + " " + fileItem.getMetadata().getEXIFTime());
            textFieldAuthor.setText(fileItem.getMetadata().getAuthor());


        }

    }

    //кнопка ДАТА (съемки)
    public void applyMaskEXIFShootingDate() {
        super.getTextFieldFileNameMask().appendText("[EXIF_D]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
    }

    //кнопка ВРЕМЯ (съемки)
    public void applyMaskEXIFShootingTime() {
        super.getTextFieldFileNameMask().appendText("[EXIF_T]");
        FieldsValuesStorage.getInstance().setTextFieldFileNameMask(super.getTextFieldFileNameMask());
    }


    //центрование превьюшки в imageView
    private void centerImage() {
        Image img = imageViewArea.getImage();
        if (img != null) {
            double w;
            double h;

            double ratioX = imageViewArea.getFitWidth() / img.getWidth();
            double ratioY = imageViewArea.getFitHeight() / img.getHeight();

            double reduce;
            if(ratioX >= ratioY) {
                reduce = ratioY;
            } else {
                reduce = ratioX;
            }

            w = img.getWidth() * reduce;
            h = img.getHeight() * reduce;

            imageViewArea.setX((imageViewArea.getFitWidth() - w) / 2);
            imageViewArea.setY((imageViewArea.getFitHeight() - h) / 2);

        }
    }
}


