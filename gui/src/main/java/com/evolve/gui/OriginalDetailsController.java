package com.evolve.gui;

import com.evolve.domain.Person;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import com.evolve.services.PersonsService;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

@Component
@FxmlView("original-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class OriginalDetailsController implements Initializable {
    public static final Set<String> HIDDEN_DATA = Set.of("WWW", "NIP_UE");

    private final PersonsService personsService;
    private final PersonListModel personListModel;

    private final ObservableList<DetailsEntry> data = FXCollections.observableArrayList();

    @FXML MenuItem copyValue;
    @FXML TableColumn<DetailsEntry, String> keyColumn;
    @FXML TableColumn<DetailsEntry, String> valueColumn;
    @FXML TableView<DetailsEntry> originalDetailsTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    setPerson(newUser);
                });

        copyValue.setOnAction(event -> {
            final DetailsEntry detailsEntry = originalDetailsTable.getSelectionModel().getSelectedItem();
            if (detailsEntry != null) {
                final String text = detailsEntry.getValue();
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(text);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }
        });
    }

    public void setPerson(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return;
        }

        final Person person = personsService.findById(personModel.getId());
        log.info("Original person details: {}", person);

        data.clear();

        if (MapUtils.isEmpty(person.getRawData())) {
            log.info("No raw data for person {}. This is fine because the person could be added manually",
                    personModel.getId());
            return;
        }

        person.getRawData()
                .entrySet()
                .stream()
                .filter(entry -> !HIDDEN_DATA.contains(entry.getKey()))
                .forEach(entry -> {

                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    data.add(new DetailsEntry(key, value != null ? value.toString() : "null"));
                });

        originalDetailsTable.setItems(data);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class DetailsEntry {
        private String key;
        private String value;
    }
}
