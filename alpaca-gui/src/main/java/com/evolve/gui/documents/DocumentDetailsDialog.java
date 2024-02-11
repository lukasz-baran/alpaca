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
import java.util.List;
import java.util.Optional;

@Slf4j
public class DocumentDetailsDialog extends DialogWindow<FilePathAndDescription> {
    public static final List<String> ACCEPTED_EXTENSION =
            List.of("*.doc", "*.docx", "*.odt", "*.pdf", "*.rtf", "*.txt", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");

    public static final FileChooser.ExtensionFilter DOCUMENTS_EXTENSION_FILTER = new FileChooser.ExtensionFilter("documents",
            ACCEPTED_EXTENSION);

    private final FilePathAndDescription filePathAndDescription;
    private final StageManager stageManager;
    private final TextField filePathTextField;


    public DocumentDetailsDialog(StageManager stageManager) {
        super("Nowy document", "Wybierz plik - dozwolone formaty: " + String.join(", ", ACCEPTED_EXTENSION));
        this.filePathAndDescription = new FilePathAndDescription();
        this.stageManager = stageManager;
        this.filePathTextField = new TextField();
    }

    @Override
    public Optional<FilePathAndDescription> showDialog(Window window) {
        final Dialog<FilePathAndDescription> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final HBox group = new HBox();
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
            validateSaveButton(saveButton);
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

    @Override
    protected void validateSaveButton(Node saveButton) {
        saveButton.setDisable(filePathTextField.getText().trim().isEmpty());
    }

}
