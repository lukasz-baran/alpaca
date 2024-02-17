package com.evolve.gui.person.details;

import com.evolve.domain.Person;
import com.evolve.gui.StageManager;
import com.evolve.services.AlpacaPeselValidator;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class PeselValidationListener implements InvalidationListener {
    private final TextField peselTextField;

    @Setter
    private Person person;

    @Override
    public void invalidated(Observable observable) {
        final String maybePesel = peselTextField.getText();
        peselTextField.setStyle("");
        peselTextField.setTooltip(null);

        if (StringUtils.isEmpty(maybePesel)) {
            return;
        }

        var validator = new AlpacaPeselValidator(person.getDob(), person.getGender());
        var validationResult = validator.validate(maybePesel);
        if (!validationResult.isValid()) {
            peselTextField.setStyle("-fx-border-color: red");
            peselTextField.setTooltip(StageManager.buildTooltip(validationResult));
        }
    }
}
