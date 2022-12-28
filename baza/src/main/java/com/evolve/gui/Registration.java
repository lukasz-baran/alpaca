package com.evolve.gui;

import com.evolve.app.EfkaSpringApp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;


@Slf4j
public class Registration extends Application {
    private ConfigurableApplicationContext applicationContext;

    private final ObservableList<PersonModel> data =
            FXCollections.observableArrayList(
                    new PersonModel("123", "Jacob", "Smith", "jacob.smith@example.com"),
                    new PersonModel("124", "Isabella", "Johnson", "isabella.johnson@example.com"),
                    new PersonModel("125", "Ethan", "Williams", "ethan.williams@example.com"),
                    new PersonModel("126", "Emma", "Jones", "emma.jones@example.com"),
                    new PersonModel("127", "Michael", "Brown", "michael.brown@example.com"));

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(EfkaSpringApp.class).run();
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }


/*    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));

        stage.setTitle("Kartoteka");
        stage.setMaximized(true);

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 400, 350);
        scene.setFill(Color.OLDLACE);
        vBox.getChildren().addAll(mainMenu(), mainTable());
        stage.setScene(scene);
        stage.show();
    }


    private MenuBar mainMenu() {
        final MenuBar menuBar = new MenuBar();
        final Menu menuFile = new Menu("Plik");
        final Menu menuEdit = new Menu("Edytuj");

        final MenuItem menuItem = new MenuItem("Importuj jednostki");
        menuItem.setOnAction(e -> log.info("Opening Database Connection..."));
        menuEdit.getItems().add(menuItem);

        Menu menuView = new Menu("Widok");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        return menuBar;
    }

    private VBox mainTable() {

        final TableView<PersonModel> table = new TableView<>();
        final Label label = new Label("Lista osób");
        label.setFont(new Font("Arial", 12));

        table.setEditable(true);

        TableColumn<PersonModel, String> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(100);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<PersonModel, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<PersonModel, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<PersonModel, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.setItems(data);

        table.getColumns().addAll(idColumn, firstNameCol, lastNameCol, emailCol);

        final VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(table);
        return vbox;
    }*/

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }
    }

}
