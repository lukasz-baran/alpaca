package com.evolve.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AlpacaJavafxApp extends Application {

    private ConfigurableApplicationContext applicationContext;
    private final ObservableList<PersonModel> data =
            FXCollections.observableArrayList(
                    new PersonModel("123", "Jacob", "Smith", "jacob.smith@example.com"),
                    new PersonModel("124", "Isabella", "Johnson", "isabella.johnson@example.com"),
                    new PersonModel("125", "Ethan", "Williams", "ethan.williams@example.com"),
                    new PersonModel("126", "Emma", "Jones", "emma.jones@example.com"),
                    new PersonModel("127", "Michael", "Brown", "michael.brown@example.com"));


    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);
        this.applicationContext = new SpringApplicationBuilder()
                .sources(AlpacaSpringApp.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(AppController.class);
        applicationContext.getBean(AppController.class).initialize();
        Scene scene = new Scene(root);
        stage.setTitle("Alpaca - accounting");
        stage.setScene(scene);
        stage.getIcons().add(new Image("alpaca.png"));
        stage.show();
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
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


}

