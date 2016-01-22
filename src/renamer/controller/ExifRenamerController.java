package renamer.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import renamer.model.FileItem;


public class ExifRenamerController extends AbstractRenamerController{

    //объявляем поля из FXML
    @FXML private ImageView imageViewArea;
    @FXML private Rectangle imageRectangle;


    @Override
    public void initialize() {

        super.initialize();
        isAddOnlyImages = true;



        //отслеживание выделенного файла в таблице для превью фотографии
        super.tableView.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> showPhotoPreview(newValue))
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


