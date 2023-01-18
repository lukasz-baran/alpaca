package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.services.PersonsService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("person-details.fxml")
@RequiredArgsConstructor
public class PersonDetailsController implements Initializable {
    private final PersonsService personsService;
    private final PersonListModel personListModel;

    @FXML
    Button addButton;

    private final ObjectProperty<LocalDate> birthDay = new SimpleObjectProperty<>();


    @FXML TextField idTextField;
    @FXML TextField firstNameTextField;
    @FXML TextField secondNameTextField;
    @FXML TextField lastNameTextField;
    @FXML RadioButton maleRadioButton;
    @FXML RadioButton femaleRadioButton;
    @FXML ToggleGroup genderToggleGroup;
    @FXML DatePicker dobPicker;
    @FXML TextField registryNumberTextField;
    @FXML TextField oldRegistryNumberTextField;
    @FXML TextField emailTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialized - PersonDetails");

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    setPerson(newUser);
                });

    }

    public void setPerson(PersonModel personModel) {
        Person person = personsService.findById(personModel.getId());
        System.out.println(person);

        idTextField.setText(person.getPersonId());
        firstNameTextField.setText(person.getFirstName());
        secondNameTextField.setText(person.getSecondName());
        lastNameTextField.setText(person.getLastName());
        emailTextField.setText(person.getEmail());
        registryNumberTextField.setText(Optional.ofNullable(person.getRegistryNum())
                .map(Object::toString).orElse(null));
        oldRegistryNumberTextField.setText(Optional.ofNullable(person.getOldRegistryNum())
                .map(Object::toString).orElse(null));

        genderToggleGroup.selectToggle(Person.Gender.MALE == person.getGender()
                ? maleRadioButton
                : femaleRadioButton);

        this.birthDay.setValue(person.getDob());
        Bindings.bindBidirectional(dobPicker.valueProperty(), birthDay);


    }
}
