package com.evolve.gui;

import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Stage stage;

    public void uncaughtException(Thread t, Throwable e) {
        log.error("Unhandled exception caught!", e);

        StageManager.showCustomErrorDialog(
                        "Błąd aplikacji",
                        "Niestety coś się nie udało. Szczegóły zawarte są poniżej",
                        stage.getScene().getWindow(), e)
                .show();
    }
}
