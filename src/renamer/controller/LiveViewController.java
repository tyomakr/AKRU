package renamer.controller;

import renamer.MainApp;

public class LiveViewController {

    /** начало блока, который может быть в наследовании */
    //даем контроллеру доступ к экземпляру mainApp
    public MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    //возврат к главному меню
    public void backToMenu() {
        mainApp.backToMenu();
        //тут еще добавим логгер
    }
    /** конец блока */
}
