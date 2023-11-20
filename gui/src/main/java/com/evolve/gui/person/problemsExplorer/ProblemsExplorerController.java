package com.evolve.gui.person.problemsExplorer;

import com.evolve.FindProblems;
import com.evolve.gui.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
    public Button btnRunExplorer;

    @FXML VBox problemsExplorerVBox;
    @FXML TextArea textAreaProblems;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(problemsExplorerVBox));
        stage.setTitle("problems explorer");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(true);
    }

    public void show() {
        stage.show();
    }


    public void runProblemsExplorer(ActionEvent actionEvent) {
        findProblems.findProblems()
                        .forEach(problem ->  {
                            textAreaProblems.appendText(problem);
                            textAreaProblems.appendText(Strings.LINE_SEPARATOR);
                        });
    }
}
