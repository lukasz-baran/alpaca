package com.evolve.gui.person.originalDetails;

import com.evolve.FindPerson;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.domain.Person;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
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
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

@Component
@FxmlView("original-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class OriginalDetailsController implements Initializable {
    public static final Set<String> HIDDEN_DATA = Set.of("WWW", "NIP_UE");

    private final FindPerson findPerson;
    private final PersonFixer personFixer;
    private final PersonListModel personListModel;

    private final ObservableList<DetailsEntry> originalData = FXCollections.observableArrayList();
    private final ObservableList<FixerEntry> fixerData = FXCollections.observableArrayList();

    @FXML MenuItem copyValue;
    @FXML TableColumn<DetailsEntry, String> keyColumn;
    @FXML TableColumn<DetailsEntry, String> valueColumn;
    @FXML TableView<DetailsEntry> originalDetailsTable;

    @FXML TableView<FixerEntry> fixersDataTable;
    @FXML TableColumn<FixerEntry, String> fieldColumn;
    @FXML TableColumn<FixerEntry, String> newValueColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        originalDetailsTable.setItems(originalData);

        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        fixersDataTable.setItems(fixerData);

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

    private void setPerson(PersonModel personModel) {
        if (personModel == null || personModel.getId() == null) {
            return;
        }

        final Person person = findPerson.findById(personModel.getId());
        log.info("Original person details: {}", person);

        originalData.clear();

        if (!MapUtils.isEmpty(person.getRawData())) {
            person.getRawData()
                    .entrySet()
                    .stream()
                    .filter(entry -> !HIDDEN_DATA.contains(entry.getKey()))
                    .forEach(entry -> {

                        final String key = entry.getKey();
                        final Object value = entry.getValue();
                        originalData.add(new DetailsEntry(key, value != null ? value.toString() : "null"));
                    });
        } else {
            log.info("No raw data for person {}. This is fine because the person could be added manually",
                    personModel.getId());
        }

        fixerData.clear();
        personFixer.getRecords(personModel.getId())
                .entrySet()
                .stream()
                .map(FixerEntry::of)
                .forEach(fixerData::add);

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class DetailsEntry {
        private String key;
        private String value;

        public static DetailsEntry of(Map.Entry<String, String> entry) {
            final String key = entry.getKey();
            final String value = Objects.toString(entry.getValue());
            return new DetailsEntry(key, value);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class FixerEntry {
        private String field;
        private String newValue;

        public static FixerEntry of(Map.Entry<String, String> entry) {
            final String field = PersonFixer.FIXER_TAGS_TO_TEXT.getOrDefault(entry.getKey(), entry.getKey());
            return new FixerEntry(field, entry.getValue());
        }
    }
}
