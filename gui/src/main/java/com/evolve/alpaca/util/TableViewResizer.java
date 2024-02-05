package com.evolve.alpaca.util;

import javafx.beans.binding.Bindings;
import javafx.scene.control.*;

public class TableViewResizer {


    /**
     * this sucks because on other machines it doesn't have to be 25 pixels :( <br/>
     *  <a href="https://stackoverflow.com/questions/26298337/tableview-adjust-number-of-visible-rows">explore other solutions</a>
     *
     */
    public static <T> void resizeTable(TableView<T> tableView) {
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(
                Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(2*25));
    }
}
