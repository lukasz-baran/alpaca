package com.evolve.gui;

import com.evolve.gui.components.NewPersonController;
import com.evolve.gui.components.NewPersonDialog;
import com.evolve.gui.components.RetentionFileChooser;
import com.evolve.gui.dictionaries.UnitsController;
import com.evolve.gui.events.PersonEditionFinishedEvent;
import com.evolve.importing.importDbf.ImportDbfService;
import com.evolve.importing.importDoc.ImportAlphanumeric;
import com.evolve.importing.importDoc.ImportPeople;
import com.evolve.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.importing.importDoc.person.Person;
import com.evolve.services.PersonsService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
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

    private final RetentionFileChooser fileChooser;
    private final ImportDbfService importDbfService;
    private final PersonsService personsService;
    private final FxWeaver fxWeaver;
    private final StageManager stageManager;

    private final FxControllerAndView<UnitsController, VBox> dialog;

    private final PersonDetailsController personDetailsController;
    private final MainTableController mainTableController;

    public Button btnNew;
    public Button btnEdit;
    public Button btnDelete;
    public Button btnExport;

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

    @FXML
    private final FxControllerAndView<NewPersonController, VBox> newPersonController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(new Locale("pl"));
        tabPersonDetails.setContent(fxWeaver.loadView(PersonDetailsController.class));
        tabOriginalDetails.setContent(fxWeaver.loadView(OriginalDetailsController.class));
        tabPersonAdditionalData.setContent(fxWeaver.loadView(PersonAdditionalDataController.class));

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

        unitsMenuItem.setOnAction(event -> dialog.getController().show());
    }

    private void openFile(File file) {
        log.info("Opened dbf file {}", file);
        importDbfService.startImport(file.getPath());
    }

    public void quitClicked(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void notYetImplemented(ActionEvent actionEvent) {
        stageManager.displayInformation("Feature is not yet implemented");
    }

    public void newPersonButtonClicked(ActionEvent actionEvent) {
        new NewPersonDialog(personsService)
            .showDialog(stageManager.getWindow())
            .ifPresent(person -> {
                final boolean success = personsService.insertPerson(person, insertedPerson -> {
                    log.info("New person successfully added: {}", insertedPerson);
                    personListModel.insertPerson(insertedPerson);
                });
                if (!success) {
                    stageManager.displayWarning("Nie udało się dodać osoby");
                }
            });
    }

    public void editButtonClicked(ActionEvent actionEvent) {
        if (this.personListModel.getCurrentPersonProperty().getValue() == null) {
            log.warn("No person is selected - cannot edit");

            final Alert alertBox = new Alert(Alert.AlertType.INFORMATION, "Nie można zacząć edycji, gdyż nie wybrano osoby");
            alertBox.initOwner(stageManager.getWindow());
            alertBox.show();
            return;
        }

        personDetailsController.setEditable(true);
        mainTableController.disableControls(true);
        disableControls(true);
    }

    public void importDbfClicked(ActionEvent actionEvent) {
        Stage stage = (Stage) stageManager.getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
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
    }


}