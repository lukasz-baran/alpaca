package com.evolve.gui.components;

import com.evolve.gui.StageManager;
import javafx.fxml.Initializable;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("new-person-dialog.fxml")
@RequiredArgsConstructor
public class NewPersonController implements Initializable {
    private final StageManager stageManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
