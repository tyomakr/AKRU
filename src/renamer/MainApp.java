package renamer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import renamer.controller.StandartRenamerController;
import renamer.controller.StartMenuController;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;


    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AK Rename Utility");

        showStartMenuWindow();
    }

    //показ окна меню
    public void showStartMenuWindow() {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/renamer/view/StartMenuWindow.fxml"));
            BorderPane startMenuWindow = loader.load();

            Scene scene = new Scene(startMenuWindow);

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
            loader.setLocation(MainApp.class.getResource("/renamer/view/StandartRenamerWindow.fxml"));
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


    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
