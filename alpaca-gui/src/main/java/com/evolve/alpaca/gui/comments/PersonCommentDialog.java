package com.evolve.alpaca.gui.comments;

import com.evolve.gui.DialogWindow;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class PersonCommentDialog extends DialogWindow<PersonCommentEntry> {

    private final PersonCommentEntry commentEntry;
    private final TextArea contentTextArea = new TextArea();

    public PersonCommentDialog(PersonCommentEntry commentEntry) {
        super(isInEdition(commentEntry) ? "Edycja komentarza: " : "Nowy komentarz",
                isInEdition(commentEntry) ? "Edytuj komentarz" :
                        "wprowadź nowy komentarz");
        this.commentEntry = commentEntry;
    }

    @Override
    public Optional<PersonCommentEntry> showDialog(Window window) {
        final Dialog<PersonCommentEntry> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        contentTextArea.setMaxWidth(300);
        contentTextArea.setPrefWidth(300);
        Optional.ofNullable(commentEntry)
                .ifPresent(entry -> contentTextArea.setText(entry.getContent()));

        grid.add(new Label("Komentarz:"), 0, 0);
        grid.add(contentTextArea, 1, 0);

        final Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        contentTextArea.textProperty().addListener((observableValue, s, t1) -> validateSaveButton(saveButton));

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(contentTextArea::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (isInEdition(commentEntry)) {
                    commentEntry.setContent(contentTextArea.getText());
                    return commentEntry;
                }
                return new PersonCommentEntry(null, null, contentTextArea.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        final String content = contentTextArea.getText().trim();

        final boolean disable = isInEdition(this.commentEntry) ?
             StringUtils.equals(this.commentEntry.getContent(), content) :
             StringUtils.isEmpty(content);

        saveButton.setDisable(disable);
    }

    static boolean isInEdition(PersonCommentEntry entry) {
        return entry != null;
    }

}
