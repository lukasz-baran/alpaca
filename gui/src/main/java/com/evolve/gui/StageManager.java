package com.evolve.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StageManager {
    public static final Image APPLICATION_ICON = new Image("alpaca.png");
    public static final FileChooser.ExtensionFilter DBF_EXTENSION_FILTER = new FileChooser.ExtensionFilter("dbf (*.dbf)", "*.dbf");

    private final FxWeaver fxWeaver;
    private final SimpleObjectProperty<File> lastKnownDirectoryProperty;

    @Setter
    @Getter
    private Stage primaryStage;

    public File getFileChooser(FileChooser.ExtensionFilter extensionFilter) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
        fileChooser.getExtensionFilters().setAll(extensionFilter);
        final File chosenFile = fileChooser.showOpenDialog(getWindow());
        if(chosenFile != null){
            //Set the property to the directory of the chosenFile so the fileChooser will open here next
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
        }
        return chosenFile;
    }

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

    public Window getWindow() {
        return primaryStage.getScene().getWindow();
    }

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

    public Optional<ButtonType> displayOkNoCancel(String question) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirmation", ButtonType.OK, ButtonType.NO, ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.initOwner(primaryStage.getScene().getWindow());
        alert.setContentText(question);

        return alert.showAndWait();
    }

    public void displayError(String message) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.initOwner(primaryStage.getScene().getWindow());
        alert.showAndWait();
    }

    public static void displayInformation(Window window, String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.initOwner(window);
        alert.show();
    }
}
