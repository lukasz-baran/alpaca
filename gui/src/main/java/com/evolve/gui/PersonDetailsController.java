package com.evolve.gui;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@FxmlView("person-details.fxml")
@RequiredArgsConstructor
public class PersonDetailsController implements Initializable {

    private final PersonListModel personListModel;

    @FXML
    Button addButton;

    @FXML
    TextField firstNameTextField;

    @FXML
    TextField secondNameTextField;

    @FXML
    TextField lastNameTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("initialized - PersonDetails");

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    setPerson(newUser);
                });

    }

    public void setPerson(PersonModel personModel) {


        firstNameTextField.setText(personModel.getFirstName());
        lastNameTextField.setText(personModel.getLastName());
        //firstNameTextField.setText("asv");
        System.out.println(firstNameTextField.getText());

    }
}
