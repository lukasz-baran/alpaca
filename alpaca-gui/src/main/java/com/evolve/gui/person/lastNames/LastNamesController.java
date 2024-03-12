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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.collections4.CollectionUtils;
import org.controlsfx.control.PopOver;
import org.springframework.stereotype.Component;

import java.net.URL;
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
        listView.setTooltip(StageManager.newTooltip("UWAGA! Edycja listy poprzednich nazwisk nie została jeszcze zaimplementowana!"));

        content.getChildren().addAll(new Label("Poprzednie nazwiska:"), listView);
        final PopOver popOver = new PopOver(content);
        popOver.setDetachable(false);
        popOver.setAutoFix(true);
        return popOver;
    }

}
