package com.evolve.gui;

import com.evolve.gui.components.RetentionFileChooser;
import com.evolve.importing.event.DbfImportCompletedEvent;
import com.evolve.importing.importDbf.ImportDbfService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
    private final ObservableList<PersonModel> data =
            FXCollections.observableArrayList(
                    new PersonModel("123", "Jacob", "Smith", "jacob.smith@example.com"),
                    new PersonModel("124", "Isabella", "Johnson", "isabella.johnson@example.com"),
                    new PersonModel("125", "Ethan", "Williams", "ethan.williams@example.com"),
                    new PersonModel("126", "Emma", "Jones", "emma.jones@example.com"),
                    new PersonModel("127", "Michael", "Brown", "michael.brown@example.com"));

    private final RetentionFileChooser fileChooser;
    private final ImportDbfService importDbfService;

    @FXML
    private TableView<PersonModel> personTable;

    @FXML
    TableColumn<PersonModel, String> idColumn;
    @FXML
    TableColumn<PersonModel, String> firstNameColumn;
    @FXML
    TableColumn<PersonModel, String> lastNameColumn;
    @FXML
    TableColumn<PersonModel, String> emailColumn;

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

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        personTable.setItems(data);

        registerEventHandlers();
    }

    private void registerEventHandlers() {
        addButton.setOnAction(event -> itemList.add(new Item(nameTextField.getText())));

        nameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                itemList.add(new Item(nameTextField.getText()));
            }
        });

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        quitMenuItem.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setOnAction(event -> itemList.clear());

        removeMenuItem.setOnAction(event -> {
            PersonModel item = personTable.getSelectionModel().getSelectedItem();
            data.remove(item);
            //Item item = itemListView.getSelectionModel().getSelectedItem();
            //itemList.remove(item);
        });

        importDbfMenuItem.setOnAction(event -> {
            Stage stage = (Stage)personTable.getScene().getWindow();
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