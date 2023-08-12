package com.evolve.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
public abstract class DialogTestBase {

    @BeforeEach
    public void setUp() throws Exception {
        FxToolkit.setupSceneRoot(() -> {
            Button openDialogButton = new Button("Open Dialog");
            openDialogButton.setId("openDialog");
            openDialogButton.setOnAction(createTestedDialog());
            StackPane root = new StackPane(openDialogButton);
            root.setPrefSize(500, 500);
            return new StackPane(root);
        });
        FxToolkit.setupStage(Stage::show);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FxToolkit.cleanupStages();
    }

    protected abstract EventHandler<ActionEvent> createTestedDialog();
}
