package com.evolve.gui.documents;

import com.evolve.content.ContentStoreService;
import com.evolve.gui.StageManager;
import com.evolve.utils.LogUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

/**
 * The code in this class is copied from <a href="https://stackoverflow.com/a/57176959/231290" target="_blank">this StackOverflow answer</a>.
 */
@Getter
@Component
@FxmlView("document-viewer.fxml")
@RequiredArgsConstructor
@Slf4j
public class DocumentViewer implements Initializable {
    private final StageManager stageManager;
    private final ContentStoreService contentStoreService;
    private Stage stage;

    @FXML
    HBox documentViewer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(documentViewer));
        stage.setTitle("Document viewer");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
        stage.setResizable(false);
    }

    public void showDocument(DocumentEntry documentEntry) {
        log.info("opening document {}", LogUtil.printJson(documentEntry));

        final InputStream imageInputStream = contentStoreService.getContent(documentEntry.getId());
        final Image image = new Image(imageInputStream);

        // simple displays ImageView the image as is
        ImageView iv1 = new ImageView();
        iv1.setImage(image);

        // resizes the image to have width of 100 while preserving the ratio and using
        // higher quality filtering method; this ImageView is also cached to
        // improve performance
        ImageView iv2 = new ImageView();
        iv2.setImage(image);
        iv2.setFitWidth(100);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);
        iv2.setCache(true);

        // defines a viewport into the source image (achieving a "zoom" effect) and
        // displays it rotated
//        ImageView iv3 = new ImageView();
//        iv3.setImage(image);
//
//        Rectangle2D viewportRect = new Rectangle2D(40, 35, 110, 110);
//        iv3.setViewport(viewportRect);
//        iv3.setRotate(90);

        Group root = new Group();
        Scene scene = new Scene(root);
        //scene.setFill(Color.BLACK);
        HBox box = new HBox();
        box.getChildren().add(iv1);
        box.getChildren().add(iv2);
//        box.getChildren().add(iv3);
        root.getChildren().add(box);

        stage.setTitle(documentEntry.getFileName());
        stage.setWidth(415);
        stage.setHeight(200);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
