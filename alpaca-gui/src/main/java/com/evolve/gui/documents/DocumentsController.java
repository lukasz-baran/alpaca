package com.evolve.gui.documents;

import com.evolve.alpaca.gui.viewer.ImageViewWindowController;
import com.evolve.content.ContentFile;
import com.evolve.content.ContentStoreService;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final ContentStoreService contentStoreService;

    private final FxControllerAndView<ImageViewWindowController, BorderPane> mainWindowController;

    @FXML TableView<DocumentEntry> documentsTable;
    @FXML TableColumn<DocumentEntry, Long> idColumn;
    @FXML TableColumn<DocumentEntry, String> fileNameColumn;
    @FXML TableColumn<DocumentEntry, String> dateAddedColumn;
    @FXML TableColumn<DocumentEntry, String> summaryColumn;
    @FXML Button btnAddDocument;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        documentsTable.setRowFactory(tv -> {
            final TableRow<DocumentEntry> row = new DocumentsRow();

            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem openDocumentMenuItem = new MenuItem("Otwórz");
            openDocumentMenuItem.setOnAction(event -> openDocument(row.getItem()));
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    openDocument(row.getItem());
                }
            });

            final MenuItem saveDocumentMenuItem = new MenuItem("Zapisz");
            saveDocumentMenuItem.setOnAction(event -> saveDocumentToFile(row.getItem()));

            final MenuItem editDocumentMenuItem = new MenuItem("Edytuj");
            editDocumentMenuItem.setOnAction(event -> editDocument(row));

            final MenuItem removeDocumentMenuItem = new MenuItem("Usuń");
            removeDocumentMenuItem.setOnAction(event -> removeDocument(row.getItem()));

            contextMenu.getItems().addAll(openDocumentMenuItem,
                    saveDocumentMenuItem,
                    editDocumentMenuItem,
                    removeDocumentMenuItem);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                    .otherwise(contextMenu));
            return row ;
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));

        dateAddedColumn.setCellValueFactory(new PropertyValueFactory<>("dateAdded"));
        dateAddedColumn.setCellValueFactory(foo -> new SimpleStringProperty(
                Optional.ofNullable(foo.getValue().getDateAdded())
                        .map(value -> value.format(DATE_TIME_FORMATTER))
                        .orElse(StringUtils.EMPTY)));

        summaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newUser) -> {
                    loadDocuments(newUser);
                });
    }

    private void editDocument(TableRow<DocumentEntry> row) {
        new DocumentEditDialog(row.getItem()).showDialog(stageManager.getWindow())
                .ifPresent(documentEntry -> {
                    log.info("edited details of document {}", documentEntry);
                    contentStoreService.changeFileDetails(documentEntry.getId(), documentEntry.getFileName(),
                            documentEntry.getSummary());
                    row.getItem().setFileName(documentEntry.getFileName());
                    row.getItem().setSummary(documentEntry.getSummary());
                    documentsTable.refresh();
                });
    }

    private void removeDocument(DocumentEntry item) {
        log.info("User wants to remove document: {}", item);

        final boolean delete = stageManager.displayConfirmation("Czy na pewno chcesz usunąć dokument " +
                item.getFileName() + "?\nOperacja jest nieodwracalna!");

        if (delete) {
            contentStoreService.deleteContent(item.getId());

            loadDocuments(personListModel.getCurrentPersonProperty().getValue());
            log.info("File deleted: {}", item);
        } else {
            log.info("User canceled document {} removal", item);
        }
    }

    private void saveDocumentToFile(DocumentEntry item) {
        log.info("User wants to save file: {}", item);
        final Long imageFileId = item.getId();
        final InputStream inputStream = contentStoreService.getContent(imageFileId);

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
            FileUtils.copyInputStreamToFile(inputStream, fileToSave);
        } catch (IOException e) {
            log.error("Error while saving file", e);
            stageManager.displayError("Błąd podczas zapisywania pliku");
            return;
        }

        log.info("File saved successfully: {}", fileToSave);
    }

    @SneakyThrows
    private void openDocument(DocumentEntry documentEntry) {
        if (documentEntry.isImageFile()) {
            mainWindowController.getController().openImage(documentEntry);
        } else  {
            final InputStream inputStream = contentStoreService.getContent(documentEntry.getId());

            final String prefix = FilenameUtils.getBaseName(documentEntry.getFileName());
            final String suffix = "." + FilenameUtils.getExtension(documentEntry.getFileName());
            final File tempFile = File.createTempFile(prefix, suffix);
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(inputStream, out);
            }

            Desktop.getDesktop().open(tempFile);
        }
    }

    public void addDocumentButtonClicked(ActionEvent actionEvent) {
        if (personListModel.getCurrentPersonProperty().getValue() == null) {
            stageManager.displayWarning("Nie wybrano osoby");
            return;
        }

        final DocumentDetailsDialog documentDetailsDialog = new DocumentDetailsDialog(stageManager);

        documentDetailsDialog.showDialog(stageManager.getWindow())
            .ifPresent(filePathAndDescription -> {
                final PersonModel currentPerson = personListModel.getCurrentPersonProperty().get();
                final File file = filePathAndDescription.getFile();
                final String description = filePathAndDescription.getDescription();

                final ContentFile contentFile = contentStoreService.setContent(file, currentPerson.getId(), description);
                log.info("Content file added: {}", contentFile);

                final DocumentEntry documentEntry = DocumentEntry.of(contentFile);
                documentsList.add(documentEntry);
        });
    }

    void loadDocuments(PersonModel personModel) {
        if (personModel != null) {
            String personId = personModel.getId();

            documentsList.clear();
            documentsList.setAll(contentStoreService.findFiles(personId).stream().map(DocumentEntry::of).toList());
            documentsTable.setItems(documentsList);
        }
    }

    /**
     * This extra class is needed to display tooltips on document entries
     */
    public static class DocumentsRow extends TableRow<DocumentEntry> {
        private final Tooltip tooltip = new Tooltip();

        @Override
        public void updateItem(DocumentEntry documentEntry, boolean empty) {
            super.updateItem(documentEntry, empty);
            if (documentEntry == null) {
                setTooltip(null);
            } else {
                tooltip.setText("mime type: " + documentEntry.getMimeType());
                setTooltip(tooltip);
            }
        }
    }


}
