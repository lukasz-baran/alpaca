package com.evolve.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class DialogWindow<ENTITY> {
    protected final ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);

    @Getter
    protected final String title;
    protected final String headerText;

    /**
     * When true dialog is shown using {@link Dialog#show()} instead of {@link Dialog#showAndWait()}.
     */
    protected final boolean isTestMode;

    public DialogWindow(String title, String headerText) {
        this(title, headerText, false);
    }

    public abstract Optional<ENTITY> showDialog(Window window);

    protected abstract void validateSaveButton(Node saveButton);

    protected final Dialog<ENTITY> createDialog(Window window) {
        final Dialog<ENTITY> dialog = new Dialog<>();
        dialog.setTitle(title);
        if (!isTestMode) {
            dialog.initOwner(window);
        }
        dialog.setHeaderText(headerText);
        dialog.getDialogPane().setId("dialogPane");
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        findSubmitButton(dialog).setId("saveButton");

        return dialog;
    }

    protected final Button findSubmitButton(Dialog<ENTITY> dialog) {
        return (Button) dialog.getDialogPane().lookupButton(saveButtonType);
    }

    protected final GridPane createGridPane() {
        final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }
}
