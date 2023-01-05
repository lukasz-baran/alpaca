package com.evolve.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Getter
@Component
@FxmlView("main-stage.fxml")
public class MainController {

    @FXML
    private Label boardLabel;

    @FXML
    private GridPane boardPanel;

    private Node getNodeFromGridPane(int col, int row) {
        ObservableList<Node> children = boardPanel.getChildren().filtered(i -> i instanceof ImageView);
        for (Node node : children) {
            if ((GridPane.getColumnIndex(node) == col) && (GridPane.getRowIndex(node) == row)) {
                return node;
            }
        }
        return null;
    }

    private void fireNode(Node node) {
        if(node!=null) {
            ImageView result = (ImageView)node;
            if(result.isVisible()) {
                result.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, false, false, null));
            }
        }
    }

    public void initializeBoard() {
        boardPanel.getParent().setStyle("-fx-background-color: #333333");
        ((Pane)boardPanel.getParent()).setPrefSize((double)40*20, (double)40*20);
    }

}
