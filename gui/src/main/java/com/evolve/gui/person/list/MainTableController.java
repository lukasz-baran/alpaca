package com.evolve.gui.person.list;

import com.evolve.alpaca.gui.export.PersonExportHandler;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonStatus;
import com.evolve.gui.StageManager;
import com.evolve.gui.components.NewPersonDialog;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.gui.person.event.PersonEditionRequestedEvent;
import com.evolve.gui.person.list.search.PersonSearchCriteria;
import com.evolve.gui.person.list.search.PersonSearchService;
import com.evolve.gui.person.list.search.SearchPersonDialog;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@FxmlView("main-table.fxml")
@RequiredArgsConstructor
@Slf4j
public class MainTableController implements Initializable {
    private static final String INVALID_REGISTRY_NUMBER_STYLE = "-fx-text-fill: red; -fx-font-weight: bold;";

    private final BooleanProperty disabledProperty = new SimpleBooleanProperty(false);
    private final PersonListModel personListModel;
    private final PersonsService personsService;
    private final PersonSearchService personSearchService;
    private final UnitsService unitsService;

    private final StageManager stageManager;
    private final ApplicationEventPublisher publisher;
    private final PersonExportHandler personExportHandler;

    @FXML Button btnNewPerson;
    @FXML Button btnEdit;
    @FXML Button btnDelete;
    @FXML Button btnExport;
    @FXML Button btnSearch;

    @FXML AnchorPane personTableAnchorPane;

    @FXML TableView<PersonModel> personTable;

    @FXML TableColumn<PersonModel, String> idColumn;
    @FXML TableColumn<PersonModel, String> firstNameColumn;
    @FXML TableColumn<PersonModel, String> lastNameColumn;
    @FXML TableColumn<PersonModel, LocalDate> dobColumn;
    @FXML TableColumn<PersonModel, Long> ageColumn;
    @FXML TableColumn<PersonModel, String> statusColumn;
    @FXML TableColumn<PersonModel, Long> registryNumberColumn;

    // search criteria bar (shown after Search is applied)
    @FXML HBox searchCriteriaHBox;
    @FXML Text textSearchCriteria;
    @FXML Hyperlink resetSearchHyperlink;

    // bottom quick-search bat
    @FXML Text textNumberOfRecords;
    @FXML TextField filterField;
    @FXML AnchorPane autoCompletePane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTable(PersonSearchCriteria.empty());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        registryNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registryNumber"));
        // the following handler prevents from showing 0 in registry number cells
        // 0 means that the registry number is unknown which valid according to business requirements:
        registryNumberColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Long registryNumber, boolean empty) {
                super.updateItem(registryNumber, empty);
                if (empty || registryNumber.equals(0L)) {
                    setText(null);
                } else {
                    setText(registryNumber.toString());
                }

                // set tooltip if necessary:
                setTooltip(null);
                setStyle(null);
                if (!empty && registryNumber != 0L) {
                    final Set<PersonModel> persons = personListModel.getFilteredList()
                            .stream()
                            .filter(person -> Objects.equals(person.getRegistryNumber(), registryNumber))
                            .collect(Collectors.toSet());

                    if (persons.size() > 1) {
                        setStyle(INVALID_REGISTRY_NUMBER_STYLE);
                        final String tooltipText = "Poniższe osoby mają ten sam numer kartoteki:\n" +
                            persons.stream().map(personModel ->
                                personModel.getId() + " " + personModel.getFirstName() + " " + personModel.getLastName())
                                .collect(Collectors.joining("\n"));
                        final Tooltip registryNumberTooltip = new Tooltip(tooltipText);
                        registryNumberTooltip.setShowDelay(Duration.ZERO);
                        setTooltip(registryNumberTooltip);
                    }
                }

            }
        });

        ageColumn.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Long itemQuantity, boolean empty) {
                super.updateItem(itemQuantity, empty);
                if (empty || itemQuantity.equals(0L)) {
                    setText(null);
                } else if (itemQuantity < 0L) {
                    setText("-");
                } else {
                    setText(itemQuantity.toString());
                }
            }
        });

        personTableAnchorPane.disableProperty().bind(disabledProperty);

        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            personListModel.getCurrentPersonProperty().setValue(newValue);
        });

        personTable.setRowFactory(tv -> {
            final TableRow<PersonModel> row = new TableRow<>();

            final ContextMenu contextMenu = createContextMenu(row);

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    startEdition(row.getItem());
                }
            });

            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));

            return row ;
        });

        resetSearchHyperlink.setOnAction(event -> showSearchCriteria(PersonSearchCriteria.empty()));

        // handle buttons for ARCHIVED persons:
        this.personListModel.getCurrentPersonProperty().addListener((observable, oldValue, newValue) -> {
            final boolean markAsArchived = newValue != null && newValue.getPersonStatus() == PersonStatus.ARCHIVED;
            btnEdit.setDisable(markAsArchived);
            btnDelete.setDisable(markAsArchived);
        });
    }

    private ContextMenu createContextMenu(TableRow<PersonModel> row) {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editPerson = new MenuItem("Edytuj");
        editPerson.setOnAction(event -> startEdition(row.getItem()));
        final MenuItem exportJson = new MenuItem("Eksportuj jako JSON");
        exportJson.setOnAction(event -> {
            final String personId = row.getItem().getId();
            final Person person = personsService.findById(personId);
            final String personJson = LogUtil.prettyPrintJson(person);
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(personJson);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        });
        contextMenu.getItems().addAll(editPerson, exportJson);
        return contextMenu;
    }

    private void populateTable(PersonSearchCriteria criteria) {
        final List<PersonListView> persons = personSearchService.defaultOrder(criteria);

        log.info("total person number {}", persons.size());

        final FilteredList<PersonModel> filteredData = personListModel.feed(persons);


        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> person.matches(newValue));
            refreshNumberOfItems();
        });

        SortedList<PersonModel> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(personTable.comparatorProperty());

        personTable.setItems(sortedData);
        refreshNumberOfItems();
    }

    @EventListener
    public void onPersonImportCompleted(DbfImportCompletedEvent importCompleted) {
        log.info("import: " + importCompleted.getMessage());
        stageManager.displayInformation(importCompleted.getMessage());

        populateTable(PersonSearchCriteria.empty());
    }

    @EventListener
    public void onApplicationEvent(PersonEditionFinishedEvent event) {
        event.getEditedPerson().ifPresent(updatedPerson -> {
            log.info("Edition successful");
            // update person table with person data from event
            personListModel.updatePerson(updatedPerson);
            personTable.refresh();
        });

        disableControls(false);

    }

    public void personInserted(Person newPerson) {
        final PersonModel personModel = personListModel.insertPerson(newPerson);
        personTable.refresh();
        personTable.getSelectionModel().select(personModel);
        personTable.scrollTo(personModel);
    }

    public void disableControls(boolean disable) {
        disabledProperty.setValue(disable);

        btnEdit.setDisable(disable);
        btnDelete.setDisable(disable);
        btnNewPerson.setDisable(disable);
        btnExport.setDisable(disable);
        btnSearch.setDisable(disable);
    }

    public void showSearchCriteria(PersonSearchCriteria criteria) {
        final boolean show = !criteria.isEmpty();
        searchCriteriaHBox.setVisible(show);
        final String text = "Kryteria: " + criteria;
        textSearchCriteria.setText(text);
        AnchorPane.setTopAnchor(personTable, show ? 35.0 : 5);
        populateTable(criteria);
    }

    private void refreshNumberOfItems() {
        textNumberOfRecords.setText("Liczba: " + personTable.getItems().size());
    }

    @FXML
    public void newPersonButtonClicked(ActionEvent actionEvent) {
        new NewPersonDialog(personsService, unitsService)
                .showDialog(stageManager.getWindow())
                .ifPresent(person -> {
                    final boolean success = personsService.insertPerson(person);

                    if (!success) {
                        stageManager.displayWarning("Nie udało się dodać osoby");
                    } else {
                        personInserted(person);
                    }
                });
    }

    @FXML
    void editButtonClicked() {
        final PersonModel editedPerson = this.personListModel.getCurrentPersonProperty().getValue();
        if (editedPerson == null) {
            log.warn("No person is selected - cannot edit");

            stageManager.displayWarning("Nie można zacząć edycji, gdyż nie wybrano osoby");
            return;
        }
        startEdition(editedPerson);
    }

    void startEdition(PersonModel editedPerson) {
        if (editedPerson.getPersonStatus() == PersonStatus.ARCHIVED) {
            stageManager.displayWarning("Nie można edytować osób usuniętych");
            return;
        }

        publisher.publishEvent(new PersonEditionRequestedEvent(editedPerson));
        disableControls(true);
    }

    @FXML
    void searchButtonClicked(ActionEvent actionEvent) {
        new SearchPersonDialog(unitsService)
                .showDialog(stageManager.getWindow())
                .ifPresent(this::showSearchCriteria);
    }

    @FXML
    void notYetImplemented(ActionEvent actionEvent) {
        stageManager.displayInformation("Funkcjonalność nie została jeszcze zaimplementowana");
    }

    @FXML
    void exportButtonClicked(ActionEvent actionEvent) {
        personExportHandler.displayExportCriteria(personTable.getItems());
    }
}
