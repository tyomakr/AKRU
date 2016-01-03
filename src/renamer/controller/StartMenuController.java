package renamer.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import renamer.MainApp;

public class StartMenuController {

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    //constructor
    public StartMenuController() {}


    //объявляем поля из FXMbuttonStdRenamer
    @FXML private Button buttonStdRenamer;


    //initialize
    private void initialize() {}


    //Вызов окна stdRenamer
    public void runStandartRenamer() {

        mainApp.getPrimaryStage().close();
        mainApp.showStandartRenamer();
    }


}
