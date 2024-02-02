package com.evolve.alpaca.gui.export;

import com.evolve.gui.StageManager;
import com.evolve.gui.person.list.PersonModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonExportHandler {

    private final StageManager stageManager;

    public void displayExportCriteria(javafx.collections.ObservableList<PersonModel> currentSelection) {
        new PersonExportDialog()
                .showDialog(stageManager.getWindow())
                .ifPresent(personExportCriteria -> {
                    System.out.println("Person export criteria: " + personExportCriteria);
                });
    }

}
