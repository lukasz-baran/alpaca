package com.evolve.gui.components;

import com.evolve.domain.Person;
import com.evolve.domain.RegistryNumber;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("registry-numbers-input.fxml")
@Slf4j
public class RegistryNumbersController implements Initializable {
    private final SimpleStringProperty registryNumberProperty = new SimpleStringProperty();
    private final SimpleStringProperty oldRegistryNumberProperty = new SimpleStringProperty();

    @FXML TextField registryNumberTextField;
    @FXML TextField oldRegistryNumberTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registryNumberTextField.textProperty().bindBidirectional(registryNumberProperty);
        oldRegistryNumberTextField.textProperty().bindBidirectional(oldRegistryNumberProperty);
    }

    public void setPerson(Person person) {
        registryNumberProperty.setValue(
                Optional.ofNullable(person.getRegistryNumber())
                        .map(RegistryNumber::getRegistryNum)
                        .map(Object::toString).orElse(null));
        oldRegistryNumberProperty.setValue(
                Optional.ofNullable(person.getRegistryNumber())
                        .map(RegistryNumber::getOldRegistryNum)
                        .map(Object::toString).orElse(null));
    }
}
