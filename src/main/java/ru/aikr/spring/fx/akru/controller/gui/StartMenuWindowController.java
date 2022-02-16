package ru.aikr.spring.fx.akru.controller.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("start-menu-window.fxml")
@RequiredArgsConstructor
public class StartMenuWindowController {

    private final FxControllerAndView<StandartRenamerController, BorderPane> standartRenamerDialog;
    private final FxControllerAndView<ExifRenamerController, BorderPane> exifRenamerDialog;

    @FXML
    Button btnStandartRenamer;
    @FXML
    Button btnEXIFRenamer;


    @FXML
    public void initialize() {
        btnStandartRenamer.setOnAction(
                actionEvent -> standartRenamerDialog.getController().show()
        );
        btnEXIFRenamer.setOnAction(
                actionEvent -> exifRenamerDialog.getController().show()
        );
    }
}
