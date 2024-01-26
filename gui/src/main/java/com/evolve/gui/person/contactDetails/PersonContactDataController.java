package com.evolve.gui.person.contactDetails;

import com.evolve.domain.PersonContactData;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Component
@FxmlView("contact-details.fxml")
@Slf4j
@RequiredArgsConstructor
public class PersonContactDataController extends EditableGuiElement implements Initializable {
    private final StageManager stageManager;
    private final ObservableList<PersonContactEntry> list = FXCollections.observableArrayList();

    @FXML TableView<PersonContactEntry> personContactDataTable;
    @FXML TableColumn<PersonContactEntry, Image> contactTypeColumn;
    @FXML TableColumn<PersonContactEntry, String> phoneOrEmailColumn;
    @FXML TableColumn<PersonContactEntry, String> commentColumn;

    @FXML MenuItem addPhoneNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contactTypeColumn.setCellValueFactory(new PropertyValueFactory<>("imageType"));
        contactTypeColumn.setCellFactory(item -> new ContactIconTableCell());
        phoneOrEmailColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        setPersonContactData(Collections.emptyList()); // it's needed, without this initial call the table won't be populated with real data

        addPhoneNumber.disableProperty().bind(disabledProperty);
        addPhoneNumber.setOnAction(this::addContactDetails);

        personContactDataTable.editableProperty().bind(disabledProperty.not());
        personContactDataTable.setRowFactory(tableView -> {
            final TableRow<PersonContactEntry> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem copyNumber = new MenuItem("Kopiuj");
            copyNumber.setOnAction(event -> {
                final String text = trimToEmpty(row.getItem().getData());

                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(text);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            });

            final MenuItem editMenuItem = new MenuItem("Edytuj");
            editMenuItem.setOnAction(event -> editContactDetails(tableView, row));
            editMenuItem.disableProperty().bind(disabledProperty);

            row.setOnMouseClicked(event -> {
                if (disabledProperty.get()) {
                    return;
                }

                if (event.getClickCount() == 2) {
                    if (row.isEmpty()) {
                        addContactDetails(event);
                    } else {
                        editContactDetails(tableView, row);
                    }
                }
            });

            final MenuItem removeMenuItem = new MenuItem("Usuń");
            removeMenuItem.setOnAction(event -> {
                list.remove(row.getItem());
                tableView.refresh();
            });
            removeMenuItem.disableProperty().bind(disabledProperty);

            contextMenu.getItems().addAll(copyNumber, editMenuItem, removeMenuItem);

            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));
            return row;
        });

    }

    public List<PersonContactData> getNumbers() {
        return list.stream()
                .map(PersonContactEntry::getPersonContactData)
                .collect(Collectors.toList());
    }

    public void setPersonContactData(List<PersonContactData> contactData) {
        list.clear();
        emptyIfNull(contactData)
                .forEach(dataItem -> list.add(new PersonContactEntry(dataItem)));

        personContactDataTable.setItems(list);
    }


    private void addContactDetails(Event actionEvent) {
        PersonContactDetailsDialog.addNewPhoneOrEmail().showDialog(stageManager.getWindow())
                .ifPresent(contactData -> {
                    list.add(new PersonContactEntry(contactData));
                    personContactDataTable.refresh();
                });
    }

    private void editContactDetails(TableView<PersonContactEntry> tableView, TableRow<PersonContactEntry> row) {
        new PersonContactDetailsDialog(row.getItem().getPersonContactData()).showDialog(stageManager.getWindow())
                .ifPresent(contactData -> {
                    row.getItem().setPersonContactData(contactData);
                    tableView.refresh();
                });
    }


    static class ContactIconTableCell extends TableCell<PersonContactEntry, Image> {
        private final ImageView imageView = new ImageView();

        public ContactIconTableCell() {
            imageView.setFitHeight(12);
            imageView.setFitWidth(12);
        }

        @Override
        protected void updateItem(Image image, boolean empty) {
            super.updateItem(image, empty);

            if (image == null || empty) {
                imageView.setImage(null);
                setGraphic(null);
            } else {
                imageView.setImage(image);
                setGraphic(imageView);
            }
        }
    }

}
