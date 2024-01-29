package com.evolve.gui;

import com.evolve.alpaca.conf.LocalUserConfiguration;
import com.evolve.alpaca.gui.help.AboutDialogWindow;
import com.evolve.alpaca.gui.units.UnitsController;
import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import com.evolve.alpaca.importing.importDoc.ImportAlphanumeric;
import com.evolve.alpaca.importing.importDoc.ImportPeople;
import com.evolve.alpaca.importing.importDoc.group.GrupyAlfabetyczne;
import com.evolve.alpaca.importing.importDoc.person.PersonFromDoc;
import com.evolve.gui.admin.importDbf.ImportDbfDialog;
import com.evolve.gui.documents.DocumentEntry;
import com.evolve.gui.documents.DocumentsController;
import com.evolve.gui.person.PersonDetailsController;
import com.evolve.gui.person.accounts.PersonAccountsController;
import com.evolve.gui.person.event.PersonEditionRequestedEvent;
import com.evolve.gui.person.list.MainTableController;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.originalDetails.OriginalDetailsController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
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
public class AppController implements Initializable {

    private final PersonListModel personListModel;

    private final ImportDbfService importDbfService;
    private final FxWeaver fxWeaver;
    private final StageManager stageManager;
    private final LocalUserConfiguration localUserConfiguration;

    private final FxControllerAndView<AboutDialogWindow, AnchorPane> aboutDialogController;
    private final FxControllerAndView<UnitsController, VBox> unitsDialogController;

    private final PersonDetailsController personDetailsController;
    private final MainTableController mainTableController;

    private final FxControllerAndView<PersonAccountsController, VBox> personAccountsController;
    private final FxControllerAndView<DocumentsController, VBox> documentsController;

    @FXML TabPane tabsPane;
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
    @FXML MenuItem aboutMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(new Locale("pl"));
        tabPersonDetails.setContent(fxWeaver.loadView(PersonDetailsController.class));
        tabOriginalDetails.setContent(fxWeaver.loadView(OriginalDetailsController.class));

        personAccountsController.getView()
                        .ifPresent(vBox -> tabPersonAdditionalData.setContent(vBox));
        documentsController.getView()
                        .ifPresent(vBox -> tabDocuments.setContent(vBox));

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

        unitsMenuItem.setOnAction(event -> unitsDialogController.getController().show());

        aboutMenuItem.setOnAction(event -> aboutDialogController.getController().show());

        personAccountsController.getController()
                .getAccountsList()
                        .addListener((ListChangeListener<? super PersonAccountsController.AccountEntry>) change ->
                                tabPersonAdditionalData.textProperty().setValue("Konta (" + change.getList().size() + ")"));
        documentsController.getController()
                .getDocumentsList()
                    .addListener((ListChangeListener<? super DocumentEntry>) change -> tabDocuments.textProperty()
                            .setValue("Dokumenty (" + change.getList().size() + ")"));
    }

    public void quitClicked(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void newPersonButtonClicked(ActionEvent actionEvent) {
        mainTableController.newPersonButtonClicked(actionEvent);
    }

    @FXML
    void importDbfClicked(ActionEvent actionEvent) {
        new ImportDbfDialog(stageManager, localUserConfiguration)
                .showDialog(stageManager.getWindow())
                .ifPresent(dbFiles -> {
                    log.info("Opened dbf file {}", dbFiles);
                    if (dbFiles.getMainFile() != null) {
                        importDbfService.startImport(dbFiles.getMainFile().getPath(),
                                dbFiles.getPlanAccountsFile().getPath());
                    }
                });
    }

    @FXML
    void importPeopleClicked(ActionEvent actionEvent) {
        final List<PersonFromDoc> people = new ImportPeople(false).processFile();

        final GrupyAlfabetyczne grupyAlfabetyczne = new ImportAlphanumeric()
                .processFile();
    }

    @EventListener
    public void onApplicationEvent(PersonEditionRequestedEvent event) {
        // we pass null because the person is already set in the model

        // change active tab to person details:
        tabsPane.getSelectionModel().select(tabPersonDetails);

        personDetailsController.setEditable(true);
    }

}