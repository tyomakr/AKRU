package renamer.controller;

import renamer.MainApp;

public class StartMenuController {

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    //constructor
    public StartMenuController() {}


    //initialize
    private void initialize() {}


    //Вызов окна stdRenamer
    public void runStandartRenamer() {

        mainApp.getPrimaryStage().close();
        mainApp.showStandartRenamer();
    }


}
