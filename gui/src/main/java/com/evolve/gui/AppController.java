package com.evolve.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Component
@FxmlView("sample.fxml")
public class AppController {
    //private final FxControllerAndView<SomeDialog, VBox> someDialog;
//

    @FXML
    ListView<Item> itemListView;

    @FXML
    private Button addButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private MenuItem newMenuItem;

    @FXML
    private MenuItem quitMenuItem;

    @FXML
    private MenuItem removeMenuItem;

    @FXML
    private MenuItem deletePersonDataMenuItem;

    @FXML
    private MenuItem importDbfMenuItem;

    private ObservableList<Item> itemList;

   // @FXML
    public void initialize() {
        itemList = FXCollections.observableArrayList();
        genItems();
        itemListView.setItems(itemList);

        registerEventHandlers();
    }

    private void registerEventHandlers() {
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                itemList.add(new Item(nameTextField.getText()));
            }
        });

        nameTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    itemList.add(new Item(nameTextField.getText()));
                }
            }
        });

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setOnAction(event -> itemList.clear());

        removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Item item = itemListView.getSelectionModel().getSelectedItem();
                itemList.remove(item);
            }
        });

        final FileChooser fileChooser = new FileChooser();
        importDbfMenuItem.setOnAction(event -> {
            Stage stage = (Stage)itemListView.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                openFile(file);
            }
        });

    }

    private void openFile(File file) {
        final Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void genItems() {
        for (int i = 0; i < 10; i++) {
            itemList.add(new Item("Item_" + Integer.toString(i)));
        }
    }
}