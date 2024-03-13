package com.evolve.gui.person.lastNames;

import com.evolve.domain.Person;
import com.evolve.gui.EditableGuiElement;
import com.evolve.gui.StageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.collections4.CollectionUtils;
import org.controlsfx.control.PopOver;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FxmlView("last-names.fxml")
@Slf4j
@RequiredArgsConstructor
public class LastNamesController extends EditableGuiElement implements Initializable {
    private static final String HAMBURGER_MENU = "\u2630";

    private final StringProperty lastNameProperty = new SimpleStringProperty();
    private final ObservableList<String> previousLastNames = FXCollections.observableArrayList();
    private final StringProperty buttonStyleProperty = new SimpleStringProperty("-fx-text-fill: gray;");
    private final PopOver lastNamesPopover = createPopover();

    @FXML HBox lastNamesHBox;
    @FXML TextField lastNameTextField;
    @FXML Button showPreviousButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lastNameTextField.textProperty().bindBidirectional(lastNameProperty);
        lastNameTextField.editableProperty().bind(disabledProperty.not());

        showPreviousButton.setTooltip(StageManager.newTooltip("Poprzednie nazwiska"));
        showPreviousButton.setOnAction(e -> {
            showLastNames((Node) e.getSource());
        });
        showPreviousButton.setText(HAMBURGER_MENU);
        showPreviousButton.styleProperty().bind(buttonStyleProperty);
    }

    public void setPerson(Person person) {
        lastNameProperty.setValue(person.getLastName());
        previousLastNames.setAll(person.getPreviousLastNames());
        if (CollectionUtils.isNotEmpty(person.getPreviousLastNames())) {
            buttonStyleProperty.setValue("-fx-text-fill: black;");
        } else {
            buttonStyleProperty.setValue("-fx-text-fill: gray;");
        }
    }

    public String getLastName() {
        return lastNameProperty.getValue();
    }

    public List<String> getPreviousNames() {
        return previousLastNames;
    }

    void showLastNames(Node parentNode) {
        if (lastNamesPopover.isShowing()) {
            lastNamesPopover.hide();
        } else {
            lastNamesPopover.show(parentNode);
        }
    }

    private PopOver createPopover() {
        final VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        final ListView<String> listView = new ListView<>(previousLastNames);
        listView.editableProperty().bind(disabledProperty.not());

        listView.setCellFactory(lv -> {
            final TextFieldListCell<String> cell = new TextFieldListCell<>(new DefaultStringConverter());
            cell.editableProperty().bind(disabledProperty.not());

            final ContextMenu contextMenu = new ContextMenu();

            final MenuItem editItem = new MenuItem("Edytuj");
            editItem.disableProperty().bind(disabledProperty);
            editItem.setOnAction(event -> {
                listView.edit(cell.getIndex());
            });

            final MenuItem deleteItem = new MenuItem("Usuń");
            deleteItem.disableProperty().bind(disabledProperty);
            deleteItem.setOnAction(event -> listView.getItems().remove(cell.getItem()));
            contextMenu.getItems().addAll(editItem, deleteItem);

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
        listView.setOnEditCommit(e -> listView.getItems().set(e.getIndex(), e.getNewValue()));

        final ContextMenu newItemContextMenu = new ContextMenu();
        final MenuItem newItem = new MenuItem("Dodaj");
        newItem.disableProperty().bind(disabledProperty);
        newItem.setOnAction(event -> {
            final int index = listView.getItems().size();
            listView.getItems().add(index, "nazwisko");
            listView.edit(index);
        });
        newItemContextMenu.getItems().add(newItem);
        listView.setContextMenu(newItemContextMenu);

        content.getChildren().addAll(new Label("Poprzednie nazwiska:"), listView);
        final PopOver popOver = new PopOver(content);
        popOver.setDetachable(false);
        popOver.setAutoFix(true);
        return popOver;
    }

}
