package renamer.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logger.ConsoleAreaAppender;
import org.apache.log4j.Logger;
import renamer.MainApp;
import renamer.model.FileItem;
import renamer.storage.FileItemsStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LiveViewController {

    //создание логгера
    public static final Logger LOGGER = Logger.getLogger(AbstractRenamerController.class);

    //даем контроллеру доступ к экземпляру mainApp
    public MainApp mainApp;

    //создаем лист для обработки файлов
    private ObservableList<FileItem> fileItemsList = FileItemsStorage.getInstance().getFileItemsList();

    //объявляем поля из FXML
    @FXML private ScrollPane scrollPaneRoot;
    @FXML private TilePane tilePane;

    @FXML private TextArea consoleArea;
    @FXML private CheckBox checkboxScanSubFolders;

    @FXML private TextField textFieldSourcePath;

    @FXML private Button buttonAddFolder;


    //initialize
    public void initialize() {

        //Инициализация логгера
        ConsoleAreaAppender consoleAreaAppender = new ConsoleAreaAppender();
        ConsoleAreaAppender.setTextArea(consoleArea);
        LOGGER.addAppender(consoleAreaAppender);


        //инициализация рабочего пространства
        scrollPaneRoot.setStyle("-fx-background-color: #333333");
        scrollPaneRoot.setPadding(new Insets(5, 5, 5, 5));
        tilePane.setPadding(new Insets(15, 15, 15, 15));
        tilePane.setHgap(15);



        LOGGER.info("Готов к работе");
    }




    //возврат к главному меню
    public void backToMenu() {
        mainApp.backToMenu();
        LOGGER.removeAllAppenders();
    }

    //кнопка добавление папки
    public void addFolder() {



        //если чекбокс отмечен
        if (checkboxScanSubFolders.isSelected()) {
            FileItemsStorage.getInstance().addFiles(true, false, true);
        }
        //а если не отмечен
        else if (!checkboxScanSubFolders.isSelected()) {
            FileItemsStorage.getInstance().addFiles(false, false, true);
        }


        showImagesPreviews();

        LOGGER.info("Выбрано " + fileItemsList.size() + " файлов");

    }

    //показ эскизов изображений
    public void showImagesPreviews() {

        if (FileItemsStorage.getInstance().getFileItemsList().size() != 0) {

            for (FileItem fileItem : FileItemsStorage.getInstance().getFileItemsList()) {

                ImageView imageView = createImageView(fileItem.getFile());
                tilePane.getChildren().addAll(imageView);
            }

            scrollPaneRoot.setContent(tilePane);
        }



    }

    private ImageView createImageView(final File imageFile) {

        ImageView imageView = null;
        try {
            final Image image = new Image(new FileInputStream(imageFile), 150, 0, true, true);
            imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setOnMouseClicked(mouseEvent -> {

                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                    if(mouseEvent.getClickCount() == 2){
                        try {
                            BorderPane borderPane = new BorderPane();
                            ImageView imageView1 = new ImageView();
                            Image image1 = new Image(new FileInputStream(imageFile));
                            imageView1.setImage(image1);
                            imageView1.setStyle("-fx-background-color: BLACK");
                            imageView1.setFitHeight(mainApp.getPrimaryStage().getHeight() - 10);
                            imageView1.setPreserveRatio(true);
                            imageView1.setSmooth(true);
                            imageView1.setCache(true);
                            borderPane.setCenter(imageView1);
                            borderPane.setStyle("-fx-background-color: BLACK");

                            Stage newStage = new Stage();
                            newStage.setWidth(mainApp.getPrimaryStage().getWidth());
                            newStage.setHeight(mainApp.getPrimaryStage().getHeight());
                            newStage.setTitle(imageFile.getName());
                            Scene scene = new Scene(borderPane, Color.BLACK);
                            newStage.setScene(scene);
                            newStage.show();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return imageView;
    }


    //setters and getters
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
