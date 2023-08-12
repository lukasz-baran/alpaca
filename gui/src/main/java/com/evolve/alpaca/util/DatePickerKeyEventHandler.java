package com.evolve.alpaca.util;

import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class DatePickerKeyEventHandler implements EventHandler<KeyEvent> {
    private final LocalDateStringConverter converter;
    private final DatePicker datePicker;

    @Override
    public void handle(KeyEvent event) {
        final String textValue = datePicker.getEditor().getText();
        datePicker.setStyle("");
        converter.fromString(textValue);
        if (StringUtils.isNotBlank(textValue) && converter.hasParseError()) {
            datePicker.setStyle("-fx-border-color: red");
        }

    }
}

