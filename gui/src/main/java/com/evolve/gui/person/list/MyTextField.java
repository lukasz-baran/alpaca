package com.evolve.gui.person.list;

import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.TextFields;


/**
 * This wrapper is needed because we want to use ControlFx input text field
 */
public class MyTextField extends TextField {

    public static TextField create() {
        return TextFields.createClearableTextField();
    }
}