package com.evolve.gui.admin.importDbf;

import com.evolve.alpaca.conf.LocalUserConfiguration;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;

@Slf4j
public class ImportDbfDialog extends DialogWindow<DbfFiles> {
    public static final FileChooser.ExtensionFilter DBF_DOCUMENTS_EXTENSION_FILTER =
            new FileChooser.ExtensionFilter("pliki DBF (*.dbf)", "*.dbf");

    public static final FileChooser.ExtensionFilter DOC_DOCUMENTS_EXTENSION_FILTER =
            new FileChooser.ExtensionFilter("pliki Word (*.doc)", "*.doc");

    private final StageManager stageManager;
    private final LocalUserConfiguration localUserConfiguration;
    private final TextField mainFilePathTextField = new TextField();
    private final TextField accountsFilePathTextField = new TextField();
    private final TextField docFilePathTextField = new TextField();

    public ImportDbfDialog(StageManager stageManager, LocalUserConfiguration localUserConfiguration) {
        super("Import plików ze systemu FK", "Wybierz pliki DBF ze starej aplikacji oraz PLAN KONT.doc");
        this.stageManager = stageManager;
        this.localUserConfiguration = localUserConfiguration;
    }

    @Override
    public Optional<DbfFiles> showDialog(Window window) {
        final Dialog<DbfFiles> dialog = createDialog(window);
        findSubmitButton(dialog).setText("Importuj");

        final GridPane grid = createGridPane();

        final DbfFiles dbfFiles = new DbfFiles();
        final Button saveButton = findSubmitButton(dialog);
        saveButton.setDisable(true);

        final HBox groupMain = createMainFileChooser(dbfFiles, saveButton);
        final HBox groupAccounts = createAccountsFileChooser(dbfFiles, saveButton);
        final HBox groupDocFile = createAccountsPlanFileChooser(dbfFiles, saveButton);

        grid.add(new Label("Główny plik DBF:"), 0, 0);
        grid.add(groupMain, 1, 0);

        grid.add(new Label("Plik kont DBF:"), 0, 1);
        grid.add(groupAccounts, 1, 1);

        grid.add(new Label("Plik kont DOC:"), 0, 2);
        grid.add(groupDocFile, 1, 2);

        validateSaveButton(saveButton);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && dbfFiles.isReady()) {
                localUserConfiguration.setConfigurationProperty(LocalUserConfiguration.Z_B_KO_DBF_LOCATION, dbfFiles.getMainFile().getPath());
                localUserConfiguration.setConfigurationProperty(LocalUserConfiguration.PLAN_DBF_LOCATION, dbfFiles.getPlanAccountsFile().getPath());
                localUserConfiguration.setConfigurationProperty(LocalUserConfiguration.PLAN_DOC_LOCATION, dbfFiles.getDocFile().getPath());

                return dbfFiles;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private HBox createMainFileChooser(DbfFiles dbfFiles, Button saveButton) {
        mainFilePathTextField.setPromptText("Z_B_KO.DBF");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DBF_DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            dbfFiles.setMainFile(file);

            mainFilePathTextField.setText(file.getAbsolutePath());
        });

        localUserConfiguration.loadProperty(LocalUserConfiguration.Z_B_KO_DBF_LOCATION)
                .ifPresent(filePath -> {
                    mainFilePathTextField.setText(filePath);
                    dbfFiles.setMainFile(new File(filePath));
                });

        return newHBox(mainFilePathTextField, chooseFileButton, saveButton);
    }

    private HBox createAccountsFileChooser(DbfFiles dbfFiles, Button saveButton) {
        accountsFilePathTextField.setPromptText("PLAN.DBF");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DBF_DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            dbfFiles.setPlanAccountsFile(file);

            accountsFilePathTextField.setText(file.getAbsolutePath());
        });

        localUserConfiguration.loadProperty(LocalUserConfiguration.PLAN_DBF_LOCATION)
                .ifPresent(filePath -> {
                    accountsFilePathTextField.setText(filePath);
                    dbfFiles.setPlanAccountsFile(new File(filePath));
                });

        return newHBox(accountsFilePathTextField, chooseFileButton, saveButton);
    }

    private HBox createAccountsPlanFileChooser(DbfFiles dbfFiles, Button saveButton) {
        docFilePathTextField.setPromptText("PLAN KONT.doc");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DOC_DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            dbfFiles.setDocFile(file);

            docFilePathTextField.setText(file.getAbsolutePath());
        });

        localUserConfiguration.loadProperty(LocalUserConfiguration.PLAN_DOC_LOCATION)
                .ifPresent(filePath -> {
                    docFilePathTextField.setText(filePath);
                    dbfFiles.setDocFile(new File(filePath));
                });

        return newHBox(docFilePathTextField, chooseFileButton, saveButton);
    }

    private HBox newHBox(TextField textField, Button chooseFileButton, Button saveButton) {
        final HBox group = new HBox();
        group.setSpacing(10);
        group.getChildren().add(textField);
        group.getChildren().add(chooseFileButton);
        textField.textProperty().addListener((observable, oldValue, newValue) -> validateSaveButton(saveButton));
        return group;
    }

    @Override
    protected void validateSaveButton(Node saveButton) {
        final boolean notEmpty = accountsFilePathTextField.getText().trim().isEmpty() ||
                mainFilePathTextField.getText().trim().isEmpty() ||
                docFilePathTextField.getText().trim().isEmpty();

        saveButton.setDisable(notEmpty);
    }


}
