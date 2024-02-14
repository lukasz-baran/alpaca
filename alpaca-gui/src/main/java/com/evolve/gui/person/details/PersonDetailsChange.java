package com.evolve.gui.person.details;

import com.evolve.domain.Person;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class PersonDetailsChange {
    private final ObjectProperty<Person> originalData;

    /**
     * @return {@code null} if we don't want to introduce any change to the entity, otherwise:
     *          "" if we clear the field or the new value
     */
    public String newPesel(TextField peselTextField) {
        return getStringValue(originalData.getValue().getPesel(), StringUtils.trimToEmpty(peselTextField.getText()));
    }

    public String newIdNumber(TextField idNumberTextField) {
        return getStringValue(originalData.getValue().getIdNumber(), StringUtils.trimToEmpty(idNumberTextField.getText()));
    }

    private static String getStringValue(String oldValue, String newValue) {
        if (StringUtils.isNotBlank(oldValue) && StringUtils.equals(newValue, oldValue)) {
            return null;
        }
        return newValue;
    }
}
