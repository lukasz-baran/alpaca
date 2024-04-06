package com.evolve.gui.documents;

import com.evolve.alpaca.document.DocumentCategory;
import com.evolve.alpaca.document.DocumentEntry;
import com.evolve.alpaca.document.UpdateDocumentCommand;
import com.evolve.alpaca.document.services.DocumentContentStorageService;
import com.evolve.alpaca.gui.viewer.ImageViewWindowController;
import com.evolve.gui.StageManager;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@FxmlView("documents.fxml")
@RequiredArgsConstructor
@Slf4j
public class DocumentsController implements Initializable {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Getter
    private final ObservableList<DocumentEntry> documentsList = FXCollections.observableArrayList();

    private final StageManager stageManager;
    private final PersonListModel personListModel;
    private final DocumentContentStorageService documentContentStorageService;

    private final FxControllerAndView<ImageViewWindowController, BorderPane> mainWindowController;

    @FXML TreeTableView<DocumentEntry> documentsTable;
    @FXML TreeTableColumn<DocumentEntry, String> tagColumn;
    @FXML TreeTableColumn<DocumentEntry, String> fileNameColumn;
    @FXML TreeTableColumn<DocumentEntry, String> dateAddedColumn;
    @FXML TreeTableColumn<DocumentEntry, String> summaryColumn;
    @FXML Button btnAddDocument;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        documentsTable.setRowFactory(tv -> {
            final TreeTableRow<DocumentEntry> row = new DocumentsRow();
            addContextMenu(row);
            return row ;
        });

        tagColumn.setCellValueFactory(param -> {
            final DocumentEntry documentEntry = param.getValue().getValue();
            if (documentEntry.getEntryType() == DocumentEntry.EntryType.CATEGORY) {
                return new SimpleStringProperty(Optional.ofNullable(documentEntry.getCategory())
                        .map(DocumentCategory::getCategory).orElse(StringUtils.EMPTY));
            }
            return new SimpleStringProperty(StringUtils.EMPTY);
        });

        fileNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("fileName"));
        dateAddedColumn.setCellValueFactory(param -> {
            final TreeItem<DocumentEntry> treeItem = param.getValue();
            final DocumentEntry emp = treeItem.getValue();

            return new SimpleStringProperty(Optional.ofNullable(emp.getDateAdded())
                    .map(value -> value.format(DATE_TIME_FORMATTER))
                    .orElse(StringUtils.EMPTY));
        });

        summaryColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("summary"));

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    loadDocuments(newUser);
                });
    }

    private ContextMenu addContextMenu(TreeTableRow<DocumentEntry> row) {
        final ContextMenu contextMenu = new ContextMenu();

        final MenuItem newDocumentMenuItem = new MenuItem("Nowy");
        newDocumentMenuItem.setOnAction(event -> addDocumentMenuItemClicked(row.getItem()));

        final MenuItem openDocumentMenuItem = new MenuItem("Otwórz");
        openDocumentMenuItem.setOnAction(event -> openDocument(row.getItem()));
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !row.isEmpty() &&
                    row.getItem() != null &&
                    row.getItem().getEntryType() != DocumentEntry.EntryType.CATEGORY) {
                openDocument(row.getItem());
            }
        });

        final MenuItem saveDocumentMenuItem = new MenuItem("Zapisz");
        saveDocumentMenuItem.setOnAction(event -> saveDocumentToFile(row.getItem()));

        final MenuItem editDocumentMenuItem = new MenuItem("Edytuj");
        editDocumentMenuItem.setOnAction(event -> editDocument(row));

        final MenuItem removeDocumentMenuItem = new MenuItem("Usuń");
        removeDocumentMenuItem.setOnAction(event -> removeDocument(row.getItem()));

        contextMenu.getItems().addAll(newDocumentMenuItem, openDocumentMenuItem, saveDocumentMenuItem,
                editDocumentMenuItem, removeDocumentMenuItem);
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                .otherwise(contextMenu));
        return contextMenu;
    }


    private void editDocument(TreeTableRow<DocumentEntry> row) {
        if (isCategory(row.getItem())) {
            return;
        }

        new DocumentEditDialog(row.getItem()).showDialog(stageManager.getWindow())
                .ifPresent(documentEntry -> {
                    log.info("edited details of document {}", documentEntry);

                    documentContentStorageService.updateContent(
                            new UpdateDocumentCommand(documentEntry.getId(), documentEntry.getFileName(),
                                    documentEntry.getSummary(), documentEntry.getCategory()));

                    loadDocuments(personListModel.getCurrentPersonProperty().get());
                });
    }

    private void removeDocument(DocumentEntry item) {
        log.info("User wants to remove document: {}", item);
        if (isCategory(item)) {
            return;
        }

        final boolean delete = stageManager.displayConfirmation("Czy na pewno chcesz usunąć dokument " +
                item.getFileName() + "?\nOperacja jest nieodwracalna!");

        if (delete) {
            documentContentStorageService.removeContent(item.getId());

            loadDocuments(personListModel.getCurrentPersonProperty().getValue());
            log.info("File deleted: {}", item);
        } else {
            log.info("User canceled document {} removal", item);
        }
    }

    private void saveDocumentToFile(DocumentEntry item) {
        if (isCategory(item)) {
            return;
        }

        log.info("User wants to save file: {}", item);

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz plik");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        fileChooser.setInitialFileName(item.getFileName());

        final File fileToSave = fileChooser.showSaveDialog(stageManager.getPrimaryStage());
        if (fileToSave == null) {
            log.info("No file selected - user must have canceled file selection");
            return;
        }

        log.info("Selected file: {}", fileToSave);
        try {
            documentContentStorageService.saveToFile(item.getId(), fileToSave);
        } catch (IOException e) {
            log.error("Error while saving file", e);
            stageManager.displayError("Błąd podczas zapisywania pliku ID: " + item.getId());
            return;
        }

        log.info("File saved successfully: {}", fileToSave);
    }

    /**
     * Prevents from performing action on category items on the list.
     */
    private boolean isCategory(DocumentEntry documentEntry) {
        if (documentEntry.getEntryType() == DocumentEntry.EntryType.CATEGORY) {
            stageManager.displayInformation("Akcja niedozwolona w przypadku kategorii");
            return true;
        }
        return false;
    }

    @SneakyThrows
    private void openDocument(DocumentEntry documentEntry) {
        if (isCategory(documentEntry)) {
            return;
        }

        if (documentEntry.isImageFile()) {
            mainWindowController.getController().openImage(documentEntry);
        } else  {
            final File tempFile = documentContentStorageService.saveToTempFile(documentEntry);

            Desktop.getDesktop().open(tempFile);
        }
    }

    void addDocumentMenuItemClicked(DocumentEntry entry) {
        if (entry.getEntryType() == DocumentEntry.EntryType.CATEGORY) {
            addDocument(entry.getCategory());
        } else {
            addDocument(null);
        }
    }

    @FXML
    void addDocumentButtonClicked(ActionEvent actionEvent) {
        if (personListModel.getCurrentPersonProperty().getValue() == null) {
            stageManager.displayWarning("Nie wybrano osoby");
            return;
        }

        addDocument(null);
    }

    private void addDocument(DocumentCategory documentCategory) {
        log.info("New document with category {}", documentCategory);

        final DocumentDetailsDialog documentDetailsDialog = new DocumentDetailsDialog(stageManager, documentCategory);
        documentDetailsDialog.showDialog(stageManager.getWindow())
                .ifPresent(filePathAndDescription -> {
                    final PersonModel currentPerson = personListModel.getCurrentPersonProperty().get();
                    documentContentStorageService.storeContent(
                            currentPerson.getId(), filePathAndDescription);

                    loadDocuments(currentPerson);
                });
    }

    void loadDocuments(PersonModel personModel) {
        if (personModel != null) {
            documentsList.clear();
            documentsList.setAll(documentContentStorageService.findPersonsDocuments(personModel.getId()));

            final DocumentEntry rootEntry = DocumentEntry.category(null);
            final TreeItem<DocumentEntry> hiddenRoot = new TreeItem<>(rootEntry);
            hiddenRoot.setExpanded(true);
            documentsTable.setRoot(hiddenRoot);
            documentsTable.setShowRoot(false);

            for (DocumentCategory category : DocumentCategory.values()) {
                final TreeItem<DocumentEntry> newTreeItem = new TreeItem<>(DocumentEntry.category(category));
                newTreeItem.setExpanded(true);

                newTreeItem.getChildren().addAll(documentsList.stream()
                        .filter(documentEntry -> documentEntry.matchesCategory(category))
                        .map(TreeItem::new).toList());
                hiddenRoot.getChildren().add(newTreeItem);
            }

        }
    }

    /**
     * This extra class is needed to display tooltips on document entries
     */
    public static class DocumentsRow extends TreeTableRow<DocumentEntry> {

        @Override
        public void updateItem(DocumentEntry documentEntry, boolean empty) {
            super.updateItem(documentEntry, empty);
            setTooltip(null);
            if (documentEntry != null && documentEntry.getEntryType() == DocumentEntry.EntryType.DOCUMENT) {
                final String tooltipText = String.format(
                        "content ID: %d\nmime type: %s\nlength: %d\ncategory: %s",
                        documentEntry.getId(), documentEntry.getMimeType(), documentEntry.getLength(),
                        documentEntry.getCategory());
                setTooltip(StageManager.newTooltip(tooltipText));
            }
        }
    }


}
