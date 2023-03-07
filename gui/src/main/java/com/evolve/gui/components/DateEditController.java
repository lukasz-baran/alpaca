package com.evolve.gui.components;

import com.evolve.gui.EditableGuiElement;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Component
@FxmlView("date-edit.fxml")
@Slf4j
@RequiredArgsConstructor
public class DateEditController extends EditableGuiElement implements Initializable {
    private final ObjectProperty<LocalDate> dateObjectProperty = new SimpleObjectProperty<>();

    @FXML DatePicker datePicker;
    private final EventHandler<MouseEvent> disableDatePopup = (MouseEvent e) -> {

        System.out.println("DATE PICKER: " + datePicker.isEditable());

        if (!datePicker.isEditable()) {
            datePicker.hide();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("DateEditController - START");
        Bindings.bindBidirectional(datePicker.valueProperty(), this.dateObjectProperty);
        datePicker.setOnMouseClicked(this.disableDatePopup);
        //datePicker.setDisable(true);
    }

    public void setDate(LocalDate date) {
        this.dateObjectProperty.setValue(date);
        Bindings.bindBidirectional(datePicker.valueProperty(), this.dateObjectProperty);
        datePicker.setOnMouseClicked(this.disableDatePopup);
    }

    @Override
    protected boolean isEditable() {
        return true;
    }

    @Override
    public boolean startEditing() {
        return false;
    }

    @Override
    public void setEditable(boolean editable) {
        //datePicker.setDisable(!editable);
        datePicker.setOnMouseClicked(editable ? null : this.disableDatePopup);
    }

    public LocalDate getDate() {
        return this.dateObjectProperty.getValue();
    }
}
