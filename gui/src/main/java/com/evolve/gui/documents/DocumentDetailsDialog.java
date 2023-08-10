package com.evolve.gui.documents;

import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;

@Slf4j
public class DocumentDetailsDialog extends DialogWindow<FilePathAndDescription> {
    public static final FileChooser.ExtensionFilter DOCUMENTS_EXTENSION_FILTER = new FileChooser.ExtensionFilter("documents",
            "*.doc", "*.docx", "*.odt", "*.pdf", "*.rtf", "*.txt", "*.png", ".jpg", "*.jpeg", "*.gif", "*.bmp");

    private final FilePathAndDescription filePathAndDescription;
    private final StageManager stageManager;

    public DocumentDetailsDialog(StageManager stageManager) {
        super("Nowy document", "Wybierz plik - dozwolone formaty to pliki z obrazami oraz tekstowe");
        this.filePathAndDescription = new FilePathAndDescription();
        this.stageManager = stageManager;
    }

    @Override
    public Optional<FilePathAndDescription> showDialog(Window window) {
        final Dialog<FilePathAndDescription> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final HBox group = new HBox();
        final TextField filePathTextField = new TextField();
        filePathTextField.setPromptText("Plik");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            filePathAndDescription.setFile(file);
            filePathTextField.setText(file.getAbsolutePath());
        });
        group.getChildren().add(filePathTextField);
        group.getChildren().add(chooseFileButton);


        grid.add(new Label("Plik:"), 0, 0);
        grid.add(group, 1, 0);

        final TextArea descriptionTextField = new TextArea();
        descriptionTextField.setPrefRowCount(5);

        grid.add(new Label("Opis:"), 0, 1);
        grid.add(descriptionTextField, 1, 1);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(filePathTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                filePathAndDescription.setDescription(descriptionTextField.getText());
                return filePathAndDescription;
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
