package com.evolve.gui;

import com.evolve.alpaca.conf.LocalUserConfiguration;
import com.evolve.alpaca.document.DocumentEntry;
import com.evolve.alpaca.gui.accounts.AccountsController;
import com.evolve.alpaca.gui.comments.PersonCommentEntry;
import com.evolve.alpaca.gui.comments.PersonCommentsController;
import com.evolve.alpaca.gui.games.FifteenPuzzleDialog;
import com.evolve.alpaca.gui.help.AboutDialogWindow;
import com.evolve.alpaca.gui.problems.ProblemsExplorerController;
import com.evolve.alpaca.gui.stats.StatsController;
import com.evolve.alpaca.gui.units.UnitsController;
import com.evolve.gui.admin.importDbf.DbfFiles;
import com.evolve.gui.admin.importDbf.ImportDbfController;
import com.evolve.gui.admin.importDbf.ImportDbfDialog;
import com.evolve.gui.documents.DocumentsController;
import com.evolve.gui.person.accounts.PersonAccountsController;
import com.evolve.gui.person.details.PersonDetailsController;
import com.evolve.gui.person.event.PersonEditionRequestedEvent;
import com.evolve.gui.person.list.MainTableController;
import com.evolve.gui.person.list.PersonListModel;
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
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Getter
@Component
@FxmlView("main-view.fxml")
@RequiredArgsConstructor
@Slf4j
public class AppController implements Initializable {

    private final PersonListModel personListModel;

    private final StageManager stageManager;
    private final LocalUserConfiguration localUserConfiguration;

    private final FxControllerAndView<AboutDialogWindow, AnchorPane> aboutDialogController;
    private final FxControllerAndView<UnitsController, VBox> unitsDialogController;
    private final FxControllerAndView<AccountsController, VBox> accountsDialogController;

    private final PersonDetailsController personDetailsController;
    private final MainTableController mainTableController;

    private final FxControllerAndView<PersonAccountsController, VBox> personAccountsController;
    private final FxControllerAndView<DocumentsController, VBox> documentsController;
    private final FxControllerAndView<ProblemsExplorerController, VBox> problemsExplorerController;
    private final FxControllerAndView<StatsController, VBox> statsController;
    private final FxControllerAndView<FifteenPuzzleDialog, VBox> fifteenPuzzleController;
    private final FxControllerAndView<ImportDbfController, VBox> importProgressController;

    private final PersonCommentsController personCommentsController;

    @FXML TabPane tabsPane;
    @FXML Tab tabPersonDetails;
    @FXML Tab tabOriginalDetails;
    @FXML Tab tabPersonAdditionalData;
    @FXML Tab tabDocuments;
    @FXML Tab tabComments;

    @FXML MenuItem newMenuItem;
    @FXML MenuItem quitMenuItem;
    @FXML MenuItem removeMenuItem;
    @FXML MenuItem deletePersonDataMenuItem;
    @FXML MenuItem importDbfMenuItem;
    @FXML MenuItem unitsMenuItem;
    @FXML MenuItem aboutMenuItem;
    @FXML MenuItem accountsMenuItem;
    @FXML MenuItem statsMenuItem;
    @FXML MenuItem puzzleMenuItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Locale.setDefault(new Locale("pl"));

        personAccountsController.getView()
                        .ifPresent(vBox -> tabPersonAdditionalData.setContent(vBox));
        documentsController.getView()
                        .ifPresent(vBox -> tabDocuments.setContent(vBox));

        quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));

        unitsMenuItem.setOnAction(event -> unitsDialogController.getController().show());
        accountsMenuItem.setOnAction(event -> accountsDialogController.getController().show());

        aboutMenuItem.setOnAction(event -> aboutDialogController.getController().show());

        personAccountsController.getController()
                .getAccountsList()
                        .addListener((ListChangeListener<? super PersonAccountsController.AccountEntry>) change ->
                                tabPersonAdditionalData.textProperty().setValue("Konta (" + change.getList().size() + ")"));
        documentsController.getController()
                .getDocumentsList()
                    .addListener((ListChangeListener<? super DocumentEntry>) change -> tabDocuments.textProperty()
                            .setValue("Dokumenty (" + change.getList().size() + ")"));

        personCommentsController.getCommentsList()
                .addListener((ListChangeListener<? super PersonCommentEntry>) change -> tabComments.textProperty()
                        .setValue("Notatki (" + change.getList().size() + ")"));

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
        final Optional<DbfFiles> importData = new ImportDbfDialog(stageManager, localUserConfiguration)
                .showDialog(stageManager.getWindow());

        importData.ifPresent(dbFiles -> {
            log.info("Opened dbf file {}", dbFiles);
            if (dbFiles.getMainFile() != null && dbFiles.getDocFile() != null) {

                importProgressController.getController().showAndImport(dbFiles);
            }
        });
}

    @FXML
    void lookForErrorsClicked(ActionEvent actionEvent) {
        problemsExplorerController.getController().show();
    }

    @EventListener
    public void onApplicationEvent(PersonEditionRequestedEvent event) {
        // we pass null because the person is already set in the model

        // change active tab to person details:
        tabsPane.getSelectionModel().select(tabPersonDetails);

        personDetailsController.setEditable(true);
    }

    @FXML
    void showStatsClicked(ActionEvent actionEvent) {
        statsController.getController().show();
    }

    @FXML
    void openPuzzleGame(ActionEvent actionEvent) {
        fifteenPuzzleController.getController().show();
    }
}