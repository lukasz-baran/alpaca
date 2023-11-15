package com.evolve.gui;

import com.evolve.AlpacaSpringApp;
import com.evolve.gui.events.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlpacaJavafxApp extends Application {
    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.applicationContext = new SpringApplicationBuilder()
                .sources(AlpacaSpringApp.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));

        stageManager = applicationContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(stage);
        stageManager.displayScene(AppController.class, "Alpaca - accounting", "alpaca.png");
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

}

