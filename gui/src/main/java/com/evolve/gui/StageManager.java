package com.evolve.gui;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StageManager {

    private final FxWeaver fxWeaver;

    @Setter
    private Stage primaryStage;

    public void displayScene(Class<? extends Initializable> fxControllerClass,
            String title, String iconFileName) {
        Objects.requireNonNull(primaryStage, "primaryStage is not set!");

        Scene scene = new Scene(fxWeaver.loadView(fxControllerClass));
//        scene.getStylesheets().addAll(getStyleSheets());

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        //        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new Image(iconFileName));


        try {
            primaryStage.show();
        } catch (Exception exception) {
            logAndExit("Unable to show scene with title " + title, exception);
        }
    }

//    public void setPrimaryStage(Stage stage) {
//        this.primaryStage = stage;
//    }
//
    private void logAndExit(String errorMsg, Exception exception) {
        log.error(errorMsg, exception, exception.getCause());
        Platform.exit();
    }

    public void displayInformation(String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.initOwner(primaryStage.getScene().getWindow());
        alert.show();
    }

    public boolean displayConfirmation(String question) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.initOwner(primaryStage.getScene().getWindow());
        alert.setContentText(question);
        final Optional<ButtonType> action = alert.showAndWait();

        return action.isPresent() && action.get().equals(ButtonType.OK);
    }

    public void displayWarning(String message) {
        final Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.initOwner(primaryStage.getScene().getWindow());
        alert.showAndWait();
    }
}
