package com.evolve.alpaca.gui.problems;

import com.evolve.FindProblems;
import com.evolve.gui.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;


@Component
@FxmlView("problems-explorer.fxml")
@RequiredArgsConstructor
@Slf4j
public class ProblemsExplorerController implements Initializable {
    private final StageManager stageManager;
    private final FindProblems findProblems;

    private Stage stage;
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
    }

    public void show() {
        stage.show();
    }


    @FXML
    void registryNumbersIssues(ActionEvent actionEvent) {
        textAreaProblems.clear();
        findProblems.findRegistryNumbersIssues()
                        .forEach(problem ->  {
                            textAreaProblems.appendText(problem);
                            textAreaProblems.appendText(Strings.LINE_SEPARATOR);
                        });
    }

    @FXML
    void missingStatuses(ActionEvent actionEvent) {
        textAreaProblems.clear();
        findProblems.findMissingDates()
                .forEach(problem ->  {
                    textAreaProblems.appendText(problem);
                    textAreaProblems.appendText(Strings.LINE_SEPARATOR);
                });
    }

    @FXML
    void invalidAddresses(ActionEvent actionEvent) {
        textAreaProblems.clear();
        findProblems.findInvalidAddresses()
                .forEach(problem ->  {
                    textAreaProblems.appendText(problem);
                    textAreaProblems.appendText(Strings.LINE_SEPARATOR);
                });
    }
}
