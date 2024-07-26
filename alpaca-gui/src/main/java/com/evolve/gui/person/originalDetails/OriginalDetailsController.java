package com.evolve.gui.person.originalDetails;

import com.evolve.EditPersonDataCommand;
import com.evolve.FindPerson;
import com.evolve.alpaca.auditlog.AuditEntry;
import com.evolve.alpaca.auditlog.FindAuditLog;
import com.evolve.alpaca.importing.importDbf.fixers.PersonFixer;
import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import com.evolve.gui.person.preview.PersonPreviewDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@FxmlView("original-details.fxml")
@RequiredArgsConstructor
@Slf4j
public class OriginalDetailsController implements Initializable {
    public static final Set<String> HIDDEN_DATA = Set.of("WWW", "NIP_UE");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private final ObjectMapper objectMapper = LogUtil.OBJECT_MAPPER;

    private final FindPerson findPerson;
    private final PersonFixer personFixer;
    private final PersonListModel personListModel;
    private final FindAuditLog findAuditLog;

    private final ObservableList<DetailsEntry> originalData = FXCollections.observableArrayList();
    private final ObservableList<FixerEntry> fixerData = FXCollections.observableArrayList();
    private final ObservableList<EditHistoryEntry> editionHistoryData = FXCollections.observableArrayList();

    private final FxControllerAndView<PersonPreviewDialog, HBox> previewDialog;

    @FXML MenuItem copyValue;
    @FXML TableColumn<DetailsEntry, String> keyColumn;
    @FXML TableColumn<DetailsEntry, String> valueColumn;
    @FXML TableView<DetailsEntry> originalDetailsTable;

    @FXML TableView<FixerEntry> fixersDataTable;
    @FXML TableColumn<FixerEntry, String> fieldColumn;
    @FXML TableColumn<FixerEntry, String> newValueColumn;

    @FXML TableView<EditHistoryEntry> editHistoryTable;
    @FXML TableColumn<EditHistoryEntry, String> editWhenColumn;
    @FXML TableColumn<EditHistoryEntry, String> editInfoColumn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        originalDetailsTable.setItems(originalData);

        fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
        newValueColumn.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        fixersDataTable.setItems(fixerData);

        editWhenColumn.setCellValueFactory(new PropertyValueFactory<>("when"));
        editWhenColumn.setCellValueFactory(param -> {
            final EditHistoryEntry entry = param.getValue();

            return new SimpleStringProperty(entry.formatDate());
        });

        editInfoColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        editHistoryTable.setItems(editionHistoryData);
        editHistoryTable.setRowFactory(tableView -> {
            final TableRow<EditHistoryEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    if (!row.isEmpty()) {
                        showChange(row.getItem());
                    }
                }
            });
            return row;
        });


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

        setUpOriginalData(personModel.getId());
        setUpFixerData(personModel.getId());
        setUpEditionHistory(personModel.getId());
    }

    private void setUpOriginalData(final String personId) {
        final Person person = findPerson.findById(personId);
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
                    personId);
        }
    }

    private void setUpFixerData(final String personId) {
        fixerData.clear();
        personFixer.getRecords(personId)
                .entrySet()
                .stream()
                .map(FixerEntry::of)
                .forEach(fixerData::add);
    }

    private void setUpEditionHistory(final String personId) {
        editionHistoryData.clear();
        findAuditLog.findById(EditPersonDataCommand.class, personId)
                .forEach(auditEntry -> editionHistoryData.add(EditHistoryEntry.of(auditEntry)));
    }

    @FXML
    public void showChange(ActionEvent actionEvent) {
        final EditHistoryEntry editHistoryEntry = editHistoryTable.getSelectionModel().getSelectedItem();
        showChange(editHistoryEntry);
    }

    @SneakyThrows
    private void showChange(EditHistoryEntry editHistoryEntry) {
        AuditEntry auditEntry = editHistoryEntry.auditEntry;

        final Person personBefore = objectMapper.readValue(auditEntry.getBefore(), Person.class);
        final Person personAfter = objectMapper.readValue(auditEntry.getAfter(), Person.class);

        final String title = editHistoryEntry.formatDate();

        previewDialog.getController().open(title, personBefore, personAfter);
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

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class EditHistoryEntry {
        private LocalDateTime when;
        private String comment;
        private AuditEntry auditEntry;

        public static EditHistoryEntry of(AuditEntry entry) {
            return new EditHistoryEntry(entry.getWhen(), "edycja", entry);
        }

        String formatDate() {
            return Optional.ofNullable(when)
                    .map(value -> value.format(DATE_TIME_FORMATTER))
                    .orElse(StringUtils.EMPTY);
        }
    }
}
