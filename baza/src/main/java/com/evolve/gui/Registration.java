package com.evolve.gui;

import com.evolve.app.EfkaSpringApp;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

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
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageReadyEvent(stage));

        stage.setTitle("Kartoteka");
        stage.setMaximized(true);

//        VBox vBox = new VBox();
//        Group group = new Group();
//        Scene scene = new Scene(group);
//        stage.setTitle("Table View Sample");
//        stage.setWidth(450);
//        stage.setHeight(500);
//        group.getChildren().addAll(mainMenu(), mainTable());
//        stage.setScene(scene);

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 400, 350);
        scene.setFill(Color.OLDLACE);
        vBox.getChildren().addAll(mainMenu(), mainTable());
        stage.setScene(scene);


//        stage.setTitle("Registration Form");
//        stage.setScene(sceneWithGrid());
        stage.show();
    }


    private MenuBar mainMenu() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Plik");
        Menu menuEdit = new Menu("Edytuj");
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
        //vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
//        vbox.getChildren().addAll(label, table);
        vbox.getChildren().addAll(table);

        //((Group) scene.getRoot()).getChildren().addAll(vbox);
        return vbox;
    }


    private Scene sceneWithGrid() {
        //Label for name
        Text nameLabel = new Text("Name");

        //Text field for name
        TextField nameText = new TextField();

        //Label for date of birth
        Text dobLabel = new Text("Date of birth");

        //date picker to choose date
        DatePicker datePicker = new DatePicker();

        //Label for gender
        Text genderLabel = new Text("gender");

        //Toggle group of radio buttons
        ToggleGroup groupGender = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("male");
        maleRadio.setToggleGroup(groupGender);
        RadioButton femaleRadio = new RadioButton("female");
        femaleRadio.setToggleGroup(groupGender);

        //Label for reservation
        Text reservationLabel = new Text("Reservation");

        //Toggle button for reservation
        ToggleButton Reservation = new ToggleButton();
        ToggleButton yes = new ToggleButton("Yes");
        ToggleButton no = new ToggleButton("No");
        ToggleGroup groupReservation = new ToggleGroup();
        yes.setToggleGroup(groupReservation);
        no.setToggleGroup(groupReservation);

        //Label for technologies known
        Text technologiesLabel = new Text("Technologies Known");

        //check box for education
        CheckBox javaCheckBox = new CheckBox("Java");
        javaCheckBox.setIndeterminate(false);

        //check box for education
        CheckBox dotnetCheckBox = new CheckBox("DotNet");
        javaCheckBox.setIndeterminate(false);

        //Label for education
        Text educationLabel = new Text("Educational qualification");

        //list View for educational qualification
        ObservableList<String> names = FXCollections.observableArrayList(
                "Engineering", "MCA", "MBA", "Graduation", "MTECH", "Mphil", "Phd");
        ListView<String> educationListView = new ListView<String>(names);

        //Label for location
        Text locationLabel = new Text("location");

        //Choice box for location
        ChoiceBox locationchoiceBox = new ChoiceBox();
        locationchoiceBox.getItems().addAll
                ("Hyderabad", "Chennai", "Delhi", "Mumbai", "Vishakhapatnam");

        //Label for register
        Button buttonRegister = new Button("Register");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(500, 500);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(nameLabel, 0, 0);
        gridPane.add(nameText, 1, 0);

        gridPane.add(dobLabel, 0, 1);
        gridPane.add(datePicker, 1, 1);

        gridPane.add(genderLabel, 0, 2);
        gridPane.add(maleRadio, 1, 2);
        gridPane.add(femaleRadio, 2, 2);
        gridPane.add(reservationLabel, 0, 3);
        gridPane.add(yes, 1, 3);
        gridPane.add(no, 2, 3);

        gridPane.add(technologiesLabel, 0, 4);
        gridPane.add(javaCheckBox, 1, 4);
        gridPane.add(dotnetCheckBox, 2, 4);

        gridPane.add(educationLabel, 0, 5);
        gridPane.add(educationListView, 1, 5);

        gridPane.add(locationLabel, 0, 6);
        gridPane.add(locationchoiceBox, 1, 6);

        gridPane.add(buttonRegister, 2, 8);

        //Styling nodes
        buttonRegister.setStyle(
                "-fx-background-color: darkslateblue; -fx-textfill: white;");

        nameLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        dobLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        genderLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        reservationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        technologiesLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        educationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");
        locationLabel.setStyle("-fx-font: normal bold 15px 'serif' ");

        //Setting the background color
        gridPane.setStyle("-fx-background-color: BEIGE;");

        //Creating a scene object
        return new Scene(gridPane);
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }
    }

}
