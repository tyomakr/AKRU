package ru.aikr.spring.fx.akru.application;

import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.aikr.spring.fx.akru.controller.gui.StartMenuWindowController;


@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.stage;
        stage.setTitle("AK Rename Utility 1.0a");
        stage.setResizable(false);
        Scene scene = new Scene(fxWeaver.loadView(StartMenuWindowController.class));
        stage.setScene(scene);
        stage.show();
    }
}
