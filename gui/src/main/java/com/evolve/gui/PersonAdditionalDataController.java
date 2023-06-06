package com.evolve.gui;

import javafx.fxml.Initializable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("additional-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonAdditionalDataController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
