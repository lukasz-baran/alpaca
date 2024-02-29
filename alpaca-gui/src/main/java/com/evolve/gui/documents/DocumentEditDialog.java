package com.evolve.gui.documents;

import com.evolve.alpaca.document.DocumentCategory;
import com.evolve.alpaca.document.DocumentEntry;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class DocumentEditDialog extends DialogWindow<DocumentEntry> {

    private final DocumentEntry editedEntry;
    private final TextField fileNameTextField = new TextField();
    private final TextArea descriptionTextArea = new TextArea();
    private final FileExtensionValidator fileExtensionValidator;

    private final ComboBox<DocumentCategory> documentCategoryComboBox;
    private final ObjectProperty<DocumentCategory> documentCategoryObjectProperty = new SimpleObjectProperty<>();


    public DocumentEditDialog(DocumentEntry documentEntry) {
        super("Dokument ID=" + documentEntry.getId(), "Edytuj nazwę i opis dokumentu");
        this.editedEntry = documentEntry;
        this.fileExtensionValidator = new FileExtensionValidator(documentEntry.getFileName());

        this.documentCategoryComboBox = new ComboBox<>();
        this.documentCategoryComboBox.getItems().addAll(DocumentCategory.values());
        this.documentCategoryComboBox.valueProperty().bindBidirectional(documentCategoryObjectProperty);
        this.documentCategoryComboBox.getSelectionModel().select(Optional.ofNullable(documentEntry.getCategory()).orElse(DocumentCategory.DEFAULT));
    }

    @Override
    public Optional<DocumentEntry> showDialog(Window window) {
        final Dialog<DocumentEntry> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        Optional.ofNullable(editedEntry).ifPresent(documentEntry -> {
            fileNameTextField.setText(documentEntry.getFileName());
            descriptionTextArea.setText(documentEntry.getSummary());
        });

        grid.add(new Label("Nazwa pliku:"), 0, 0);
        grid.add(fileNameTextField, 1, 0);

        grid.add(new Label("Kategoria:"), 0, 1);
        grid.add(documentCategoryComboBox, 1, 1);

        descriptionTextArea.setPrefRowCount(5);

        grid.add(new Label("Opis:"), 0, 2);
        grid.add(descriptionTextArea, 1, 2);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        fileNameTextField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        descriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        documentCategoryObjectProperty.addListener(change -> validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(fileNameTextField::requestFocus);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                editedEntry.setFileName(fileNameTextField.getText());
                editedEntry.setSummary(descriptionTextArea.getText());
                editedEntry.setCategory(this.documentCategoryObjectProperty.getValue());
                return editedEntry;
            }
            return null;
        });

        return dialog.showAndWait();
    }



    @Override
    protected void validateSaveButton(Node saveButton) {
        final boolean fileNameValid = validateFileNameTextField();

        final boolean disable =
                !fileNameValid ||
                StringUtils.equals(editedEntry.getFileName(), this.fileNameTextField.getText()) &&
                StringUtils.equals(editedEntry.getSummary(), this.descriptionTextArea.getText()) &&
                editedEntry.matchesCategory(this.documentCategoryObjectProperty.getValue());

        saveButton.setDisable(disable);
    }

    private boolean validateFileNameTextField() {
        fileNameTextField.setStyle("");
        fileNameTextField.setTooltip(null);
        final String input = fileNameTextField.getText();
        var result = fileExtensionValidator.validate(input);

        if (!result.isValid()) {
            fileNameTextField.setStyle("-fx-border-color: red");
            fileNameTextField.setTooltip(StageManager.buildTooltip(result));
        }
        return result.isValid();
    }


}
