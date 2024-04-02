package com.evolve.gui;

import com.evolve.AlpacaSpringApp;
import com.evolve.gui.events.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
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
        final String[] args = getParameters().getRaw().toArray(new String[0]);
        final String profile = SystemUtils.IS_OS_MAC ? "mac" : "win";

        this.applicationContext = new SpringApplicationBuilder()
                .sources(AlpacaSpringApp.class)
                .profiles(profile)
                .headless(false) // needed because of Toolkit.getDefaultToolkit().getScreenSize()
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler(stage));

        applicationContext.publishEvent(new StageReadyEvent(stage));
        hideSplashScreen();

        stageManager = applicationContext.getBean(StageManager.class);
        stageManager.setPrimaryStage(stage);
        stageManager.displayScene(AppController.class, "Alpaca - accounting", "alpaca.png");
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    private void hideSplashScreen() {
        notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START, this));
    }

}

