package com.evolve.alpaca.gui.comments;

import com.evolve.alpaca.comment.services.PersonCommentService;
import com.evolve.gui.person.list.PersonListModel;
import com.evolve.gui.person.list.PersonModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@FxmlView("person-comments.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonCommentsController implements Initializable {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Getter
    private final ObservableList<PersonCommentEntry> commentsList = FXCollections.observableArrayList();
    private final PersonCommentService personCommentService;
    private final PersonListModel personListModel;

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

        personListModel.getCurrentPersonProperty().addListener(
                (ObservableValue<? extends PersonModel> obs, PersonModel oldUser, PersonModel newPerson) -> loadComments(newPerson));
    }

    @FXML
    void addCommentButtonClicked(ActionEvent actionEvent) {
    }

    void loadComments(PersonModel personModel) {
        if (personModel != null) {
            commentsList.clear();
            commentsList.setAll(personCommentService.findPersonComments(personModel.getId())
                    .stream()
                    .map(PersonCommentEntry::of)
                    .collect(Collectors.toList())
            );
        }
    }


}