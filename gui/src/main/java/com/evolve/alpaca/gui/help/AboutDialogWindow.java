package com.evolve.alpaca.gui.help;

import com.evolve.gui.StageManager;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Getter
@Component
@FxmlView("about-dialog.fxml")
@RequiredArgsConstructor
@Slf4j
public class AboutDialogWindow implements Initializable {
    private static final String ABOUT_CONTENT_HTML_FILE = "about-content.html";
    private static final String WINDOW_ABOUT_DIALOG_TITLE = "O programie";
    private final StageManager stageManager;
    private Stage stage;

    @FXML WebView webViewAbout;
    @FXML VBox aboutDialog;
    @FXML Button btnOk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(aboutDialog));
        stage.setTitle(WINDOW_ABOUT_DIALOG_TITLE);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(false);
    }

    public void show() {
        stage.show();
        try {
            final URL url = Resources.getResource(ABOUT_CONTENT_HTML_FILE);
            final String aboutContent = Resources.toString(url, Charsets.UTF_8);
            webViewAbout.getEngine().loadContent(aboutContent);

        } catch (IOException e) {
            log.error("Unable to load file with about content {}", ABOUT_CONTENT_HTML_FILE);
        }

    }

    public void onOk(ActionEvent actionEvent) {
        stage.close();
    }

}
