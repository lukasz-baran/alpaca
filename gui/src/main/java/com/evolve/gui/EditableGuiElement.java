package com.evolve.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class EditableGuiElement {

    protected final BooleanProperty disabledProperty = new SimpleBooleanProperty(true);

    protected CurrentState currentState;


    CurrentState getCurrentState() {
        return CurrentState.EDITABLE;
    }

    public void setEditable(boolean editable) {
        disabledProperty.set(!editable);
    }

    enum CurrentState {
        READONLY,
        EDITABLE,
        EDITED
    }

}
