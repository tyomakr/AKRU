package ru.aikr.spring.fx.akru.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import ru.aikr.spring.fx.akru.AKRUApp;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        this.context = new SpringApplicationBuilder()
                .sources(AKRUApp.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
