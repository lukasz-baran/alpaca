package com.evolve.gui.person.lastNames;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.StringUtils;

public class LastNameListCell extends ListCell<String> {
    private TextField textField;
    private final ListCell<String> cell = this;

    public LastNameListCell(ListView<String> lastNames, BooleanProperty disabledProperty) {
        ContextMenu contextMenu = new ContextMenu();
        cell.setEditable(lastNames.isEditable());

        final MenuItem editItem = new MenuItem();
        editItem.disableProperty().bind(disabledProperty);
        editItem.textProperty().bind(Bindings.format("Edytuj \"%s\"", cell.itemProperty()));
        editItem.setOnAction(event -> cell.startEdit());

        final MenuItem addItem = new MenuItem("Dodaj");
        addItem.disableProperty().bind(disabledProperty);
        addItem.setOnAction(ev -> {
            final int index = lastNames.getItems().size() ;


            lastNames.getItems().add("nowe nazwisko");

            lastNames.getSelectionModel().clearSelection();
            lastNames.getSelectionModel().select(index);
            //cell.setItem(lastNames.getSelectionModel().getSelectedItem());
            lastNames.edit(index);
        });



        final MenuItem deleteItem = new MenuItem();
        deleteItem.disableProperty().bind(disabledProperty);
        deleteItem.textProperty().bind(Bindings.format("Usuń \"%s\"", cell.itemProperty()));
        deleteItem.setOnAction(ev -> {
            lastNames.getItems().remove(cell.getItem());
        });

        contextMenu.setOnShowing(e -> {
            if(lastNames.getSelectionModel().getSelectedItems().size() - 1 > 0)
            {
                editItem.setDisable(true);
                addItem.setDisable(true);
            }
        });

        contextMenu.getItems().addAll(addItem, editItem, deleteItem);

        cell.textProperty().bind(cell.itemProperty());

        cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
            if (isNowEmpty) {
                cell.setContextMenu(null);
            } else {
                cell.setContextMenu(contextMenu);
            }
        });
    }

    public String getString() {
        return getItem() == null ? "" : getItem();
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }
        cell.textProperty().unbind();
        setText(null);
        setGraphic(textField);
        textField.selectAll();
        textField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText((String) getItem());
        setGraphic(null);
        setGraphic(getGraphic());
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        cell.textProperty().unbind();
        if (empty) {

            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }

                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getGraphic());
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                final String editedValue = textField.getText().trim();

                System.out.println("ENTER " + editedValue);
                if (StringUtils.isNotEmpty(editedValue) && editedValue.length() > 2) {
                    commitEdit(textField.getText());
                    setGraphic(null);
                    setGraphic(getGraphic());
                }

            } else if (t.getCode() == KeyCode.ESCAPE) {
                System.out.println("ESCAPE");
                cancelEdit();
            }

            System.out.println("other " + t.getCode());
        });
    }

}
