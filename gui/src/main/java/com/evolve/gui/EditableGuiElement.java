package com.evolve.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class EditableGuiElement {

    protected final BooleanProperty disabledProperty = new SimpleBooleanProperty(true);

    protected CurrentState currentState;

    /**
     * Some elements in the GUI can be edited and some not (eg. Person ID)
     */
    protected abstract boolean isEditable();

    CurrentState getCurrentState() {
        return CurrentState.EDITABLE;
    }



    /**
     *
     * @return {@code true} if edit was possible
     */
    public abstract boolean startEditing();

    public void setEditable(boolean editable) {
        disabledProperty.set(!editable);
    }


    enum CurrentState {
        READONLY,
        EDITABLE,
        EDITED
    }

}
