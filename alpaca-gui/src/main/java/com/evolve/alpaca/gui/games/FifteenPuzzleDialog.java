package com.evolve.alpaca.gui.games;

import com.evolve.gui.StageManager;
import com.google.common.collect.Lists;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Component
@FxmlView("fifteen-puzzle.fxml")
@Slf4j
public class FifteenPuzzleDialog implements Initializable {
    private static final String EMPTY = "";
    public static final Image ALPACA_ICON = new Image("alpaca.png", 50, 50, true, true);

    private final StageManager stageManager;
    private final List<String> solvedList;

    private Stage stage;

    @FXML Button btnRestart;
    @FXML HBox buttonBar;
    @FXML Text timerText;

    @FXML VBox fifteenPuzzleVBox;
    @FXML GridPane gameBoardGrid;

    @FXML Button imagePane00;
    @FXML Button imagePane01;
    @FXML Button imagePane02;
    @FXML Button imagePane03;

    @FXML Button imagePane10;
    @FXML Button imagePane11;
    @FXML Button imagePane12;
    @FXML Button imagePane13;

    @FXML Button imagePane20;
    @FXML Button imagePane21;
    @FXML Button imagePane22;
    @FXML Button imagePane23;

    @FXML Button imagePane30;
    @FXML Button imagePane31;
    @FXML Button imagePane32;
    @FXML Button imagePane33;


    private List<Button> buttonList;

    private int second;
    private Timeline stopWatchTimeline;

    public FifteenPuzzleDialog(StageManager stageManager) {
        this.stageManager = stageManager;
        this.solvedList = new ArrayList<>();
        IntStream.rangeClosed(1, 15).forEach(i -> solvedList.add(String.valueOf(i)));
        solvedList.add(EMPTY);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(fifteenPuzzleVBox));
        stage.setTitle("Czasoumilacz");

        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(false);

        this.buttonList = Arrays.asList(
                imagePane00, imagePane01, imagePane02, imagePane03,
                imagePane10, imagePane11, imagePane12, imagePane13,
                imagePane20, imagePane21, imagePane22, imagePane23,
                imagePane30, imagePane31, imagePane32, imagePane33
        );

        this.second = 0;
        this.timerText.setText(getFormattedTime());

        this.stopWatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent event) -> {
            second++;
            this.timerText.setText(getFormattedTime());
        }));
        stopWatchTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private String getFormattedTime() {
        return String.format("%02d:%02d:%02d", second / 3600, (second % 3600) / 60, second % 60);
    }

    @FXML
    void restartGame(ActionEvent actionEvent) {
        final List<String> numbers = Lists.newArrayList(this.solvedList);
        Collections.shuffle(numbers);
        final AtomicInteger counter = new AtomicInteger(0);
        buttonList.forEach(button -> {
            final int index = counter.getAndIncrement();
            final String number = numbers.get(index);
            if (EMPTY.equals(number)) {
                disableButton(button);
            } else {
                enableButton(button, number);
            }
        });

        this.second = 0;
        this.stopWatchTimeline.play();
    }

    @FXML
    void cellClickAction(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof Button button) {
            final String id = button.getId();
            final int row = FifteenPuzzle.getRow(id);
            final int column = FifteenPuzzle.getColumn(id);

            final Optional<Button> maybeEmptyButton = Stream.of(getButtonAt(row - 1, column),
                    getButtonAt(row + 1, column),
                    getButtonAt(row, column - 1),
                    getButtonAt(row, column + 1))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(FifteenPuzzleDialog::isEmpty)
                    .findFirst();

            maybeEmptyButton.ifPresent(emptyButton -> {
                enableButton(emptyButton, button.getText());
                disableButton(button);

                if (puzzleSolved()) {
                    this.stopWatchTimeline.stop();
                    var result = getFormattedTime();
                    this.timerText.setText(result);

                    stageManager.displayWarning("Wygrałeś! Twój czas: " + result);
                }
            });

        }
    }

    private static boolean isEmpty(Button button) {
        return EMPTY.equals(button.getText());
    }

    private Optional<Button> getButtonAt(int row, int column) {
        if (row < 0 || row > 3 || column < 0 || column > 3) {
            return Optional.empty();
        }
        final int index = row * 4 + column;
        return Optional.of(buttonList.get(index));
    }

    private boolean puzzleSolved() {
        var currentList = buttonList.stream().map(Button::getText).toList();
        return ListUtils.isEqualList(currentList, solvedList);
    }

    public void show() {
        stage.show();
        this.buttonList.forEach(this::disableButton);
    }

    private void disableButton(Button button) {
        button.setDisable(true);
        button.setText(EMPTY);
        button.setGraphic(new ImageView(ALPACA_ICON));
    }

    private void enableButton(Button button, String text) {
        button.setDisable(false);
        button.setText(text);
        button.setGraphic(null);
    }

    public void solveGame(ActionEvent actionEvent) {
        final AtomicInteger counter = new AtomicInteger(0);
        buttonList.forEach(button -> {
            final int index = counter.getAndIncrement();
            final String number = solvedList.get(index);
            if (EMPTY.equals(number)) {
                disableButton(button);
            } else {
                enableButton(button, number);
            }
        });


    }
}
