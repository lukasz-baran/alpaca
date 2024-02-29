package com.evolve.gui.documents;

import com.evolve.alpaca.document.DocumentCategory;
import com.evolve.alpaca.document.FilePathAndDescription;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private final StageManager stageManager;
    private final TextField filePathTextField = new TextField();
    private final TextArea descriptionTextField = new TextArea();
    private final ComboBox<DocumentCategory> documentCategoryComboBox;
    private final ObjectProperty<DocumentCategory> documentCategoryObjectProperty = new SimpleObjectProperty<>();

    public DocumentDetailsDialog(StageManager stageManager, DocumentCategory documentCategory) {
        super("Nowy dokument", "Wybierz plik - dozwolone formaty: " + String.join(", ", ACCEPTED_EXTENSION));
        this.stageManager = stageManager;

        this.documentCategoryComboBox = new ComboBox<>();
        this.documentCategoryComboBox.getItems().addAll(DocumentCategory.values());
        this.documentCategoryComboBox.valueProperty().bindBidirectional(documentCategoryObjectProperty);
        this.documentCategoryComboBox.getSelectionModel().select(Optional.ofNullable(documentCategory).orElse(DocumentCategory.DEFAULT));
    }

    @Override
    public Optional<FilePathAndDescription> showDialog(Window window) {
        final Dialog<FilePathAndDescription> dialog = createDialog(window);
        final FilePathAndDescription filePathAndDescription = new FilePathAndDescription();

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

        grid.add(new Label("Kategoria:"), 0, 1);
        grid.add(documentCategoryComboBox, 1, 1);

        descriptionTextField.setPrefRowCount(5);

        grid.add(new Label("Opis:"), 0, 2);
        grid.add(descriptionTextField, 1, 2);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        documentCategoryObjectProperty.addListener(change ->  {
            validateSaveButton(saveButton);
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(filePathTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                filePathAndDescription.setDescription(descriptionTextField.getText());
                filePathAndDescription.setDocumentCategory(documentCategoryObjectProperty.getValue());
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
