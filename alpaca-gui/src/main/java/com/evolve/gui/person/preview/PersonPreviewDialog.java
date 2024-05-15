package com.evolve.gui.person.preview;

import com.evolve.alpaca.utils.LogUtil;
import com.evolve.domain.Person;
import com.evolve.gui.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import static com.evolve.gui.StageManager.APPLICATION_ICON;

@Getter
@Component
@FxmlView("person-preview.fxml")
@RequiredArgsConstructor
@Slf4j
public class PersonPreviewDialog implements Initializable {

    private final StageManager stageManager;
    private Stage stage;

    @FXML TreeView<PersonTreeItem> previewTreeView;
    @FXML TreeView<PersonTreeItem> previewTreeView1;


    @FXML HBox personPreviewDialog;
    @FXML Button btnClose;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.stage = new Stage();
        stage.initOwner(stageManager.getPrimaryStage());
        stage.setScene(new Scene(personPreviewDialog));
        stage.setTitle("Podgląd");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.getIcons().add(APPLICATION_ICON);
    }

    @SneakyThrows
    public void open(Person person, Person anotherPerson) {
        final JSONParser parser = new JSONParser();

        final String personJson = LogUtil.prettyPrintJson(person);
        log.info("Opening " + personJson);
        JSONObject root = (JSONObject) parser.parse(personJson);
        stage.show();


        previewTreeView.setRoot(new PersonPreviewTreeBuilder(true)
                .of(person));

        previewTreeView1.setRoot(new PersonPreviewTreeBuilder(true)
                .of(anotherPerson));

//        final String rootNode = person.getFirstName() + " " + person.getLastName() + " (" + person.getPersonId() + ")";
//        previewTreeView.setRoot(parseJSON(rootNode, root));
    }

    @FXML
    void onClose(ActionEvent actionEvent) {
        stage.close();
    }

    @SuppressWarnings("unchecked")
    private static TreeItem<String> parseJSON(String name, Object json) {
        TreeItem<String> item = new TreeItem<>();
        if (json instanceof JSONObject) {
            item.setValue(name);
            JSONObject object = (JSONObject) json;
            ((Set<Map.Entry>) object.entrySet()).forEach(entry -> {
                String childName = (String) entry.getKey();
                Object childJson = entry.getValue();
                TreeItem<String> child = parseJSON(childName, childJson);
                item.getChildren().add(child);
            });
        } else if (json instanceof JSONArray) {
            item.setValue(name);
            JSONArray array = (JSONArray) json;
            for (int i = 0; i < array.size(); i++) {
                String childName = String.valueOf(i);
                Object childJson = array.get(i);
                TreeItem<String> child = parseJSON(childName, childJson);
                item.getChildren().add(child);
            }
        } else {
            item.setValue(name + " : " + json);
        }
        return item;
    }
}
