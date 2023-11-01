package com.evolve.gui.person.list;

import com.evolve.domain.Person;
import com.evolve.domain.PersonListView;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.gui.StageManager;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.gui.person.list.search.PersonSearchCriteria;
import com.evolve.alpaca.importing.event.DbfImportCompletedEvent;
import com.evolve.services.PersonsService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("main-table.fxml")
@RequiredArgsConstructor
@Slf4j
public class MainTableController implements Initializable, ApplicationListener<PersonEditionFinishedEvent> {

    private final BooleanProperty disabledProperty = new SimpleBooleanProperty(false);
    private final PersonListModel personListModel;
    private final PersonsService personsService;
    private final StageManager stageManager;

    @FXML AnchorPane personTableAnchorPane;

    @FXML TableView<PersonModel> personTable;

    @FXML TableColumn<PersonModel, String> idColumn;
    @FXML TableColumn<PersonModel, String> firstNameColumn;
    @FXML TableColumn<PersonModel, String> lastNameColumn;
    @FXML TableColumn<PersonModel, LocalDate> dobColumn;
    @FXML TableColumn<PersonModel, String> statusColumn;
    @FXML TableColumn<PersonModel, Long> registryNumberColumn;

    @FXML HBox searchCriteriaHBox;
    @FXML Text textSearchCriteria;
    @FXML Hyperlink resetSearchHyperlink;

    @FXML Text textNumberOfRecords;
    @FXML TextField filterField;
    @FXML Button btnClearFilter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTable("id", true, PersonSearchCriteria.empty());

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        registryNumberColumn.setCellValueFactory(new PropertyValueFactory<>("registryNumber"));

        personTableAnchorPane.disableProperty().bind(disabledProperty);

        btnClearFilter.setOnAction(event -> filterField.clear());

        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            personListModel.getCurrentPersonProperty().setValue(newValue);
        });

        resetSearchHyperlink.setOnAction(event -> showSearchCriteria(PersonSearchCriteria.empty()));
    }

    private void populateTable(String sortBy, boolean upDown, PersonSearchCriteria criteria) {
        final List<PersonListView> persons = personsService.fetchList(
                PersonLookupCriteria.builder()
                        .sortBy(sortBy)
                        .upDown(upDown)
                        .unitNumber(criteria.unitNumber())
                        .build());

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

        populateTable("id", true, PersonSearchCriteria.empty());
    }

    @Override
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
    }

    public void showSearchCriteria(PersonSearchCriteria criteria) {
        final boolean show = !criteria.isEmpty();
        searchCriteriaHBox.setVisible(show);
        final String text = "Kryteria: " + criteria;
        textSearchCriteria.setText(text);
        AnchorPane.setTopAnchor(personTable, show ? 35.0 : 5);
        populateTable("id", true, criteria);
    }

    private void refreshNumberOfItems() {
        textNumberOfRecords.setText("Liczba: " + personTable.getItems().size());
    }
}
