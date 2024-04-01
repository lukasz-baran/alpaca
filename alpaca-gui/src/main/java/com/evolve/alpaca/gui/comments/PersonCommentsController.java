package com.evolve.alpaca.gui.comments;

import com.evolve.alpaca.comment.Comment;
import com.evolve.alpaca.comment.services.PersonCommentService;
import com.evolve.gui.StageManager;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@FxmlView("person-comments.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonCommentsController implements Initializable {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
    private static final String REMOVE_COMMENT_CONFIRMATION = "Czy na pewno chcesz usunąć komentarz?\nOperacja jest nieodwracalna!";

    @Getter
    private final ObservableList<PersonCommentEntry> commentsList = FXCollections.observableArrayList();

    private final PersonCommentService personCommentService;
    private final PersonListModel personListModel;
    private final StageManager stageManager;

    @FXML
    TableView<PersonCommentEntry> commentsTable;
    @FXML
    Button btnAddComment;
    @FXML
    TableColumn<PersonCommentEntry, String> dateAddedColumn;
    @FXML
    TableColumn<PersonCommentEntry, String> contentColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateAddedColumn.setCellValueFactory(param -> {
            final PersonCommentEntry commentEntry = param.getValue();
            return new SimpleStringProperty(Optional.ofNullable(commentEntry.getDateAdded())
                    .map(value -> value.format(DATE_TIME_FORMATTER))
                    .orElse(StringUtils.EMPTY));
        });

        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        commentsTable.setRowFactory(tv -> {
            final TableRow<PersonCommentEntry> row = new TableRow<>();
            addContextMenu(row);
            return row ;
        });
        commentsTable.setItems(new SortedList<>(commentsList, Comparator.comparing(PersonCommentEntry::getDateAdded).reversed()));

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newPerson) -> loadComments(newPerson));
    }

    @FXML
    void addCommentButtonClicked(ActionEvent actionEvent) {
        if (personListModel.getCurrentPersonProperty().getValue() == null) {
            stageManager.displayWarning("Nie można dodawać komentarzy. Nie wyobrano osoby.");
            return;
        }

        final String personId = personListModel.getCurrentPersonProperty().getValue().getId();

        new PersonCommentDialog(null)
                .showDialog(stageManager.getWindow())
                .ifPresent(newEntry -> {
                    final Comment newComment = personCommentService.addNewComment(personId, newEntry.getContent());
                    commentsList.add(PersonCommentEntry.of(newComment));
                    commentsTable.refresh();
                });
    }

    private ContextMenu addContextMenu(TableRow<PersonCommentEntry> row) {
        final ContextMenu contextMenu = new ContextMenu();

        final MenuItem editDocumentMenuItem = new MenuItem("Edytuj");
        editDocumentMenuItem.setOnAction(event -> editComment(row));

        final MenuItem removeDocumentMenuItem = new MenuItem("Usuń");
        removeDocumentMenuItem.setOnAction(event -> removeComment(row));

        contextMenu.getItems().addAll(editDocumentMenuItem, removeDocumentMenuItem);
        row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null)
                .otherwise(contextMenu));
        return contextMenu;
    }

    private void removeComment(TableRow<PersonCommentEntry> row) {
        if (stageManager.displayConfirmation(REMOVE_COMMENT_CONFIRMATION)) {
            final PersonCommentEntry entry = row.getItem();
            personCommentService.removeComment(entry.getId());
            commentsList.remove(entry);
            commentsTable.refresh();
        }
    }

    private void editComment(TableRow<PersonCommentEntry> row) {
        new PersonCommentDialog(row.getItem()).showDialog(stageManager.getWindow())
                .flatMap(entry -> personCommentService.editComment(entry.getId(), entry.getContent()))
                .ifPresent(newEntry -> commentsTable.refresh());
    }

    void loadComments(PersonModel personModel) {
        if (personModel != null) {
            commentsList.clear();
            commentsList.setAll(personCommentService.findPersonComments(personModel.getId())
                    .stream()
                    .map(PersonCommentEntry::of)
                    .collect(Collectors.toList())
            );
            commentsTable.refresh();
        }
    }

}