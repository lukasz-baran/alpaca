package com.evolve.gui.components;

import com.evolve.domain.Person;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("gender-select.fxml")
@Slf4j
public class GenderComboboxController implements Initializable {
    private final ObjectProperty<Person.Gender> genderObjectProperty = new SimpleObjectProperty<>();
    @FXML ComboBox<Person.Gender> genderCombo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderCombo.getItems().addAll(Person.Gender.values());
        genderCombo.valueProperty().bindBidirectional(genderObjectProperty);
        genderCombo.setDisable(true);
        genderCombo.getSelectionModel().select(null);
    }

    public void setPersonGender(Person person) {
        log.info("Setting person gender: {}", person.getGender());

        genderObjectProperty.setValue(person.getGender());
    }

}
