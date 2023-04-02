package com.evolve.gui;

import javafx.scene.control.Dialog;
import javafx.stage.Window;

import java.util.Optional;

public abstract class DialogWindow<ENTITY> {

    protected final Dialog<ENTITY> dialog = new Dialog<>();

    public abstract Optional<ENTITY> showDialog(Window window);

}
