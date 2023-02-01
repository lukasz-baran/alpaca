package com.evolve.gui.components;

import com.evolve.domain.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@FxmlView("authorized-persons-list.fxml")
@Slf4j
public class AuthorizedPersonsController implements Initializable {
    private final ObservableList<AuthorizedPersonEntry> list =
            FXCollections.observableArrayList();

    @FXML TableView<AuthorizedPersonEntry> authorizedPersonsTable;
    @FXML TableColumn<AuthorizedPersonEntry, String> authorizedNameColumn;
    @FXML TableColumn<AuthorizedPersonEntry, String> relationColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authorizedNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        relationColumn.setCellValueFactory(new PropertyValueFactory<>("relation"));

        setAuthorizedPersons(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data
    }

    public void setAuthorizedPersons(List<Person.AuthorizedPerson> authorizedPersons) {
        list.clear();
        log.info("setting {}", authorizedPersons);
        emptyIfNull(authorizedPersons)
                .forEach(authorizedPerson -> {
                    list.add(new AuthorizedPersonEntry(authorizedPerson));
                });

        authorizedPersonsTable.setItems(list);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class AuthorizedPersonEntry {
        private String name;
        private String relation;

        public AuthorizedPersonEntry(Person.AuthorizedPerson authorizedPerson) {
            this(authorizedPerson.getFirstName() + " " + authorizedPerson.getLastName(),
                    authorizedPerson.getRelation());
        }
    }

}
