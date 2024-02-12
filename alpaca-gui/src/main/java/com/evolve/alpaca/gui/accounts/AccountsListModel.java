package com.evolve.alpaca.gui.accounts;

import com.evolve.alpaca.account.Account;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class AccountsListModel {
    private final ObservableList<AccountEntry> data = FXCollections.observableArrayList();
    private final ObjectProperty<AccountEntry> currentPersonProperty = new SimpleObjectProperty<>(null);


    public FilteredList<AccountEntry> feed(List<Account> accounts) {
        data.clear();
        accounts.stream()
                .map(AccountEntry::of)
                .forEach(data::add);
        return getFilteredList();
    }

    public FilteredList<AccountEntry> getFilteredList() {
        return new FilteredList<>(data, p -> true);
    }

}
