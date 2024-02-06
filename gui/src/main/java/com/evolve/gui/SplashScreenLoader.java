package com.evolve.gui;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashScreenLoader extends Preloader {
    public static final Image SPLASH_IMAGE = new Image("images/alpaca-splash.png");
    private static final int SPLASH_WIDTH = 813 + 20;
    private static final int SPLASH_HEIGHT = 420 + 20;

    private Stage splashScreen;
    private Pane splashLayout;
    private ProgressBar loadProgress;

    @Override
    public void init() throws Exception {
        super.init();

        final ImageView splash = new ImageView(SPLASH_IMAGE);

        this.loadProgress = new ProgressBar();
        this.loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
        Label progressText = new Label("Ładowanie danych...");
        splashLayout = new VBox();
        splashLayout.getChildren().addAll(splash, loadProgress, progressText);
        progressText.setAlignment(Pos.CENTER);
        splashLayout.setStyle("-fx-padding: 5; -fx-background-color: cornsilk; -fx-border-width:5; -fx-border-color: linear-gradient(to bottom, #145DA0, #B1D4E0);");
        splashLayout.setEffect(new DropShadow());
    }

    @Override
    public void start(Stage stage) {
        splashScreen = stage;

        final Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        splashScreen.setScene(splashScene);
        splashScreen.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
        splashScreen.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
        splashScreen.initStyle(StageStyle.TRANSPARENT);
        splashScreen.setAlwaysOnTop(true);
        splashScreen.show();
    }


    @Override
    public void handleApplicationNotification(PreloaderNotification notification) {
        if (notification instanceof StateChangeNotification) {
            loadProgress.setProgress(1);
            splashScreen.toFront();
            FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
            fadeSplash.setFromValue(1.0);
            fadeSplash.setToValue(0.0);
            fadeSplash.setOnFinished(actionEvent -> splashScreen.hide());
            fadeSplash.play();
        }
    }

}
