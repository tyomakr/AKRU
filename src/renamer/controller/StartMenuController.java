package renamer.controller;

import renamer.MainApp;

public class StartMenuController {

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    //Вызов окна stdRenamer
    public void runStandartRenamer() {
        mainApp.getPrimaryStage().close();
        mainApp.showStandartRenamer();
    }

    //Вызов окна ExifRenamer
    public void runExifRenamer() {
        mainApp.getPrimaryStage().close();
        mainApp.showExifRenamer();
    }


}
