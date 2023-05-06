package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.gui.EditableGuiElement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("gender-select.fxml")
@Slf4j
public class GenderComboboxController extends EditableGuiElement implements Initializable {
    private final ObjectProperty<Person.Gender> genderObjectProperty = new SimpleObjectProperty<>();
    @FXML HBox genderComboHBox;
    @FXML ComboBox<Person.Gender> genderCombo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genderCombo.getItems().addAll(Person.Gender.values());
        genderCombo.valueProperty().bindBidirectional(genderObjectProperty);

        genderCombo.setDisable(true);
        genderCombo.setEditable(false);
        genderCombo.getSelectionModel().select(null);
    }

    public void setPersonGender(Person person) {
        log.info("Setting person gender: {}", person.getGender());

        genderObjectProperty.setValue(person.getGender());
    }

    @Override
    public void setEditable(boolean editable) {
        System.out.println("EDIT gender " + editable);
//        if (editable) {
//            genderCombo.setOnShown(null);
//        } else {
//            genderCombo.setOnShown(event -> genderCombo.hide());
//        }
//        genderCombo.setDisable(!editable);
//        genderCombo.setEditable(editable);
//        genderCombo.getEditor().setEditable(!editable);

//        genderCombo.getItems().clear();
//        genderCombo.getItems().addAll(Person.Gender.values());
//        genderCombo.valueProperty().bindBidirectional(genderObjectProperty);
//        genderCombo.setDisable(false);
//        genderCombo.setOnShown(event -> genderCombo.hide());
//        genderCombo.setEditable(false);

//        genderCombo.setDisable(false);
//        genderCombo.getItems().addAll(Person.Gender.values());
//        genderCombo.valueProperty().bindBidirectional(genderObjectProperty);
        //genderCombo.setEditable(editable);

        //Node parent = genderCombo.getParent();

    }
}
