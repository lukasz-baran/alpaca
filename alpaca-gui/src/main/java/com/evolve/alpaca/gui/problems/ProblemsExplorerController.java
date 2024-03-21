package com.evolve.alpaca.gui.problems;

import com.evolve.FindProblems;
import com.evolve.gui.StageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static com.evolve.gui.StageManager.APPLICATION_ICON;


@Component
@FxmlView("problems-explorer.fxml")
@RequiredArgsConstructor
@Slf4j
public class ProblemsExplorerController implements Initializable {
    private final StageManager stageManager;
    private final FindProblems findProblems;
    private final SimpleIntegerProperty numberOfElements = new SimpleIntegerProperty();

    private Stage stage;
    @FXML Text textNumberOfElements;
    @FXML Button btnRunExplorer;
    @FXML Button btnMissingDates;
    @FXML Button btnInvalidAddresses;

    @FXML VBox problemsExplorerVBox;
    @FXML TextArea textAreaProblems;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(problemsExplorerVBox));
        stage.setTitle("Szukaj błędów");
        // experimentally we don't display window as modal:
        //stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(true);
        textAreaProblems.setEditable(false);
        numberOfElements.set(0);

        textNumberOfElements.textProperty().bind(
                Bindings.format("Liczba znalezionych problemów: %d", numberOfElements));
    }

    public void show() {
        stage.show();
    }

    @FXML
    void registryNumbersIssues(ActionEvent actionEvent) {
        populateForm(findProblems::findRegistryNumbersIssues);
    }

    @FXML
    void missingStatuses(ActionEvent actionEvent) {
        populateForm(findProblems::findMissingDates);
    }

    @FXML
    void invalidAddresses(ActionEvent actionEvent) {
        populateForm(findProblems::findInvalidAddresses);
    }

    void populateForm(Supplier<List<String>> finder) {
        textAreaProblems.clear();
        final List<String> problems = finder.get();
        numberOfElements.set(problems.size());
        problems.forEach(problem ->  {
                    textAreaProblems.appendText(problem);
                    textAreaProblems.appendText(Strings.LINE_SEPARATOR);
                });

    }
}
