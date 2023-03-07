package com.evolve.gui.dictionaries;

import com.evolve.domain.Unit;
import com.evolve.gui.events.StageReadyEvent;
import com.evolve.services.UnitsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@Component
@FxmlView("units-dialog.fxml")
@RequiredArgsConstructor
@Slf4j
public class UnitsController implements Initializable, ApplicationListener<StageReadyEvent> {
    private final UnitsService unitsService;
    private final ObservableList<UnitEntry> units = FXCollections.observableArrayList();

    private Stage primaryStage;
    private Stage stage;

    @FXML AnchorPane unitsDialog;
    @FXML TableView<UnitEntry> unitsTable;
    @FXML TableColumn<UnitEntry, String> unitNumberColumn;
    @FXML TableColumn<UnitEntry, String> unitDescriptionColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stage = new Stage();
        stage.initOwner(primaryStage);
        stage.setScene(new Scene(unitsDialog));
        stage.setTitle("Jednostki");
        stage.initModality(Modality.WINDOW_MODAL);
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        unitDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("unitDescription"));

        final Map<String, Unit> unitsMap = unitsService.fetchAll();

        unitsMap.keySet()
                .stream()
                .sorted()
                .forEach(unitNumber -> {
                    final Unit unit = unitsMap.get(unitNumber);
                    this.units.add(new UnitEntry(unitNumber, unit != null ? unit.getName() : ""));
                });
        unitsTable.setItems(this.units);
    }

//    @FXML
//    public void initialize() {
//        this.stage = new Stage();
//        stage.setScene(new Scene(unitsDialog));

//        openAnotherDialogButton.setOnAction(
//                actionEvent -> anotherControllerAndView.getController().show()
//                                           );
//        closeButton.setOnAction(
//                actionEvent -> stage.close()
//                               );
//    }

    public void show() {
        stage.show();
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.primaryStage = event.getStage();
        log.info("primaryStage set");
    }

    @AllArgsConstructor
    @Getter
    public static class UnitEntry {
        private String unitNumber;
        private String unitDescription;
    }
}
