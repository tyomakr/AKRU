package ru.aikr.spring.fx.akru.controller.gui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.aikr.spring.fx.akru.service.preparing.MaskHandlerService;
import ru.aikr.spring.fx.akru.service.process.RenamerService;
import ru.aikr.spring.fx.akru.service.storage.GUIFieldsControlService;
import ru.aikr.spring.fx.akru.service.storage.FileItemsStorageService;

@Component
@FxmlView("std-renamer-window.fxml")
public class StandartRenamerController extends AbstractRenamerController {

    @FXML
    private BorderPane borderPane;


    public StandartRenamerController(
            FileItemsStorageService fileItemsStorageService,
            GUIFieldsControlService GUIFieldsControlService,
            MaskHandlerService maskHandlerService,
            RenamerService renamerService) {

        super(
                fileItemsStorageService,
                GUIFieldsControlService,
                maskHandlerService,
                renamerService);
    }

    @Override
    public void initialize() {

        Stage stage = new Stage();
        super.setStage(stage);                                                                                          //для функционала кнопки "к меню"

        stage.setScene(new Scene(borderPane));
        stage.setResizable(false);

        super.setImagesOnlyFlag(false);
        super.initialize();
    }
}
