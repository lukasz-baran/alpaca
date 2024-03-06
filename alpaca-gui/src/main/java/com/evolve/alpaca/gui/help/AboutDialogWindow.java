package com.evolve.alpaca.gui.help;

import com.evolve.alpaca.conf.AlpacaCommonConfiguration;
import com.evolve.gui.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Getter
@Component
@FxmlView("about-dialog.fxml")
@Slf4j
public class AboutDialogWindow implements Initializable {
    private static final String WINDOW_ABOUT_DIALOG_TITLE = "O programie";
    private static final String DEFAULT_FONT = Font.getDefault().getFamily();

    private final ObservableList<AboutPropertyEntry> propertyEntries = FXCollections.observableArrayList();
    private final StageManager stageManager;
    private Stage stage;

    @FXML VBox aboutDialog;
    @FXML Button btnOk;

    @FXML TextFlow aboutTextFlow;

    @FXML TableView<AboutPropertyEntry> propertiesTable;
    @FXML TableColumn<AboutPropertyEntry, String> propertyNameColumn;
    @FXML TableColumn<AboutPropertyEntry, String> propertyValueColumn;

    final Text textName = new Text("Stowarzyszenie Wzajemnej Pomocy Lekarskiej Regionu Rzeszowskiego\n");
    final Text textAddress = new Text("ul. Dekerta 2\n35-030 Rzeszów");

    final ContextMenu cm = new ContextMenu();
    final MenuItem copyMenuItem = new MenuItem("Kopiuj");

    AboutDialogWindow(StageManager stageManager, AlpacaCommonConfiguration alpacaCommonConfiguration) {
        this.stageManager = stageManager;
        this.propertyEntries.addAll(
                AboutPropertyEntry.of("telefon", "(17) 717 77 26"),
                AboutPropertyEntry.of("email", "kasalek@o2.pl"),
                AboutPropertyEntry.of("KRS", "0000015325"),
                AboutPropertyEntry.of("NIP", "8133066841"),
                AboutPropertyEntry.of("Regon", "690680440"),
                AboutPropertyEntry.of("Nr konta ", "17 1020 4391 0000 6202 0064 8964 (PKO BP I O/RZESZÓW)"),
                AboutPropertyEntry.of("Wersja", alpacaCommonConfiguration.getFullVersionNumber()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();

        // create text
        textName.setFont(Font.font(DEFAULT_FONT, 17));
        textName.setFill(Color.DARKBLUE);

        textAddress.setFill(Color.DARKBLUE);
        textAddress.setFont(Font.font(DEFAULT_FONT, FontWeight.BOLD, 13));

        aboutTextFlow.getChildren().addAll(textName, textAddress);
        aboutTextFlow.setTextAlignment(TextAlignment.CENTER);

        propertyNameColumn.setCellValueFactory(new PropertyValueFactory<>("propertyName"));
        propertyValueColumn.setCellValueFactory(new PropertyValueFactory<>("propertyValue"));
        propertiesTable.setItems(propertyEntries);
        propertiesTable.getStyleClass().add("noheader");

        copyMenuItem.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(textName.getText() + textAddress.getText());
            clipboard.setContent(content);
        });
        cm.getItems().add(copyMenuItem);

        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(aboutDialog));
        stage.setTitle(WINDOW_ABOUT_DIALOG_TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(false);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                closeWindow();
            }
        });
    }

    public void show() {
        stage.show();
    }

    @FXML
    void onOk(ActionEvent actionEvent) {
        closeWindow();
    }

    @FXML
    void copyProperty(ActionEvent actionEvent) {
        final AboutPropertyEntry aboutPropertyEntry = propertiesTable.getSelectionModel().getSelectedItem();
        if (aboutPropertyEntry != null) {
            final String text = aboutPropertyEntry.getPropertyValue();
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(text);
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

    @FXML
    void addressMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
            cm.hide();
            cm.show(aboutTextFlow, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            return;
        }
        cm.hide();
    }

    private void closeWindow() {
        stage.close();
    }
}
