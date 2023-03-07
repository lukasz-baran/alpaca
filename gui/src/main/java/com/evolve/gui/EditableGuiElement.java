package com.evolve.gui;

public abstract class EditableGuiElement {

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

    public abstract void setEditable(boolean editable);


    enum CurrentState {
        READONLY,
        EDITABLE,
        EDITED
    }

}
