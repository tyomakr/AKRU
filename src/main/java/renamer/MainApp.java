package renamer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import renamer.controller.ExifRenamerController;

import renamer.controller.StandartRenamerController;
import renamer.controller.StartMenuController;
import renamer.storage.FieldsValuesStorage;
import renamer.storage.FileItemsStorage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AK Rename Utility");

        //сразу пробрасываем доступ к этому классу для FileItemsStorage
        FileItemsStorage.getInstance().setMainApp(this);

        showStartMenuWindow();
    }

    //показ окна меню
    public void showStartMenuWindow() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/resources/view/StartMenuWindow.fxml"));
            BorderPane startMenuWindow = loader.load();

            Scene scene = new Scene(startMenuWindow);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/startMenu.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

            StartMenuController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //показ окна стандартного переименования
    public void showStandartRenamer() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/resources/view/StandartRenamerWindow.fxml"));
            BorderPane standartRenamerWindow = loader.load();

            Scene scene = new Scene(standartRenamerWindow);
            primaryStage.setScene(scene);
            primaryStage.show();

            StandartRenamerController controller = loader.getController();
            controller.setMainApp(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //показ окна переименования по Exif
    public void showExifRenamer() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/resources/view/ExifRenamerWindow.fxml"));
            BorderPane exifRenamerWindow = loader.load();

            Scene scene = new Scene(exifRenamerWindow);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/exifRenamerWindow.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.show();

            ExifRenamerController controller = loader.getController();
            controller.setMainApp(this);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //возврат к главному меню
    public void backToMenu() {
        getPrimaryStage().close();

        //Чистка storage
        FileItemsStorage.getInstance().getFileItemsList().clear();
        FieldsValuesStorage.getInstance().fieldsSetNull();

        showStartMenuWindow();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
