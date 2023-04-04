package com.evolve.gui;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public abstract class DialogWindow<ENTITY> {
    protected final ButtonType saveButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);

    protected final String title;
    protected final String headerText;

    public abstract Optional<ENTITY> showDialog(Window window);


    protected final Dialog<ENTITY> createDialog(Window window) {
        final Dialog<ENTITY> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initOwner(window);
        dialog.setHeaderText(headerText);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        return dialog;
    }
}
