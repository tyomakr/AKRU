package renamer.controller;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import renamer.model.FileItem;
import java.io.File;


public class ExifRenamerController extends AbstractRenamerController{

    //объявляем поля из FXML
    @FXML private ImageView imageViewArea;
    @FXML private Rectangle imageRectangle;

    @FXML private TextField dateTimeShooting;


    @Override
    public void initialize() {

        super.initialize();
        isAddOnlyImages = true;

        //отслеживание выделенного файла в таблице для показа превью фотографии
        super.tableView.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> showPhotoPreview(newValue))
        );

        //test
        super.tableView.getSelectionModel().selectedItemProperty().addListener(
                (((observable, oldValue, newValue) -> readMetadata(newValue)))
        );


    }

    //показ превью фотокарточки)
    private void showPhotoPreview(FileItem fileItem) {

        String previewPhotoPath = fileItem.getFile().toURI().toString();
        Image previewPhoto = new Image(previewPhotoPath);
        imageViewArea.setImage(previewPhoto);
        imageViewArea.setFitHeight(313);
        imageViewArea.setFitWidth(323);

        centerImage();
    }


    private void readMetadata(FileItem fileItem) {

        File jpegFile = fileItem.getFile();
        try {

            Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            String exifDateTimeShooting = directory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

            exifDateTimeShooting = exifDateTimeShooting.replaceAll(":", "-");
            dateTimeShooting.setText(exifDateTimeShooting);

                




        } catch (Exception e) {
            LOGGER.error("ОШИБКА: " + e.getMessage());
        }


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


