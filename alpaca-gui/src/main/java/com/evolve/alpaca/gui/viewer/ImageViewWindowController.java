package com.evolve.alpaca.gui.viewer;

import com.evolve.content.ContentStoreService;
import com.evolve.gui.StageManager;
import com.evolve.alpaca.document.DocumentEntry;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static com.evolve.gui.StageManager.APPLICATION_ICON;


@FxmlView("image-view-window.fxml")
@Component
@RequiredArgsConstructor
public class ImageViewWindowController implements Initializable {

    private enum ViewStyle {
        FIT_TO_WINDOW, FIT_TO_DESKTOP, ORIGINAL, STRETCHED
    }

    private final StageManager stageManager;
    private final ContentStoreService contentStoreService;

    private ImageViewModel imageViewModel = new ImageViewModel();
    private final Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    private final double zoomStep = 0.1;
    private boolean isCtrlDown = false;

    private final ToggleGroup toggleGroupViewStyle = new ToggleGroup();

    private final SimpleBooleanProperty isViewingFullScreen = new SimpleBooleanProperty(false);
    private final SimpleObjectProperty<ViewStyle> viewStyleProperty = new SimpleObjectProperty<>(ViewStyle.FIT_TO_DESKTOP);


    // Structure: AnchorPane (+FullScreenGrid) > ScrollPane > AnchorPane (+SelRect) > StackPane > ImageView

    @FXML BorderPane borderPaneWindow;
    @FXML RadioMenuItem menuStretched;
    @FXML ImageView imageViewMain;
    @FXML RadioMenuItem menuFitToWindow;
    @FXML RadioMenuItem menuFitToDesktop;
    @FXML RadioMenuItem menuOriginalSize;
    @FXML RadioMenuItem menuFullScreen;
    @FXML ScrollPane scrollPaneMain;
    @FXML Label labelStatus;
    @FXML Label labelPoints;
    @FXML Label labelResolution;
    @FXML ToolBar toolBar;
    @FXML GridPane gridPaneStatusBar;
    @FXML AnchorPane anchorPaneMain;
    @FXML MenuBar menuBar;
    @FXML MenuItem menuClose;
    @FXML GridPane gridPaneQuickInfo;
    @FXML Label labelQuickInfo;
    @FXML Label labelQuickInfo2;
    @FXML Label labelQuickInfo3;

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize only UI control listeners in this method
        this.stage = new Stage();
        Scene scene = new Scene(borderPaneWindow);
        //scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(scene);
        stage.setTitle("Image view");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);

        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setWidth(preferences.getDouble("MainWindowWidth", 854));
        stage.setHeight(preferences.getDouble("MainWindowHeight", 640));
        stage.setX(preferences.getDouble("MainWindowLeft",
                Screen.getPrimary().getVisualBounds().getWidth() / 2 - stage.getWidth() / 2));
        stage.setY(preferences.getDouble("MainWindowTop",
                Screen.getPrimary().getVisualBounds().getHeight() / 2 - stage.getHeight() / 2));
        stage.setOnShown(windowEvent -> {
            // initialize listeners; must be called after UI has loaded
            initUIListeners();

            if (stage.getWidth() >= 0.99 * Screen.getPrimary().getVisualBounds().getWidth()
                    && stage.getHeight() >= 0.99 * Screen.getPrimary().getVisualBounds().getHeight()) {
                stage.setMaximized(true);
            }
        });
        stage.setOnCloseRequest(event -> {
            // save window positions
            preferences.putDouble("MainWindowHeight", stage.getScene().getWindow().getHeight());
            preferences.putDouble("MainWindowWidth", stage.getScene().getWindow().getWidth());
            preferences.putDouble("MainWindowTop", stage.getScene().getWindow().getY());
            preferences.putDouble("MainWindowLeft", stage.getScene().getWindow().getX());
        });

        stage.show(); // must be invoked AFTER setting up OnShown handler


        // reset control properties
        labelResolution.setText("");
        labelPoints.setText("");
        labelQuickInfo.setText("");
        labelQuickInfo2.setText("");
        labelQuickInfo3.setText("");
        gridPaneQuickInfo.toFront();
        imageViewMain.setFitHeight(0);
        imageViewMain.setFitWidth(0);
        labelStatus.textProperty().bind(imageViewModel.statusProperty());

        // MenuBar ToggleGroup
        menuFitToDesktop.setToggleGroup(toggleGroupViewStyle);
        menuStretched.setToggleGroup(toggleGroupViewStyle);
        menuFitToWindow.setToggleGroup(toggleGroupViewStyle);
        menuOriginalSize.setToggleGroup(toggleGroupViewStyle);

        scrollPaneMain.pannableProperty().setValue(true);

        // trigger change listener
        // bindings for full screen viewing
        toolBar.managedProperty().bind(toolBar.visibleProperty());
        gridPaneStatusBar.managedProperty().bind(gridPaneStatusBar.visibleProperty());
        menuBar.managedProperty().bind(menuBar.visibleProperty());
        gridPaneQuickInfo.visibleProperty().bind(isViewingFullScreen);

        // common EventHandler for all toolbar elements; focus on the ImageView whenever any element is actioned
        EventHandler<ActionEvent> defaultToolbarEventHandler = event -> imageViewMain.requestFocus();

        // set focus on the ImageView whenever any Button on the Toolbar is actioned
        toolBar.getItems().forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.addEventHandler(ActionEvent.ACTION, defaultToolbarEventHandler);
            } else if (node instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) node;
                toggleButton.addEventHandler(ActionEvent.ACTION, defaultToolbarEventHandler);
            }
        });

        // start tracking resolution, zoom and SelectionRectangle
        imageViewMain.fitWidthProperty().addListener(new ImageSizeChangeListener());
        imageViewMain.fitHeightProperty().addListener(new ImageSizeChangeListener());
    }

    /**
     * Sets up the listeners to various virtual (ViewModel) properties. Important to call this after the UI has loaded.
     */
    public void initUIListeners() {
        // remove SelectionRectangle if window size is changed
        // Window must be called after the stage has initialized
        Window window = imageViewMain.getScene().getWindow();

        double titleBarHeight = window.getHeight() - window.getScene().getHeight();

        double fixedWidth = window.getWidth() - window.getScene().getWidth();
        double fixedHeight = titleBarHeight + menuBar.getHeight() + toolBar.getHeight() + gridPaneStatusBar.getHeight();

        // bind ChangeListeners
        imageViewModel.selectedImageModelProperty().addListener(new ImageChangeListener());
        viewStyleProperty.addListener(new ViewStyleChangeListener(fixedWidth, fixedHeight));

        // restore previous settings
        viewStyleProperty.set(ViewStyle.valueOf( /* Default view style */
                preferences.get("LastViewStyle", ViewStyle.FIT_TO_DESKTOP.toString())));

        // refresh ViewStyle if we're switching to full screen mode
        isViewingFullScreen.addListener(((observable, oldValue, newValue) -> {
            ViewStyle old = viewStyleProperty.get();
            viewStyleProperty.set(null);
            viewStyleProperty.set(old);

            // don't show scroll bars if we're in full screen mode
            if (newValue) {
                scrollPaneMain.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPaneMain.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            } else {
                scrollPaneMain.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                scrollPaneMain.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            }
        }));
    }

    @FXML
    public void scrollPaneMain_onClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            scrollPaneMain.requestFocus();
            if (mouseEvent.getClickCount() == 2) {
                toggleFullScreen();
            }
        } else if (mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
            toggleFullScreen();
        }
    }

    @FXML
    public void scrollPaneMain_onKeyPress(KeyEvent keyEvent) {

        switch (keyEvent.getCode()) {

            case ENTER:
                toggleFullScreen();
                break;

            case ESCAPE:
                if (isViewingFullScreen.get()) {
                    toggleFullScreen();
                } else {
                    stage.close();
                }
                break;

            case CONTROL:
                isCtrlDown = true;
                break;

            case SHIFT:
            case ALT:
                // prevent conflict with menu shortcuts
                isCtrlDown = false;
                break;

            default:
                break;
        }
    }

    public void scrollPaneMain_onKeyRelease(KeyEvent keyEvent) {
        isCtrlDown = false;
    }

    public void scrollPaneMain_onScroll(ScrollEvent scrollEvent) {
        scrollEvent.consume();

        if (isCtrlDown) {

            // ctrl is down; zoom image instead of switching
            if (scrollEvent.getDeltaY() > 0 || scrollEvent.getDeltaX() > 0) {
                zoomIn();
            } else if (scrollEvent.getDeltaY() < 0 || scrollEvent.getDeltaX() < 0) {
                zoomOut();
            }

        } else {

            // don't switch images if scrollbar is visible
            if (getViewingWidth() > scrollPaneMain.getViewportBounds().getWidth()
                    || getViewingHeight() > scrollPaneMain.getViewportBounds().getHeight()) {
                return;
            }

        }
    }

    public void imageViewMain_onMouseExit(MouseEvent mouseEvent) {
        labelPoints.setText("");
    }

    public void imageViewMain_onMouseMove(MouseEvent mouseEvent) {
        refreshCoordinates(mouseEvent.getX(), mouseEvent.getY());
    }


    private void toggleFullScreen() {
        if (imageViewModel.getSelectedImageModel() == null) {
            return;
        }

        boolean setFullScreen = !isViewingFullScreen.get();
        ((Stage) scrollPaneMain.getScene().getWindow()).setFullScreen(setFullScreen);
        menuBar.setVisible(!setFullScreen);
        toolBar.setVisible(!setFullScreen);
        gridPaneStatusBar.setVisible(!setFullScreen);
        isViewingFullScreen.set(setFullScreen);
        menuFullScreen.setSelected(setFullScreen);
    }

    /**
     * @return The width of the image as it's being displayed on the screen.
     */
    private double getViewingWidth() {
        double width = imageViewMain.getFitHeight() * imageViewModel.getSelectedImageModel().getAspectRatio();
        if (width > imageViewMain.getFitWidth()) {
            width = imageViewMain.getFitWidth();
        }

        return width;
    }

    /**
     * @return The height of the image as it's being displayed on the screen.
     */
    private double getViewingHeight() {
        double height = imageViewMain.getFitWidth() / imageViewModel.getSelectedImageModel().getAspectRatio();
        if (height > imageViewMain.getFitHeight()) {
            height = imageViewMain.getFitHeight();
        }

        return height;
    }

    /**
     * @return Size of the displayed image as a ratio of the size of the original image
     */
    private double getCurrentViewingZoom() {
        return BigDecimal.valueOf(100 * getViewingWidth()
                        / (imageViewModel.getSelectedImageModel().hasOriginal()
                        ? imageViewModel.getSelectedImageModel().getOriginal().getWidth()
                        : imageViewModel.getSelectedImageModel().getWidth()))
                .setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Updates the title of the application window.
     */
    private void updateTitle() {
        Stage stage = (Stage) imageViewMain.getScene().getWindow();
        String title;
        if (imageViewModel.getSelectedImageModel() == null) {
            title = "SlimView";
        } else {
            title = imageViewModel.getSelectedImageModel().getName() + " - SlimView [Zoom: "
                    + (int) getViewingWidth() + " x " + (int) getViewingHeight() + " px]";
        }
        stage.setTitle(title);
    }

    private void updateFullScreenInfo() {
//        labelQuickInfo.setText(mainViewModel.getSelectedImageModel().getBestPath() + " ["
//                + (mainViewModel.getIndex(mainViewModel.getSelectedImageModel()) + 1) + "/"
//                + mainViewModel.getFileCount() + "]");
        labelQuickInfo2.setText(getCurrentViewingZoom() + "%");
        labelQuickInfo3.setText(imageViewModel.getSelectedImageModel().hasOriginal()
                ? imageViewModel.getSelectedImageModel().getOriginal().getResolution()
                : imageViewModel.getSelectedImageModel().getResolution());
    }

    public void openImage(DocumentEntry documentEntry) {
        try {
            stage.show();
            final InputStream imageInputStream = contentStoreService.getContent(documentEntry.getId());
            imageViewModel.loadImage(imageInputStream, documentEntry);
        } catch (IOException e) {
            showLoadingFailedError(e);
        }
    }


    private void zoom(double targetWidth, double targetHeight) {
        double maxAllowedWidth = 5
                * (imageViewModel.getSelectedImageModel().hasOriginal()
                ? imageViewModel.getSelectedImageModel().getOriginal().getWidth()
                : imageViewModel.getSelectedImageModel().getWidth());
        double maxAllowedHeight = 5
                * (imageViewModel.getSelectedImageModel().hasOriginal()
                ? imageViewModel.getSelectedImageModel().getOriginal().getHeight()
                : imageViewModel.getSelectedImageModel().getHeight());
        double minAllowedWidth = Math.max(1, maxAllowedWidth / 5 * 0.1);
        double minAllowedHeight = Math.max(1, maxAllowedHeight / 5 * 0.1);

        if (targetWidth > maxAllowedWidth) {
            targetWidth = maxAllowedWidth;
        }
        if (targetWidth < minAllowedWidth) {
            targetWidth = minAllowedWidth;
        }
        if (targetHeight > maxAllowedHeight) {
            targetHeight = maxAllowedHeight;
        }
        if (targetHeight < minAllowedHeight) {
            targetHeight = minAllowedHeight;
        }

        imageViewMain.fitHeightProperty().unbind();
        imageViewMain.fitWidthProperty().unbind();
        imageViewMain.setFitWidth(targetWidth);
        imageViewMain.setFitHeight(targetHeight);

        // center new zoomed image
        scrollPaneMain.setVvalue(0.5);
        scrollPaneMain.setHvalue(0.5);
    }

    private void zoomIn() {
        double targetWidth = getViewingWidth() * (1 + zoomStep);
        double targetHeight = getViewingHeight() * (1 + zoomStep);
        zoom(targetWidth, targetHeight);
    }

    private void zoomOut() {
        double targetWidth = getViewingWidth() * (1 - zoomStep);
        double targetHeight = getViewingHeight() * (1 - zoomStep);
        zoom(targetWidth, targetHeight);
    }

    private void resetZoom() {
        viewStyleProperty.set(null);
        viewStyleProperty.set(ViewStyle.ORIGINAL);
    }

    private void bestFit() {
        viewStyleProperty.set(null);
        viewStyleProperty.set(ViewStyle.FIT_TO_WINDOW);
    }

    /**
     * Shows the coordinates of the cursor point when the mouse is hovering over the image
     */
    private void refreshCoordinates(double x, double y) {
        if (imageViewModel.getSelectedImageModel() == null) {
            labelPoints.setText("");
        } else {
            labelPoints.setText("[ " + (int) x + ", " + (int) y + " ]");
        }
    }

    private void showLoadingFailedError(Exception e) {

        StageManager.showCustomErrorDialog(
                        "Loading failed",
                        "The requested file doesn't exist or is unreadable.",
                        imageViewMain.getScene().getWindow(), e)
                .show();
    }

    /**
     * Positions a child window at the center of a parent window
     *
     * @param owner The owning window
     * @param child The window to be centered
     */
    private void childWindowInCentre(Window owner, Window child) {
        child.setOnShown(event -> {
            child.setX(owner.getX() + owner.getWidth() / 2 - child.getWidth() / 2);
            child.setY(owner.getY() + owner.getHeight() / 3 - child.getHeight() / 2);
        });
    }

    /**
     * Triggered when the image is changed
     */
    private class ImageChangeListener implements ChangeListener<ImageModel> {

        @Override
        public void changed(ObservableValue<? extends ImageModel> observable,
                            ImageModel oldValue,
                            ImageModel newValue) {

            // check if image is corrupted
            if (newValue != null && newValue.getImage() == null) {
                showLoadingFailedError(null);
            }

            imageViewMain.setImage(newValue.getImage());

            // reset the ViewStyle if we've zoomed image
            ViewStyle currentViewStyle = viewStyleProperty.get();
            viewStyleProperty.set(null); // force trigger ChangeListener
            viewStyleProperty.set(currentViewStyle);

            imageViewMain.requestFocus();
            updateFullScreenInfo();

            try {
                updateTitle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Triggered when the ViewStyle is changed
     */
    private class ViewStyleChangeListener implements ChangeListener<ViewStyle> {

        private final double fixedWidth;
        private final double fixedHeight;

        /**
         * @param fixedWidth  Width of the fixed UI elements such as window borders
         * @param fixedHeight Height of the fixed UI elements such as title bar and menu bar
         */
        public ViewStyleChangeListener(double fixedWidth, double fixedHeight) {
            this.fixedWidth = fixedWidth;
            this.fixedHeight = fixedHeight;
        }

        @Override
        public void changed(ObservableValue<? extends ViewStyle> observable,
                            ViewStyle oldValue,
                            ViewStyle newValue) {

            if (imageViewModel.getSelectedImageModel() == null) {
                return;
            }

            if (newValue == null) {
                newValue = Objects.requireNonNullElse(oldValue, ViewStyle.FIT_TO_WINDOW);
            }

            imageViewMain.setPreserveRatio(true);
            imageViewMain.fitWidthProperty().unbind();
            imageViewMain.fitHeightProperty().unbind();

            // reset image size first
            if (imageViewMain.getImage() != null) {
                imageViewMain.setFitWidth(imageViewModel.getSelectedImageModel().getWidth());
                imageViewMain.setFitHeight(imageViewModel.getSelectedImageModel().getHeight());
            }

            switch (newValue) {
                case ORIGINAL:

                    menuOriginalSize.setSelected(true);
                    break;

                case FIT_TO_WINDOW:

                    menuFitToWindow.setSelected(true);
                    imageViewMain.fitWidthProperty().bind(scrollPaneMain.widthProperty());
                    imageViewMain.fitHeightProperty().bind(scrollPaneMain.heightProperty());
                    break;

                case FIT_TO_DESKTOP:

                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    double screenWidth = screenSize.getWidth();
                    double screenHeight = screenSize.getHeight();
                    double desktopViewportWidth /* Takes screen insets into account */
                            = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth();
                    double desktopViewportHeight
                            = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();
                    double aspectRatio = imageViewModel.getSelectedImageModel().getAspectRatio();

                    double viewportWidth = desktopViewportWidth - fixedWidth;
                    double viewportHeight = desktopViewportHeight - fixedHeight;

                    double finalWidth,
                            finalHeight;

                    if (!isViewingFullScreen.get()) {

                        finalWidth = viewportWidth;
                        finalHeight = finalWidth / aspectRatio;
                        if (finalHeight > viewportHeight) {
                            finalHeight = viewportHeight;
                            finalWidth = aspectRatio * finalHeight;
                        }

                        Window window = imageViewMain.getScene().getWindow();

                        ((Stage) window).setMaximized(false);
                        window.setWidth(finalWidth + fixedWidth);
                        window.setHeight(finalHeight + fixedHeight);

                        // ensure window remains within view
                        if (window.getX() + window.getWidth() > desktopViewportWidth) {
                            window.setX(desktopViewportWidth - window.getWidth());
                        } else if (window.getX() < 0) {
                            window.setX(0);
                        }

                        if (window.getY() + window.getHeight() > desktopViewportHeight) {
                            window.setY(desktopViewportHeight - window.getHeight());
                        } else if (window.getY() < 0) {
                            window.setY(0);
                        }

                    } else {

                        finalWidth = screenWidth;
                        finalHeight = finalWidth / aspectRatio;
                        if (finalHeight > screenHeight) {
                            finalHeight = screenHeight;
                            finalWidth = aspectRatio * finalHeight;
                        }
                    }

                    menuFitToDesktop.setSelected(true);
                    imageViewMain.setPreserveRatio(false);
                    imageViewMain.setFitWidth(finalWidth);
                    imageViewMain.setFitHeight(finalHeight);
                    break;

                case STRETCHED:

                    menuStretched.setSelected(true);
                    imageViewMain.setPreserveRatio(false);
                    imageViewMain.fitWidthProperty().bind(scrollPaneMain.widthProperty());
                    imageViewMain.fitHeightProperty().bind(scrollPaneMain.heightProperty());
                    break;
            }

            preferences.put("LastViewStyle", newValue.toString());
            imageViewMain.requestFocus();
        }
    }

    /**
     * Triggered when the image size is changed
     */
    private class ImageSizeChangeListener implements ChangeListener<Number> {

        @Override
        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

            updateTitle();
            updateFullScreenInfo();
            labelResolution.setText("");

            if (imageViewModel.getSelectedImageModel() != null) {
                labelResolution.setText(
                        (imageViewModel.getSelectedImageModel().hasOriginal()
                                ? imageViewModel.getSelectedImageModel().getOriginal().getResolution()
                                : imageViewModel.getSelectedImageModel().getResolution())
                                + " (" + getCurrentViewingZoom() + "%)");
            }
        }
    }

    public void menuClose_onAction(ActionEvent actionEvent) {
        stage.close();
    }

    public void menuFullScreen_onAction(ActionEvent actionEvent) {
        toggleFullScreen();
    }

    public void menuOriginalSize_onAction(ActionEvent actionEvent) {
        viewStyleProperty.set(ViewStyle.ORIGINAL);
    }

    public void menuFitToWindow_onAction(ActionEvent actionEvent) {
        bestFit();
    }

    public void menuStretched_onAction(ActionEvent actionEvent) {
        viewStyleProperty.set(ViewStyle.STRETCHED);
    }

    public void menuZoomIn_onAction(ActionEvent actionEvent) {
        zoomIn();
    }

    public void menuZoomOut_onAction(ActionEvent actionEvent) {
        zoomOut();
    }

    public void menuResetZoom_onAction(ActionEvent actionEvent) {
        resetZoom();
    }

    public void buttonResetZoom_onAction(ActionEvent actionEvent) {
        bestFit();
    }

    public void buttonZoomOut_onAction(ActionEvent actionEvent) {
        zoomOut();
    }

    public void buttonZoomIn_onAction(ActionEvent actionEvent) {
        zoomIn();
    }

    public void menuFitToDesktop_onAction(ActionEvent actionEvent) {
        viewStyleProperty.set(ViewStyle.FIT_TO_DESKTOP);
    }
}

