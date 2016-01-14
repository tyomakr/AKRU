package renamer.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import logger.ConsoleAreaAppender;
import org.apache.log4j.Logger;
import renamer.MainApp;


public class ExifRenamerController {

    //создание логгера
    public static final Logger LOGGER = Logger.getLogger(ExifRenamerController.class);

    //даем контроллеру доступ к экземпляру mainApp
    private MainApp mainApp;

    //объявляем поля из FXML
    @FXML private TextArea consoleArea;

    //constructor
    public ExifRenamerController() {}


    //initialize
    public void initialize() {

        //Инициализация логгера
        ConsoleAreaAppender consoleAreaAppender = new ConsoleAreaAppender();
        ConsoleAreaAppender.setTextArea(consoleArea);
        LOGGER.addAppender(consoleAreaAppender);


    }


    //возврат к главному меню
    public void backToMenu() {
        mainApp.backToMenu();
    }


    //setters and getters
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


}
