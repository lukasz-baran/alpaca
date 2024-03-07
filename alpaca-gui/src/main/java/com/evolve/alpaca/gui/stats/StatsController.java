package com.evolve.alpaca.gui.stats;

import com.evolve.FindPerson;
import com.evolve.domain.Person;
import com.evolve.domain.PersonLookupCriteria;
import com.evolve.domain.PersonStatus;
import com.evolve.gui.StageManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
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

    private Tooltip tooltip = new Tooltip("");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(statsAnchorPane));
        stage.setTitle("Statusy");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(true);

        tooltip.setShowDelay(Duration.ZERO);
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
                .map(entry -> new PieChart.Data(entry.getKey() + " (" + toText(entry.getValue()) + ")",
                        entry.getValue()))
                .collect(Collectors.toList());

        pieChart.setData(FXCollections.observableList(pieChartData));
        pieChart.setTitle("Wszystkich: " + personList.size());

        for (final PieChart.Data data : pieChart.getData()) {
            Tooltip.install(data.getNode(), tooltip);
            applyMouseEvents(data);
        }
    }

    private void applyMouseEvents(final PieChart.Data data) {
        final Node node = data.getNode();

        node.setOnMouseEntered(arg0 -> {
            node.setEffect(new Glow());
            String styleString = "-fx-border-color: white; -fx-border-width: 3; -fx-border-style: dashed;";
            node.setStyle(styleString);

            final StringTokenizer tokenizer = new StringTokenizer(data.getName());
            final Optional<PersonStatus> status = PersonStatus.fromName(tokenizer.nextToken());
            if (status.isPresent() && status.get() == PersonStatus.ACTIVE) {
                final List<Person> personList = findPerson.fetch(PersonLookupCriteria.ALL);
                var numberOfRetired = personList.stream()
                        .filter(person -> person.getStatus() == PersonStatus.ACTIVE)
                        .filter(person -> BooleanUtils.isTrue(person.getRetired()))
                        .count();
                var numberOfExemptFromFees = personList.stream()
                        .filter(person -> person.getStatus() == PersonStatus.ACTIVE)
                        .filter(person -> BooleanUtils.isTrue(person.getExemptFromFees()))
                        .count();
                tooltip.setText("Aktywni: " + (int)data.getPieValue() + "\n" +
                                "Emeryci: " + numberOfRetired + "\n" +
                                "Zwolnieni: " + numberOfExemptFromFees
                );
            } else {
                tooltip.setText(data.getName());
            }
        });

        node.setOnMouseExited(arg0 -> {
            node.setEffect(null);
            node.setStyle("");
        });
    }

    private static String toText(double number) {
        final Double d = number;
        return String.valueOf(d.intValue());
    }


}
