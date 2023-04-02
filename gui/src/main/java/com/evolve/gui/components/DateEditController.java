package com.evolve.gui.components;

import com.evolve.gui.EditableGuiElement;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bindings.bindBidirectional(datePicker.valueProperty(), this.dateObjectProperty);
        datePicker.setEditable(false);
    }

    public void setDate(LocalDate date) {
        this.dateObjectProperty.setValue(date);
        Bindings.bindBidirectional(datePicker.valueProperty(), this.dateObjectProperty);
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
        datePicker.setEditable(!editable);
    }

    public LocalDate getDate() {
        return this.dateObjectProperty.getValue();
    }
}
