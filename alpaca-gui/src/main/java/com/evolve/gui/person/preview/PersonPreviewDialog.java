package com.evolve.gui.person.preview;

import com.evolve.domain.Person;
import com.evolve.gui.StageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Getter
@Component
@FxmlView("person-preview.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonPreviewDialog implements Initializable {
    public static final String TITLE = "Podgląd";


    private final StageManager stageManager;
    private Stage stage;

    @FXML TreeView<PersonTreeItem> previewTreeView;
    @FXML TreeView<PersonTreeItem> previewTreeView1;

    @FXML HBox personPreviewDialog;
    @FXML CheckBox hideEmptyCheckBox;
    @FXML Button btnClose;

    private final ObjectProperty<Person> personBeforeObjectProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Person> personAfterObjectProperty = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(personPreviewDialog));
        stage.setTitle(TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);

        attachCellFactory(previewTreeView);
        attachCellFactory(previewTreeView1);
    }

    void attachCellFactory(TreeView<PersonTreeItem> previewTreeView) {
        previewTreeView.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<PersonTreeItem> call(TreeView<PersonTreeItem> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(PersonTreeItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText("");
                            setStyle(null);
                        } else {
                            setText(item.toString());

                            final PersonTreeItemDifference difference = item.getDifference();
                            if (Objects.requireNonNull(difference) == PersonTreeItemDifference.CHANGED) {
                                setStyle("-fx-background-color: yellow;");
                            }
                        }
                    }
                };
            }
        });
    }

    @SneakyThrows
    public void open(String title, Person person, Person anotherPerson) {
        stage.setTitle(TITLE + ": " + title);
        stage.show();

        personBeforeObjectProperty.setValue(person);
        personAfterObjectProperty.setValue(anotherPerson);

        showTrees(false);
    }

    void showTrees(boolean hideEmpty) {
        final PersonPreview personPreview = new PersonPreviewTreeBuilder(!hideEmpty, personBeforeObjectProperty.get()).of();
        final PersonPreview otherPersonPreview = new PersonPreviewTreeBuilder(!hideEmpty, personAfterObjectProperty.get()).of();

        personPreview.compareWith(otherPersonPreview);
        otherPersonPreview.compareWith(personPreview);

        previewTreeView.setRoot(personPreview.root());
        previewTreeView1.setRoot(otherPersonPreview.root());
    }

    @FXML
    void onClose(ActionEvent actionEvent) {
        stage.close();
    }

    @FXML
    void hideEmptyToggle(ActionEvent actionEvent) {
        showTrees(hideEmptyCheckBox.isSelected());
    }
}
