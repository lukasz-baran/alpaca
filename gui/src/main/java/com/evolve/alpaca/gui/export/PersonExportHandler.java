package com.evolve.alpaca.gui.export;

import com.evolve.alpaca.export.PersonExportService;
import com.evolve.gui.StageManager;
import com.evolve.gui.person.list.PersonModel;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonExportHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final String FILE_NAME_PATTERN = "alpaca_export_%s.csv";


    private final StageManager stageManager;
    private final PersonExportService alpacaExportService;

    public void displayExportCriteria(ObservableList<PersonModel> currentSelection) {
        new PersonExportDialog()
                .showDialog(stageManager.getWindow())
                .ifPresent(personExportCriteria -> {
                    System.out.println("Person export criteria: " + personExportCriteria);

                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Zapisz plik");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
                    fileChooser.setInitialFileName(getInitialFileName());

                    final File fileToSave = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
                    if (fileToSave == null) {
                        log.info("No file selected - user must have canceled file selection");
                        return;
                    }

                    log.info("Selected file: {}", fileToSave);
                    var orderedElements = currentSelection.stream().map(PersonModel::getId).toList();
                    alpacaExportService.exportPersons(personExportCriteria, fileToSave, orderedElements);
                });
    }

    private String getInitialFileName() {
        return String.format(FILE_NAME_PATTERN, LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

}
