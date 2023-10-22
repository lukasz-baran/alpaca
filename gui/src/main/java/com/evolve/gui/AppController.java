package com.evolve.gui;

import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import com.evolve.alpaca.importing.importDoc.ImportAlphanumeric;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.person.Person;
import com.evolve.gui.admin.importDbf.ImportDbfDialog;
import com.evolve.gui.components.NewPersonDialog;
import com.evolve.gui.dictionaries.UnitsController;
import com.evolve.gui.documents.DocumentsController;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.gui.person.list.MainTableController;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.search.SearchPersonDialog;
import com.evolve.services.PersonsService;
import com.evolve.services.UnitsService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Getter
@Component
@FxmlView("main-view.fxml")
@RequiredArgsConstructor
@Slf4j
public class AppController implements Initializable, ApplicationListener<PersonEditionFinishedEvent> {

    private final PersonListModel personListModel;

    private final ImportDbfService importDbfService;
    private final PersonsService personsService;
    private final UnitsService unitsService;
    private final FxWeaver fxWeaver;
    private final StageManager stageManager;

    private final FxControllerAndView<UnitsController, VBox> unitsDialogController;

    private final PersonDetailsController personDetailsController;
    private final MainTableController mainTableController;

    public Button btnNew;
    public Button btnEdit;
    public Button btnDelete;
    public Button btnExport;
    public Button btnSearch;

    @FXML Tab tabPersonDetails;
    @FXML Tab tabOriginalDetails;
    @FXML Tab tabPersonAdditionalData;
    @FXML Tab tabDocuments;

    @FXML MenuItem newMenuItem;
    @FXML MenuItem quitMenuItem;
    @FXML MenuItem removeMenuItem;
    @FXML MenuItem deletePersonDataMenuItem;
    @FXML MenuItem importDbfMenuItem;
    @FXML MenuItem unitsMenuItem;
    @FXML MenuItem importPeopleMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(new Locale("pl"));
        tabPersonDetails.setContent(fxWeaver.loadView(PersonDetailsController.class));
        tabOriginalDetails.setContent(fxWeaver.loadView(OriginalDetailsController.class));
        tabPersonAdditionalData.setContent(fxWeaver.loadView(PersonAdditionalDataController.class));
        tabDocuments.setContent(fxWeaver.loadView(DocumentsController.class));

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

        unitsMenuItem.setOnAction(event -> unitsDialogController.getController().show());
    }

    public void quitClicked(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void notYetImplemented(ActionEvent actionEvent) {
        stageManager.displayInformation("Feature is not yet implemented");
    }

    public void newPersonButtonClicked(ActionEvent actionEvent) {
        new NewPersonDialog(personsService, unitsService)
            .showDialog(stageManager.getWindow())
            .ifPresent(person -> {
                final boolean success = personsService.insertPerson(person);

                if (!success) {
                    stageManager.displayWarning("Nie udało się dodać osoby");
                } else {
                    mainTableController.personInserted(person);
                }
            });
    }

    public void editButtonClicked(ActionEvent actionEvent) {
        if (this.personListModel.getCurrentPersonProperty().getValue() == null) {
            log.warn("No person is selected - cannot edit");

            stageManager.displayWarning("Nie można zacząć edycji, gdyż nie wybrano osoby");
            return;
        }

        personDetailsController.setEditable(true);
        mainTableController.disableControls(true);
        disableControls(true);
    }

    public void searchButtonClicked(ActionEvent actionEvent) {
        new SearchPersonDialog(unitsService)
                .showDialog(stageManager.getWindow())
                .ifPresent(personSearchCriteria -> {
                    System.out.println(personSearchCriteria);
                    mainTableController.showSearchCriteria(personSearchCriteria);
                });
    }

    public void importDbfClicked(ActionEvent actionEvent) {
        new ImportDbfDialog(stageManager)
                .showDialog(stageManager.getWindow())
                .ifPresent(dbFiles -> {
                    log.info("Opened dbf file {}", dbFiles);
                    if (dbFiles.getMainFile() != null) {
                        importDbfService.startImport(dbFiles.getMainFile().getPath(),
                                dbFiles.getPlanAccountsFile().getPath());
                    }
                });
    }

    public void importPeopleClicked(ActionEvent actionEvent) {
        final List<Person> people = new ImportPeople(false).processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();
    }

    @Override
    public void onApplicationEvent(PersonEditionFinishedEvent event) {
        disableControls(false);
    }

    void disableControls(boolean disable) {
        btnEdit.setDisable(disable);
        btnDelete.setDisable(disable);
        btnNew.setDisable(disable);
        btnExport.setDisable(disable);
        btnSearch.setDisable(disable);
    }

}