package com.evolve.gui.admin.importDbf;

import com.evolve.alpaca.conf.LocalUserConfiguration;
import com.evolve.gui.DialogWindow;
import com.evolve.gui.StageManager;
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
    public static final FileChooser.ExtensionFilter DOCUMENTS_EXTENSION_FILTER =
            new FileChooser.ExtensionFilter("documents", "*.dbf");

    private final StageManager stageManager;
    private final LocalUserConfiguration localUserConfiguration;

    public ImportDbfDialog(StageManager stageManager, LocalUserConfiguration localUserConfiguration) {
        super("Nowy document", "Wybierz pliki DBF ze starej aplikacji");
        this.stageManager = stageManager;
        this.localUserConfiguration = localUserConfiguration;
    }

    @Override
    public Optional<DbfFiles> showDialog(Window window) {
        final Dialog<DbfFiles> dialog = createDialog(window);

        final GridPane grid = createGridPane();

        final DbfFiles dbfFiles = new DbfFiles();
        final Button saveButton = findSubmitButton(dialog);
        saveButton.setDisable(true);

        final HBox groupMain = createMainFileChooser(dbfFiles, saveButton);
        final HBox groupAccounts = createAccountsFileChooser(dbfFiles, saveButton);

        grid.add(new Label("Główny plik DBF:"), 0, 0);
        grid.add(groupMain, 1, 0);

        grid.add(new Label("Plik kont DBF:"), 0, 1);
        grid.add(groupAccounts, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType && dbfFiles.isReady()) {
                localUserConfiguration.setConfigurationProperty(LocalUserConfiguration.Z_B_KO_DBF_LOCATION, dbfFiles.getMainFile().getPath());
                localUserConfiguration.setConfigurationProperty(LocalUserConfiguration.PLAN_DBF_LOCATION, dbfFiles.getPlanAccountsFile().getPath());

                return dbfFiles;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private HBox createMainFileChooser(DbfFiles dbfFiles, Button saveButton) {
        final HBox group = new HBox();
        group.setSpacing(10);

        final TextField filePathTextField = new TextField();
        filePathTextField.setPromptText("Z_B_KO.DBF");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            dbfFiles.setMainFile(file);

            filePathTextField.setText(file.getAbsolutePath());
        });
        group.getChildren().add(filePathTextField);
        group.getChildren().add(chooseFileButton);

        localUserConfiguration.loadProperty(LocalUserConfiguration.Z_B_KO_DBF_LOCATION)
                .ifPresent(filePath -> {
                    filePathTextField.setText(filePath);
                    dbfFiles.setMainFile(new File(filePath));
                });

        updateSaveButtonState(saveButton, filePathTextField.getText());

        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, newValue);
        });

        return group;
    }

    private HBox createAccountsFileChooser(DbfFiles dbfFiles, Button saveButton) {
        final HBox group = new HBox();
        group.setSpacing(10);

        final TextField filePathTextField = new TextField();
        filePathTextField.setPromptText("PLAN.DBF");

        final Button chooseFileButton = new Button("Wybierz plik");
        chooseFileButton.onActionProperty().setValue(event -> {
            final File file = stageManager.getFileChooser(DOCUMENTS_EXTENSION_FILTER);
            if (file == null) {
                log.info("No file selected - user must have canceled file selection");
                return;
            }
            dbfFiles.setPlanAccountsFile(file);

            filePathTextField.setText(file.getAbsolutePath());
        });
        group.getChildren().add(filePathTextField);
        group.getChildren().add(chooseFileButton);

        localUserConfiguration.loadProperty(LocalUserConfiguration.PLAN_DBF_LOCATION)
                .ifPresent(filePath -> {
                    filePathTextField.setText(filePath);
                    dbfFiles.setPlanAccountsFile(new File(filePath));
                });
        updateSaveButtonState(saveButton, filePathTextField.getText());

        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, newValue);
        });

        return group;
    }

    private void updateSaveButtonState(Button saveButton, String newValue) {
        saveButton.setDisable(newValue.trim().isEmpty());
    }

}
