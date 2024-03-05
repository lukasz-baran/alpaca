package com.evolve.alpaca.gui.stats;

import com.evolve.FindPerson;
import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.PersonStatus;
import com.evolve.gui.StageManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Component
@FxmlView("stats.fxml")
@RequiredArgsConstructor
@Slf4j
public class StatsController implements Initializable {
    private final StageManager stageManager;
    private final FindPerson findPerson;
    private Stage stage;

    @FXML AnchorPane statsAnchorPane;
    @FXML PieChart pieChart;

    private String toText(double number) {
        final Double d = number;
        return String.valueOf(d.intValue());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(statsAnchorPane));
        stage.setTitle("Statusy");
        // experimentally we don't display window as modal:
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(true);
    }

    public void show() {
        stage.show();
        showStats();
    }

    void showStats() {
        final List<Person> personList = findPerson.fetch(PersonLookupCriteria.ALL);
        final Map<PersonStatus, Long> groupedByStatus = personList
                .stream()
                .collect(Collectors.groupingByConcurrent(Person::getStatus, Collectors.counting()));

        final List<PieChart.Data> pieChartData = groupedByStatus.entrySet()
                .stream()
                .map(entry -> new PieChart.Data(entry.getKey().getName() + " (" + toText(entry.getValue()) + ")",
                        entry.getValue()))
                .collect(Collectors.toList());

        pieChart.setData(FXCollections.observableList(pieChartData));
        pieChart.setTitle("Wszystkich: " + personList.size());

        for(PieChart.Data data : pieChart.getData()) {
            // TODO
//            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> label.setText(toText(data.getPieValue())));
        }
    }

}
