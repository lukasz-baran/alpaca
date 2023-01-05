package com.evolve.gui;

import com.sun.tools.javac.Main;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {
    private final FxWeaver fxWeaver;

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        log.info("onApplicationEvent {}", event);
//        Stage stage = event.stage;
//        var node = fxWeaver.loadView(MainController.class);
//        Scene scene = new Scene(fxWeaver.loadView(MainController.class), 400, 300);
//        stage.setScene(scene);
//        stage.show();
    }
}