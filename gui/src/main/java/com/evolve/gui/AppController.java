package com.evolve.gui;

import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.gui.components.RetentionFileChooser;
import com.evolve.gui.dictionaries.UnitsController;
import com.evolve.importing.event.DbfImportCompletedEvent;
import com.evolve.importing.importDbf.ImportDbfService;
import com.evolve.services.PersonsService;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Getter
@Component
@FxmlView("sample.fxml")
@RequiredArgsConstructor
@Slf4j
public class AppController implements Initializable {

    public static final int PERSONS_PER_PAGE = 100;

    private final PersonListModel personListModel;

    private final RetentionFileChooser fileChooser;
    private final ImportDbfService importDbfService;
    private final PersonsService personsService;
    private final FxWeaver fxWeaver;

    private final FxControllerAndView<UnitsController, VBox> dialog;

    @FXML
    private TableView<PersonModel> personTable;

    @FXML TableColumn<PersonModel, String> idColumn;
    @FXML TableColumn<PersonModel, String> firstNameColumn;
    @FXML TableColumn<PersonModel, String> lastNameColumn;
    @FXML TableColumn<PersonModel, LocalDate> dobColumn;
    @FXML TableColumn<PersonModel, String> statusColumn;

    @FXML Tab tabPersonDetails;
    @FXML Tab tabOriginalDetails;
    @FXML Tab tabDocuments;

    @FXML MenuItem newMenuItem;
    @FXML MenuItem quitMenuItem;
    @FXML MenuItem removeMenuItem;
    @FXML MenuItem deletePersonDataMenuItem;
    @FXML MenuItem importDbfMenuItem;
    @FXML MenuItem unitsMenuItem;

    @FXML TextField filterField;

    @FXML Pagination pagination;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(new Locale("pl"));
        populateTable("id", true);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabPersonDetails.setContent(fxWeaver.loadView(PersonDetailsController.class));
        tabOriginalDetails.setContent(fxWeaver.loadView(OriginalDetailsController.class));

        registerEventHandlers();
    }

    private void registerEventHandlers() {
        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));

        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        //newMenuItem.setOnAction(event -> itemList.clear());

        unitsMenuItem.setOnAction(event -> {
            dialog.getController().show();

            /*
            Label secondLabel = new Label("I'm a Label on new Window");

            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(secondLabel);

            Scene secondScene = new Scene(secondaryLayout, 230, 100);

            // New window (Stage)
            Stage newWindow = new Stage();
            newWindow.setTitle("Second Stage");
            newWindow.setScene(secondScene);

            // Set position of second window, related to primary window.
//            newWindow.setX(primaryStage.getX() + 200);
//            newWindow.setY(primaryStage.getY() + 100);

            newWindow.show();

             */
        });


        //personTable.setFocusModel();
        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            personListModel.getCurrentPersonProperty().setValue(newValue);
        });
    }

    private void openFile(File file) {
        log.info("Opened dbf file {}", file);
        importDbfService.startImport(file.getPath());
    }

    @EventListener
    public void handleContextStart(DbfImportCompletedEvent importCompleted) {
        log.info("import: " + importCompleted.getMessage());
        new Alert(Alert.AlertType.INFORMATION, importCompleted.getMessage()).show();
        populateTable("id", true);
    }

    void populateTable(String sortBy, boolean upDown) {
        final List<PersonListView> persons = personsService.fetchList(PersonLookupCriteria.builder().sortBy(sortBy).upDown(upDown).build());

        // TODO set pagination
        final int numberOfPersons = persons.size();
        log.info("total person number {}", numberOfPersons);

        int pageCount = numberOfPersons / PERSONS_PER_PAGE;
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(1);
        pagination.setMaxPageIndicatorCount(3);
        //        pagination.setPageFactory(pageNumber -> {
        //
        //        });

        System.out.println(pagination.getPageCount());

        personListModel.feed(persons);

        FilteredList<PersonModel> filteredData = personListModel.getFilteredList();

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                String firstName = StringUtils.trimToEmpty(person.getFirstName()).toLowerCase();
                if (firstName.contains(lowerCaseFilter)) {
                    return true;
                }

                if (StringUtils.trimToEmpty(person.getLastName()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                if (StringUtils.trimToEmpty(person.getId()).toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<PersonModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(personTable.comparatorProperty());

        personTable.setItems(sortedData);
    }

    public void quitClicked(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void importDbfClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) personTable.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }
}