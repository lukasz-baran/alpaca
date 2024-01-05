package com.evolve.gui.person.bankAccounts;

import com.evolve.domain.BankAccount;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class BankAccountEntry {
    private final StringProperty number = new SimpleStringProperty();
    private final BooleanProperty invalid = new SimpleBooleanProperty();

    public BankAccountEntry(String number) {
        setNumber(number);
        setInvalid(!BankAccount.isValid(number));
    }

    public String getNumber() {
        return number.get();
    }

    public StringProperty nameProperty() {
        return number;
    }

    public void setNumber(String name) {
        this.number.set(name);
    }

    public boolean getInvalid() {
        return invalid.get();
    }

    public BooleanProperty invalidProperty() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }

    BankAccount toBankAccount() {
        return BankAccount.of(getNumber());
    }


}
