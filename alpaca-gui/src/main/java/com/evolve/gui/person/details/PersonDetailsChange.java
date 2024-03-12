package com.evolve.gui.person.details;

import com.evolve.EditPersonDataCommand;
import com.evolve.domain.Person;
import com.evolve.exception.AlpacaBusinessException;
import javafx.beans.property.ObjectProperty;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class PersonDetailsChange {
    private final ObjectProperty<Person> originalData;

    public EditPersonDataCommand buildCommand(PersonDetailsController controller) {
        if (originalData == null) {
            throw new AlpacaBusinessException("Unable to create command - are you trying to edit an empty record?");
        }

        return new EditPersonDataCommand(
                controller.idTextField.getText(),
                controller.firstNameTextField.getText(),
                controller.personLastNamesController.getController().getLastName(),
                controller.secondNameTextField.getText(),
                controller.phoneNumbersController.getController().getNumbers(),
                controller.personAddresses.getController().getPersonAddresses(),
                controller.authorizedController.getController().getAuthorizedPersons(),
                controller.personStatusController.getController().getStatusChanges(),
                controller.unitNumberComboBox.getSelectionModel().getSelectedItem().unitNumber(),
                controller.registryNumberTextField.getText(),
                controller.oldRegistryNumberTextField.getText(),
                controller.personBankAccountsController.getController().getAccounts(),
                controller.retiredCheckBox.isSelected(),
                controller.exemptFromFeesCheckBox.isSelected(),
                newPesel(controller.peselTextField.getText()),
                newIdNumber(controller.idNumberTextField.getText()));
    }

    /**
     * @return {@code null} if we don't want to introduce any change to the entity, otherwise:
     *          "" if we clear the field or the new value
     */
    String newPesel(String peselText) {
        if (originalData.getValue() == null) {
            return null;
        }
        return getStringValue(originalData.getValue().getPesel(), StringUtils.trimToEmpty(peselText));
    }

    String newIdNumber(String idNumberText) {
        if (originalData.getValue() == null) {
            return null;
        }
        return getStringValue(originalData.getValue().getIdNumber(), StringUtils.trimToEmpty(idNumberText));
    }

    private static String getStringValue(String oldValue, String newValue) {
        if (oldValue == null && StringUtils.isEmpty(newValue)) {
            return null;
        }

        if (StringUtils.equals(newValue, oldValue)) {
            return null;
        }
        return newValue;
    }
}
