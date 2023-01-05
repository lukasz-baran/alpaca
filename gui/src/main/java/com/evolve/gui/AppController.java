package com.evolve.gui;

import com.evolve.gui.components.RetentionFileChooser;
import com.evolve.importing.event.DbfImportCompletedEvent;
import com.evolve.importing.importDbf.ImportDbfService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Getter
@Component
@FxmlView("sample.fxml")
@RequiredArgsConstructor
@Slf4j
public class AppController {
    //private final FxControllerAndView<SomeDialog, VBox> someDialog;

    private final RetentionFileChooser fileChooser;
    private final ImportDbfService importDbfService;

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

        importDbfMenuItem.setOnAction(event -> {
            Stage stage = (Stage)itemListView.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                openFile(file);
            }
        });

    }

    private void openFile(File file) {
        log.info("Opened dbf file {}", file);
        importDbfService.startImport(file.getPath());
    }

    private void genItems() {
        for (int i = 0; i < 10; i++) {
            itemList.add(new Item("Item_" + Integer.toString(i)));
        }
    }

    @EventListener
    public void handleContextStart(DbfImportCompletedEvent importCompleted) {
        log.info("import: " + importCompleted.getMessage());
        Alert a = new Alert(Alert.AlertType.INFORMATION, importCompleted.getMessage());
        a.show();
    }
}