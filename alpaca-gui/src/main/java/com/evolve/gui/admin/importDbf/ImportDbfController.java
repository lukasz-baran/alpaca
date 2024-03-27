package com.evolve.gui.admin.importDbf;

import com.evolve.alpaca.importing.ImportDataCommand;
import com.evolve.alpaca.importing.importDbf.ImportDbfService;
import com.evolve.gui.StageManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Component
@FxmlView("import-progress-dialog.fxml")
@RequiredArgsConstructor
@Slf4j
public class ImportDbfController implements Initializable {
    private final StageManager stageManager;
    private final ImportDbfService importDbfService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private Stage stage;
    @FXML VBox importProgressVBox;
    @FXML Text textNumberStep;
    @FXML TextArea textAreaMessages;
    @FXML ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(importProgressVBox));
        stage.setTitle("Importowanie danych");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(true);
        textAreaMessages.setEditable(false);
    }

    private void clearProgress() {
        textAreaMessages.clear();
        progressBar.setProgress(0.0);
    }

    public void showAndImport(DbfFiles dbFiles) {
        stage.show();
        clearProgress();

        final Task<Integer> task = createImportTask(dbFiles);

        progressBar.progressProperty().bind(task.progressProperty());
        task.messageProperty().addListener((observable, oldValue, newValue) ->  {
            textAreaMessages.appendText(newValue);
            textAreaMessages.appendText(Strings.LINE_SEPARATOR);
        });

        new Thread(task).start();
    }

    private Task<Integer> createImportTask(DbfFiles dbFiles) {
        final Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {

                return importDbfService.startImport(
                        new ImportDataCommand(
                                dbFiles.getMainFile().getPath(),
                                dbFiles.getPlanAccountsFile().getPath(),
                                dbFiles.getDocFile().getPath(),
                                (value, message) -> {
                                    updateProgress(value, 1.0);
                                    updateMessage(message);
                                }
                        ));
            }
        };

        task.setOnFailed(wse -> {
            progressBar.progressProperty().unbind();
            StageManager.showCustomErrorDialog(
                    "Błąd importu!",
                    "Coś poszło nie tak podczas importu danych.",
                    stage.getScene().getWindow(),
                    wse.getSource().getException())
            .show();
        });

        task.setOnSucceeded(wse -> {
            progressBar.progressProperty().unbind();
            stage.close();

            applicationEventPublisher.publishEvent(new DbfImportCompletedEvent(this, task.getValue()));
        });
        return task;
    }

}
