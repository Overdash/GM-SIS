package customers.logic;

/**
 * Created by Belal on 03/02/2017.
 */

//make bookings past and future

import UI.Menu;
import UI.ScreenHandler;
import UI.ScreenMaster;
import Vehicles.Logic.VehiclesController;
import common.Database;
import common.Main;
import diagrep.logic.BookingController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import misc.AlertBox;
import parts.PartsController;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_PREF_SIZE;

public class CustomerController implements Initializable, ScreenMaster {

    //navbar
    @FXML
    private Button navCust;
    @FXML
    private Button navVech;
    @FXML
    private Button navParts;
    @FXML
    private Button navBook;
    @FXML
    private Button navUP;
    @FXML
    private Button navOut;
    @FXML
    private Button minBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private Button sRep;
    @FXML
    private Label title;
    @FXML
    private BorderPane draggable;

    //buttons
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnPaid;

    //variables to make the mouse draggable for add/edit
    private static double xOffset = 0;
    private static double yOffset = 0;
    private TextArea textArea;

    //search bar
    @FXML
    private TextField searchBox;

    @FXML private ChoiceBox<String> typeCB;

    //customer details
    @FXML
    private Label custFirstAdd;
    @FXML
    private Label custNumber;
    @FXML
    private Label custEmail;
    @FXML
    private Label custID;
    @FXML
    private Label custType;
    @FXML
    private Label custPostcode;
    @FXML
    private Label custCity;
    @FXML
    private Label custSecondAdd;


    //bookings details
    @FXML
    private Label bookCost;
    @FXML
    private Label bookEmployeeID;
    @FXML
    private Label bookType;
    @FXML
    private Label bookDate;
    @FXML
    private Label bookID;
    @FXML
    private Label bookFaultStatus;
    @FXML
    private Label bookFinishDate;
    @FXML
    private TextArea bookFault;

    //vehicle details
    @FXML
    private Label vehMoTdate;
    @FXML
    private Label vehMileage;
    @FXML
    private Label vehModel;
    @FXML
    private Label vehFuelType;
    @FXML
    private Label vehWarranty;
    @FXML
    private Label vehColour;
    @FXML
    private Label vehEngineSize;
    @FXML
    private Label vehRegNumber;
    @FXML
    private Label vehMake;
    @FXML
    private Label vehLastService;

    //part details
    @FXML
    private Label parID;
    @FXML
    private Label parInstallDate;
    @FXML
    private Label parWarExpDate;
    @FXML
    private Label parCost;
    @FXML
    private Label parStock;
    @FXML
    private TextArea parDescription;

    //bills
    @FXML
    private Label billTotal;
    @FXML
    private TextArea billStatus;

    //table customer
    @FXML
    private TableView<CustomerData> customerTable;

    @FXML
    private TableColumn<CustomerData, Number> colID;
    @FXML
    private TableColumn<CustomerData, String> colFirstname;
    @FXML
    private TableColumn<CustomerData, String> colSurname;
    @FXML
    private TableColumn<CustomerData, String> colVehReg;
    @FXML
    private TableColumn<CustomerData, Integer> colType;

    //booking table
    @FXML private TableView<CustomerData> bookingPastTable;

    @FXML private TableView<CustomerData> bookingFutureTable;

    @FXML private TableColumn<CustomerData, String> colPastDate;

    @FXML private TableColumn<CustomerData, String> colFutureDate;

    @FXML private TableColumn<CustomerData, Number> colPastBookID;

    @FXML private TableColumn<CustomerData, Number> colFutureBookID;

    //parts table
    @FXML private TableView<CustomerData> partsTable;

    @FXML private TableColumn<CustomerData, String> colPartName;

    @FXML private TableColumn<CustomerData, Number> colPartID;

    private ScreenHandler sh;
    private Database db;


    /**
     * This method is called when the FXML loads.
     * contains all the listeners for the different tables in the screen.
     * @param location implicitly called when FXML Loads
     * @param resources implicitly called when FXML Loads
     */
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        Menu.loadTopBars(title, closeBtn, minBtn, draggable);

        navVech.setOnAction(e -> goToVeh());
        navParts.setOnAction(e -> goToParts());
        sRep.setOnAction(e -> goToSPC());

        setFields();
        loadCustomerData(2);
        bookingFutureTable.setPlaceholder(new Label("Select a vehicle"));
        bookingPastTable.setPlaceholder(new Label("Select a vehicle"));
        partsTable.setPlaceholder(new Label("Select a booking"));

        ObservableList<String> searchChoices = FXCollections.observableArrayList(
                "Both",
                "Business",
                "Private"
        );
        typeCB.setItems(searchChoices);
        typeCB.getSelectionModel().select("Both");

        typeCB.getSelectionModel().selectedIndexProperty().addListener(
                ((observable, oldValue, newValue) -> {
                    int choice = typeCB.getSelectionModel().getSelectedIndex();
                    switch (choice) {
                        case 0:
                            loadCustomerData(2);
                            break;
                        case 1:
                            loadCustomerData(1);
                            break;
                        case 2:
                            loadCustomerData(0);
                            break;
                    }
                })
        );

        // Add Listener to update fields depending on the selected item
        customerTable.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try{
                    updateDetails(customerTable.getSelectionModel().getSelectedItem().getcolID());}
                    catch(NullPointerException e) {
                        bookingPastTable.getItems().clear();
                        bookingFutureTable.getItems().clear();
                        vehMoTdate.setText("");
                        vehMileage.setText("");
                        vehModel.setText("");
                        vehFuelType.setText("");
                        vehWarranty.setText("");
                        vehColour.setText("");
                        vehEngineSize.setText("");
                        vehRegNumber.setText("");
                        vehMake.setText("");
                        vehLastService.setText("");
                        bookID.setText("");
                        btnPaid.setVisible(false);
                        bookDate.setText("");
                        bookFinishDate.setText("");
                        bookFault.setText("");
                        bookType.setText("");
                        billStatus.setText("");
                        billTotal.setText("");
                        custCity.setText("");
                        custEmail.setText("");
                        custFirstAdd.setText("");
                        custID.setText("");
                        custNumber.setText("");
                        custPostcode.setText("");
                        custSecondAdd.setText("");
                        custType.setText("");
                    }
                }
        );


            bookingPastTable.getSelectionModel().selectedIndexProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        try{
                        updateBookingDetails(bookingPastTable.getSelectionModel().getSelectedItem().getcolBookID(), 1);}
                        catch(NullPointerException e) {
                            partsTable.getItems().clear();
                          }
                    }
            );


            bookingFutureTable.getSelectionModel().selectedIndexProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        try{
                        updateBookingDetails(bookingFutureTable.getSelectionModel().getSelectedItem().getcolBookID(), 2);}
                        catch(NullPointerException e) {
                            partsTable.getItems().clear();
                       }
                    }
            );

            partsTable.getSelectionModel().selectedIndexProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        try{
                            if (bookingPastTable.getSelectionModel().getSelectedItem()!= null)
                        updatePartDetails(partsTable.getSelectionModel().getSelectedItem().getcolPartID(), bookingPastTable.getSelectionModel().getSelectedItem().getcolBookID());
                            else  updatePartDetails(partsTable.getSelectionModel().getSelectedItem().getcolPartID(), bookingFutureTable.getSelectionModel().getSelectedItem().getcolBookID());
                        }
                         catch(NullPointerException e) {
                             parID.setText("");
                             parCost.setText("");
                             parDescription.setText("");
                             parInstallDate.setText("");
                             parWarExpDate.setText("");
                        }
                    }
            );

        btnPaid.setVisible(false);

    }

    /**
     * Called when the add button is pressed.
     */
    @FXML
    private void addNew() {
        addNewEdit(false);
    }

    /**
     * Called when the edit button is pressed.
     */
    @FXML
    private void edit() {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            addNewEdit(true);
        } else {
            AlertBox.error("Error Selection", "Select A Customer Record!");
        }
    }

    /**
     * The methods addNew() and edit() use this method to add or edit a customer.
     * A GUI is created and loaded with all the relevant data to add/edit.
     * All fields are validated before any values are parsed into the database
     * Upon confirmation the data is added to the database.
     * @param edit Whether the function is to add or edit a customer record.
     */
    private void addNewEdit(boolean edit) {
        //Stage for ADD NEW popup
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); //Cant interact with the main app without closing the popup
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initOwner(Main.stage);

        AnchorPane anchorPane = new AnchorPane();
        BorderPane borderPane = new BorderPane();
        BorderPane draggable = new BorderPane();
        Pane pane = new Pane();

        Button closeBtn = new Button();
        Button minBtn = new Button();
        Button addBtn = new Button();
        Button cancelBtn = new Button();

        RadioButton businessRB = new RadioButton();
        RadioButton privateRB = new RadioButton();

        TextField textField = new TextField();
        Label label = new Label();
        TextField textField0 = new TextField();
        Label label0 = new Label();
        TextField textField1 = new TextField();
        Label label1 = new Label();
        Label label2 = new Label();
        Label label3 = new Label();
        TextField textField2 = new TextField();
        Label label4 = new Label();
        TextField textField3 = new TextField();
        TextField textField4 = new TextField();
        Label label5 = new Label();
        TextField textField5 = new TextField();
        Label label6 = new Label();
        Label label7 = new Label();
        TextField textField6 = new TextField();
        Label label8 = new Label();
        textArea = new TextArea();

        anchorPane.setId("main__body");
        anchorPane.setMaxHeight(USE_PREF_SIZE);
        anchorPane.setMaxWidth(USE_PREF_SIZE);
        anchorPane.setMinHeight(USE_PREF_SIZE);
        anchorPane.setMinWidth(USE_PREF_SIZE);
        anchorPane.setPrefHeight(543.0);
        anchorPane.setPrefWidth(390.0);
        anchorPane.getStylesheets().add("UI/MainView.css");

        pane.setId("nav_container");
        pane.setPrefHeight(65.0);
        pane.setPrefWidth(390.0);
        pane.getStyleClass().add("header");
        pane.getStylesheets().add("UI/MainView.css");

        borderPane.setLayoutX(300.0);
        borderPane.setLayoutY(12.0);
        borderPane.setPrefHeight(37.0);
        borderPane.setPrefWidth(76.0);

        BorderPane.setAlignment(closeBtn, javafx.geometry.Pos.CENTER);
        closeBtn.setMaxHeight(USE_PREF_SIZE);
        closeBtn.setMaxWidth(USE_PREF_SIZE);
        closeBtn.setMinHeight(USE_PREF_SIZE);
        closeBtn.setMinWidth(USE_PREF_SIZE);
        closeBtn.setMnemonicParsing(false);
        closeBtn.setPrefHeight(16.0);
        closeBtn.setPrefWidth(14.0);
        closeBtn.getStyleClass().add("button_close");
        closeBtn.setText("-");
        borderPane.setRight(closeBtn);

        BorderPane.setAlignment(minBtn, javafx.geometry.Pos.CENTER);
        minBtn.setMaxHeight(USE_PREF_SIZE);
        minBtn.setMaxWidth(USE_PREF_SIZE);
        minBtn.setMinHeight(USE_PREF_SIZE);
        minBtn.setMinWidth(USE_PREF_SIZE);
        minBtn.setMnemonicParsing(false);
        minBtn.setPrefHeight(16.0);
        minBtn.setPrefWidth(14.0);
        minBtn.getStyleClass().add("button_mini");
        minBtn.setText("-");
        borderPane.setCenter(minBtn);

        draggable.setLayoutX(4.0);
        draggable.setLayoutY(7.0);
        draggable.setPrefHeight(16.0);
        draggable.setPrefWidth(381.0);

        businessRB.setLayoutX(254.0);
        businessRB.setLayoutY(82.0);
        businessRB.setMnemonicParsing(false);
        businessRB.getStyleClass().add("title_smaller");
        businessRB.setText("Business");

        textField.setLayoutX(141.0);
        textField.setLayoutY(199.0);
        textField.setPrefHeight(25.0);
        textField.setPrefWidth(228.0);
        textField.setPromptText("1st Line");

        label.setLayoutX(17.0);
        label.setLayoutY(229.0);
        label.getStyleClass().add("header_title");
        label.setText("Address Line 2:");

        textField0.setLayoutX(141.0);
        textField0.setLayoutY(158.0);
        textField0.setPrefHeight(25.0);
        textField0.setPrefWidth(228.0);
        textField0.setPromptText("Enter Surname");

        label0.setLayoutX(17.0);
        label0.setLayoutY(158.0);
        label0.getStyleClass().add("header_title");
        label0.setText("Surname:*");

        textField1.setLayoutX(141.0);
        textField1.setLayoutY(328.0);
        textField1.setPrefHeight(25.0);
        textField1.setPrefWidth(228.0);
        textField1.setPromptText("Enter Number");

        addBtn.setLayoutX(27.0);
        addBtn.setLayoutY(400.0);
        addBtn.setMnemonicParsing(false);
        addBtn.setPrefWidth(177.0);
        addBtn.setText("Add New Customer");
        addBtn.getStyleClass().add("listView");
        addBtn.getStyleClass().add("info_blue");

        privateRB.setLayoutX(153.0);
        privateRB.setLayoutY(82.0);
        privateRB.setMnemonicParsing(false);
        privateRB.setPrefHeight(17.0);
        privateRB.setPrefWidth(101.0);
        privateRB.setSelected(true);
        privateRB.getStyleClass().add("title_smaller");
        privateRB.setText("Private");

        label1.setLayoutX(17.0);
        label1.setLayoutY(328.0);
        label1.setPrefHeight(25.0);
        label1.setPrefWidth(73.0);
        label1.getStyleClass().add("header_title");
        label1.setText("Number:*");

        label2.setLayoutX(17.0);
        label2.setLayoutY(298.0);
        label2.getStyleClass().add("header_title");
        label2.setText("Postcode:*");

        label3.setLayoutX(17.0);
        label3.setLayoutY(30.0);
        label3.getStyleClass().add("header_title");
        label3.setText("Add New Customer");

        textField2.setLayoutX(141.0);
        textField2.setLayoutY(298.0);
        textField2.setPrefHeight(25.0);
        textField2.setPrefWidth(228.0);
        textField2.setPromptText("Enter Postcode");

        textArea.setEditable(false);
        textArea.setLayoutX(16.0);
        textArea.setLayoutY(444.0);
        textArea.setPrefHeight(70.0);
        textArea.setPrefWidth(356.0);
        textArea.setPromptText("Errors");

        label4.setLayoutX(17.0);
        label4.setLayoutY(82.0);
        label4.getStyleClass().add("header_title");
        label4.setText("Customer Type:*");

        textField3.setLayoutX(141.0);
        textField3.setLayoutY(229.0);
        textField3.setPrefHeight(25.0);
        textField3.setPrefWidth(228.0);
        textField3.setPromptText("2nd Line");

        cancelBtn.setLayoutX(308.0);
        cancelBtn.setLayoutY(400.0);
        cancelBtn.setMnemonicParsing(false);
        cancelBtn.setText("Cancel");
        cancelBtn.getStyleClass().add("listView");
        cancelBtn.getStyleClass().add("info_blue");

        textField4.setLayoutX(141.0);
        textField4.setLayoutY(119.0);
        textField4.setPrefHeight(25.0);
        textField4.setPrefWidth(228.0);
        textField4.setPromptText("Enter Forename");

        label5.setLayoutX(17.0);
        label5.setLayoutY(365.0);
        label5.getStyleClass().add("header_title");
        label5.setText("Email:*");

        textField5.setLayoutX(141.0);
        textField5.setLayoutY(267.0);
        textField5.setPrefHeight(25.0);
        textField5.setPrefWidth(228.0);
        textField5.setPromptText("Enter City");

        label6.setLayoutX(17.0);
        label6.setLayoutY(119.0);
        label6.getStyleClass().add("header_title");
        label6.setText("Forname:*");

        label7.setLayoutX(17.0);
        label7.setLayoutY(199.0);
        label7.getStyleClass().add("header_title");
        label7.setText("Address Line 1:*");

        textField6.setLayoutX(141.0);
        textField6.setLayoutY(365.0);
        textField6.setPrefHeight(25.0);
        textField6.setPrefWidth(228.0);
        textField6.setPromptText("Enter Email");

        label8.setLayoutX(17.0);
        label8.setLayoutY(267.0);
        label8.getStyleClass().add("header_title");
        label8.setText("City:*");

        final ToggleGroup togGroup = new ToggleGroup();
        businessRB.setToggleGroup(togGroup);
        privateRB.setToggleGroup(togGroup);

        togGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (businessRB.isSelected())
                        privateRB.setSelected(false);
                    else
                        businessRB.setSelected(false);
                });

        pane.getChildren().add(borderPane);
        pane.getChildren().add(draggable);
        pane.getChildren().add(label3);
        anchorPane.getChildren().add(pane);
        anchorPane.getChildren().add(businessRB);
        anchorPane.getChildren().add(privateRB);
        anchorPane.getChildren().add(textField);
        anchorPane.getChildren().add(label);
        anchorPane.getChildren().add(textField0);
        anchorPane.getChildren().add(label0);
        anchorPane.getChildren().add(textField1);
        anchorPane.getChildren().add(label1);
        anchorPane.getChildren().add(label2);
        anchorPane.getChildren().add(textField2);
        anchorPane.getChildren().add(textArea);
        anchorPane.getChildren().add(label4);
        anchorPane.getChildren().add(textField3);
        anchorPane.getChildren().add(textField4);
        anchorPane.getChildren().add(label5);
        anchorPane.getChildren().add(textField5);
        anchorPane.getChildren().add(label6);
        anchorPane.getChildren().add(label7);
        anchorPane.getChildren().add(textField6);
        anchorPane.getChildren().add(label8);
        anchorPane.getChildren().add(addBtn);
        anchorPane.getChildren().add(cancelBtn);

        //make the window draggable
        draggable.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
        });

        draggable.setOnMouseDragged(event -> {
                draggable.getScene().getWindow().setX(event.getScreenX() - xOffset);
                draggable.getScene().getWindow().setY(event.getScreenY() - yOffset);
        });

        if (edit)
        {
            label3.setText("Edit Customer");
            addBtn.setText("Edit Customer");
            CustomerData selectedData = customerTable.getSelectionModel().getSelectedItem();
            int selected = selectedData.getcolID();
            String getSelectedCustomer = "SELECT * FROM Customers WHERE CustomerID =" + selected;
            ResultSet selectedRS = db.query(getSelectedCustomer);
            try {
                if (selectedRS.getString("Type").equals("Business")) {
                    businessRB.setSelected(true);
                } else {
                    privateRB.setSelected(true);
                }

                textField4.setText(selectedRS.getString("Firstname"));
                textField0.setText(selectedRS.getString("Surname"));
                textField.setText(selectedRS.getString("FirstAddressLine"));
                textField3.setText(selectedRS.getString("SecondAddressLine"));
                textField5.setText(selectedRS.getString("City"));
                textField2.setText(selectedRS.getString("Postcode"));
                textField1.setText(selectedRS.getString("Number"));
                textField6.setText(selectedRS.getString("Email"));

            } catch (SQLException se) {
                AlertBox.error("SQL Fetching Error", "Unable to fetch selected data for editing.", se);
                se.printStackTrace();
            }
        }

        addBtn.setOnAction(event -> {
            int last_inserted_id = 1;
            textArea.clear();
            if (validate(textField4, textField0, textField, textField5, textField2, textField1, textField6)) {
                AlertBox.display("Error", "Please fill in all the marked values");
            } else if (validateFirstName(textField4) | validateSurName(textField0) | validateCity(textField5) | validatePostcode(textField2) | validateNumber(textField1) | validateEmail(textField6)) {
                AlertBox.display("Error","Please Fill in the fields correctly");
            } else {
                boolean completed = true;
                String type = "Private";
                if (businessRB.isSelected()) {
                    type = "Business";
                }
                try {
                    String insert;
                    if(edit){
                        insert = "UPDATE Customers SET Firstname = ?, FirstAddressLine = ?, SecondAddressLine = ?, City = ?, Postcode = ?, Number = ?, Email = ?, Type = ?, Surname = ? WHERE CustomerID =" + customerTable.getSelectionModel().getSelectedItem().getcolID();
                    }
                    else {
                        insert = "INSERT INTO Customers(Firstname,FirstAddressLine,SecondAddressLine,City,Postcode,Number,Email,Type,Surname) VALUES(?,?,?,?,?,?,?,?,?)";
                    }
                    PreparedStatement stmt = db.getCon().prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, textField4.getText());
                    stmt.setString(2, textField.getText());
                    stmt.setString(3, textField3.getText());
                    stmt.setString(4, textField5.getText());
                    stmt.setString(5, textField2.getText());
                    stmt.setString(6, textField1.getText());
                    stmt.setString(7, textField6.getText());
                    stmt.setString(8, type);
                    stmt.setString(9, textField0.getText());
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if(rs.next()) {
                        last_inserted_id = rs.getInt(1);
                    }

                } catch (SQLException se) {
                    se.printStackTrace();
                    AlertBox.error("Query Error", "Error inserting/updating new Customer", se);
                    completed = false;
                }
                if (completed) {
                    if(edit){
                        customerTable.getSelectionModel().clearSelection();
                        AlertBox.info("Success", "Update Successful", "Customer details have been updated");
                        loadCustomerData(2);
                        refreshOtherModules();
                        dialog.close();
                    }
                    else{
                        AlertBox.info("Success", "Add Successful", "Customer details have been added");
                        loadCustomerData(2);
                        refreshOtherModules();
                        dialog.close();
                        if (AlertBox.choice("Add Vehicle", "Add Vehicle for New Customer?", "Are you sure?")) {
                            addCustomerVehicle(last_inserted_id);
                        }
                    }
                }
            }
        });

        closeBtn.setOnAction(event -> {
                dialog.close();
        });

        minBtn.setOnAction(event -> {
            ((Stage)((Button)event.getSource()).getScene().getWindow()).setIconified(true);
        });

        cancelBtn.setOnAction(event -> {
                if (AlertBox.choice("Close form", "Are you sure?", "All fields will be disposed, nothing will be saved, Are you sure?"))
                    dialog.close();
            });

            Scene dialogScene = new Scene(anchorPane, 390, 543);
            dialog.setScene(dialogScene);
            dialog.show();

    }

    /**
     * Called when the delete button is pressed.
     * Deletes all records of a customer with a confirmation.
     */
    @FXML
    private void confirmDelete() {
        if (customerTable.getSelectionModel().getSelectedItem() != null) {
            boolean del = AlertBox.choice("Confirmation", "About to delete!", "Are you sure you wish to delete the currently selected Customer?");
            if (del) {
                int id = customerTable.getSelectionModel().getSelectedItem().getcolID();
                int index = customerTable.getSelectionModel().getSelectedIndex();
                customerTable.getSelectionModel().clearSelection(index);
                String delQuery = "DELETE FROM Customers WHERE CustomerID = ?";
                try (PreparedStatement stmt = db.getCon().prepareStatement(delQuery)) { //try-with-resources
                    stmt.setInt(1, id);
                    db.update("PRAGMA foreign_keys=ON");
                    stmt.executeUpdate();
                } catch (SQLException se) {
                    AlertBox.error("Error Deleting", "SQL Error occurred while deleting this selection", se);
                }
                loadCustomerData(2);
                refreshOtherModules();
            }
        } else {
            AlertBox.error("Error Selection", "Select A Customer Record!");
        }
    }

    /**
     * Refreshes the tables of the other modules in the system
     */
    private void refreshOtherModules(){
        VehiclesController vehiclesController = (VehiclesController) ScreenHandler.getController(Main.VEHICLE);
        vehiclesController.refresh();

        BookingController bookingController =(BookingController) ScreenHandler.getController(Main.BOOKINGS);
        bookingController.refreshTable();

        PartsController partsController = (PartsController) ScreenHandler.getController(Main.PARTS);
        partsController.refresh();
    }

    /**
     * Calls the add method in the vehicle module for a new customer to add a vehicle
     * @param key The newly added customer.
     */
    private void addCustomerVehicle (Integer key) {
        VehiclesController controller = (VehiclesController) ScreenHandler.getController(Main.VEHICLE);
        controller.add(key);

        loadCustomerData(2);
    }

    /**
     * Calls the addEdit method in the bookings module to allow a vehicle booking.
     */
    @FXML
    private void makeBooking() {
       try {
            if (customerTable.getSelectionModel().getSelectedItem().getcolVehReg() != null) {

                BookingController controller = (BookingController) ScreenHandler.getController(Main.BOOKINGS);
                controller.addEdit(false,
                        customerTable.getSelectionModel().getSelectedItem().getcolVehReg());

            } else AlertBox.error("Error Selection", "Select A Vehicle");
       }
       catch (NullPointerException e) {AlertBox.error("Error Selection", "Select A Vehicle");}
    }

    /**
     * Listener values for the customer table.
     * Updates labels with the customer information.
     * Updates labels with the vehicle information.
     * Updates the booking table with values.
     * @param id Customer id
     */
    @FXML
    private void updateDetails(Integer id){
        try {
            String customerQuery = "SELECT * FROM Customers WHERE CustomerID = " + id; // Retrieve Customer info
            ResultSet rsCustomer = db.query(customerQuery);

            String vehicleQuery = "SELECT * FROM Vehicle WHERE RegNumber = '" + customerTable.getSelectionModel().getSelectedItem().getcolVehReg() + "'";
            ResultSet rsVehicle = db.query(vehicleQuery);

            //setting customer label details
            if (customerTable.getSelectionModel().getSelectedItem().getcolID() != null) {
                custID.setText(rsCustomer.getString("CustomerID"));
                custFirstAdd.setText(rsCustomer.getString("FirstAddressLine"));
                custSecondAdd.setText(rsCustomer.getString("SecondAddressLine"));
                custCity.setText(rsCustomer.getString("City"));
                custPostcode.setText(rsCustomer.getString("Postcode"));
                custNumber.setText(rsCustomer.getString("Number"));
                custEmail.setText(rsCustomer.getString("Email"));
                custType.setText(rsCustomer.getString("Type"));
            }

            //setting vehicle label details
            if (customerTable.getSelectionModel().getSelectedItem().getcolVehReg() != null) {

                vehMoTdate.setText(rsVehicle.getString("MoTDate"));
                vehMileage.setText(rsVehicle.getString("Mileage"));
                vehModel.setText(rsVehicle.getString("Model"));
                vehFuelType.setText(rsVehicle.getString("FuelType"));
                if (rsVehicle.getInt("Warranty") == 1) vehWarranty.setText("Under Warranty");
                else vehWarranty.setText("No Warranty");
                vehColour.setText(rsVehicle.getString("Colour"));
                vehEngineSize.setText(rsVehicle.getString("EngineSize"));
                vehRegNumber.setText(rsVehicle.getString("RegNumber"));
                vehMake.setText(rsVehicle.getString("Make"));
                vehLastService.setText(rsVehicle.getString("LastService"));

                updateBookingTable(customerTable.getSelectionModel().getSelectedItem().getcolVehReg());
            }

        } catch (SQLException se) {
            AlertBox.error("Error","Error loading customer details", se);
            se.printStackTrace();
        }

    }

    /**
     * Listener values for the past and future booking tables.
     * Updates labels and text areas with booking information .
     * Updates parts table with values.
     * @param id booking id
     * @param time whether the booking is past or future listener
     */
    private void updateBookingDetails(Integer id, Integer time){
            try {
                bookFault.setWrapText(true);
                if (time == 1) {
                    bookingFutureTable.getSelectionModel().clearSelection();
                }
                else {
                    bookingPastTable.getSelectionModel().clearSelection();
                }

                String bookingQuery = "SELECT * FROM Bookings WHERE BookingID =" + id;
                ResultSet rsBooking = db.query(bookingQuery);

                if (id != null) {
                    bookID.setText(rsBooking.getString("BookingID"));
                    bookDate.setText(rsBooking.getString("BookingDate"));
                    bookFinishDate.setText(rsBooking.getString("FinishDate"));
                    bookFault.setText(rsBooking.getString("Fault"));
                    if (rsBooking.getString("BookingType").equals("1")) bookType.setText("Diagnosis");
                    else bookType.setText("Repair");

                    updatePartsTable(id);
                    calculateBill(id);
                }


            } catch (SQLException se) {
                AlertBox.error("Error", "Error loading booking details", se);
                se.printStackTrace();
            }
        }

    /**
     * Listener values for the parts table.
     * Updates labels and text areas with parts information.
     * @param partID part id
     * @param bookID booking id
     */
    private void updatePartDetails (Integer partID, Integer bookID) {
        try {
            parDescription.setWrapText(true);
            String partsQuery = "SELECT * FROM PartForRepair JOIN Parts ON PartForRepair.PartID = Parts.PartID WHERE PartForRepair.RepairID = '" + bookID + "' AND Parts.PartID = '" + partID + "'";
            ResultSet rsParts = db.query(partsQuery);

            parID.setText(rsParts.getString("PartID"));
            parInstallDate.setText(rsParts.getString("InstallDate"));
            parDescription.setText(rsParts.getString("Description"));
            parCost.setText(rsParts.getString("PartCost"));
            parWarExpDate.setText(rsParts.getString("ExpiryDate"));

        } catch (SQLException se) {
            AlertBox.error("Error", "Error loading part details", se);
            se.printStackTrace();
        }
    }

    /**
     * Filters customer table according to input in the search box.
     * Sets the customer table with data.
     * Adapted from code.makery.ch javafx course.
     * @param data initial dataList from the database
     */
   @FXML private void search(ObservableList<CustomerData> data) {
            FilteredList<CustomerData> filteredData = new FilteredList<>(data, p -> true);
            searchBox.setOnKeyReleased(e -> {
            searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(CustomerData -> {
                    // If filter text is empty, display all records.

                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        // Compare with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (CustomerData.getcolFirstname().toLowerCase().contains(lowerCaseFilter)) {
                            return true; // Filter matches first name.
                        } else if (CustomerData.getcolSurname().toLowerCase().contains(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        } else if (CustomerData.getcolVehReg() != null) {
                            if (CustomerData.getcolVehReg().toLowerCase().contains(lowerCaseFilter))
                                return true; // Filter matches veh reg.
                        }
                        return false; // Does not match.
                });
            });
            });
            SortedList<CustomerData> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(customerTable.comparatorProperty());
            customerTable.setItems(sortedData);
    }


    /**
     *The following validation methods are all validation for the add/edit method inputs.
     */
    private boolean validate(TextField firstName, TextField surName, TextField firstAddressLine,TextField city,TextField postcode,TextField number,TextField email){
        if (firstName.getText().isEmpty() | surName.getText().isEmpty() | firstAddressLine.getText().isEmpty()
                | city.getText().isEmpty() | postcode.getText().isEmpty() | number.getText().isEmpty()
                | email.getText().isEmpty())
            return true;
        else
            return false;
    }

    private boolean validateFirstName(TextField field){
        if (!field.getText().matches("[a-zA-Z]+"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid First Name"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    private boolean validateSurName(TextField field){
        if (!field.getText().matches("[a-zA-Z]+"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid Surname"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    private boolean validateCity(TextField field){
        if (!field.getText().matches("[a-zA-Z]+"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid City"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    private boolean validatePostcode(TextField field){
        if (!field.getText().matches("^([A-PR-UWYZa-pr-uwyza](([0-9](([0-9]|[A-HJKSTUWa-hjkstuw])?)?)|([A-HK-Ya-hk-y][0-9]([0-9]|[ABEHMNPRVWXYabehmnprvwxy])?)) ?[0-9][ABD-HJLNP-UW-Zabd-hjlnp-uw-z]{2})$"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid UK postcode"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    private boolean validateNumber(TextField field){
        if (!field.getText().matches("^[0-9]+$"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid Number"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    private boolean validateEmail(TextField field){
        if (!field.getText().matches("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+"))
        {
            String text = textArea.getText();
            text = text + "Please fill in a valid email"+" \n";
            textArea.setText(text);
            return true;
        }
        else
            return false;
    }

    /**
     * Associates all columns in the TableView to their relevant variables in the wrapper class (CustomerData)
     */
    private void setFields(){

        colFirstname.setCellValueFactory(new PropertyValueFactory<>("colFirstname")); // same as in customer data variables
        colSurname.setCellValueFactory(new PropertyValueFactory<>("colSurname"));;
        colID.setCellValueFactory(new PropertyValueFactory<>("colID"));
        colVehReg.setCellValueFactory(new PropertyValueFactory<>("colVehReg"));
        colType.setCellValueFactory(new PropertyValueFactory<>("colType"));

        colPastDate.setCellValueFactory(new PropertyValueFactory<>("colDate"));
        colPastBookID.setCellValueFactory(new PropertyValueFactory<>("colBookID"));

        colFutureBookID.setCellValueFactory(new PropertyValueFactory<>("colBookID"));
        colFutureDate.setCellValueFactory(new PropertyValueFactory<>("colDate"));

        colPartID.setCellValueFactory(new PropertyValueFactory<>("colPartID"));
        colPartName.setCellValueFactory(new PropertyValueFactory<>("colPartName"));

    }

    /**
     * Queries the database to get customer data for the customer table
     * @param type filters the data set by type (both, business, private)
     */
    public void loadCustomerData(int type){
        ObservableList<CustomerData> dataList = FXCollections.observableArrayList();
        String query = "";
        if (type == 2 ){//business and private
            typeCB.getSelectionModel().select("Both");
            query = "SELECT Customers.Type, Customers.Firstname, Customers.Surname, Customers.CustomerID, Vehicle.RegNumber FROM Customers LEFT JOIN Vehicle ON Customers.CustomerID = Vehicle.CustomerID";
        }
        else if (type == 1){//business
            query = "SELECT Customers.Type, Customers.Firstname, Customers.Surname, Customers.CustomerID, Vehicle.RegNumber FROM Customers LEFT JOIN Vehicle ON Customers.CustomerID = Vehicle.CustomerID WHERE Customers.Type = 'Business'";
        }
        else if (type == 0){//private
            query = "SELECT Customers.Type, Customers.Firstname, Customers.Surname, Customers.CustomerID, Vehicle.RegNumber FROM Customers LEFT JOIN Vehicle ON Customers.CustomerID = Vehicle.CustomerID WHERE Customers.Type = 'Private'";
        }
        ResultSet rs = db.query(query);
        try{
            while (rs.next())
                dataList.add(new CustomerData(rs));
        } catch (SQLException se){
            AlertBox.error("loading","Customer table failed");
            se.printStackTrace();
        }
        search(dataList);
    }

    /**
     * Queries the database to get booking data for the past and future booking tables.
     * @param reg vehicle reg of selected record
     */
    private void updateBookingTable(String reg)
    {
        bookID.setText("");
        bookDate.setText("");
        bookFinishDate.setText("");
        bookFault.setText("");
        bookType.setText("");
        billStatus.setText("");
        billTotal.setText("");
        btnPaid.setVisible(false);
        ObservableList<CustomerData> dataPastList = FXCollections.observableArrayList();
        ObservableList<CustomerData> dataFutureList = FXCollections.observableArrayList();
        String query = "SELECT BookingDate, BookingID FROM Bookings WHERE RegNumber = '" + reg + "'";
        ResultSet rs = db.query(query);
        LocalDate date;
        try {
            while (rs.next()) {
                date = LocalDate.parse(rs.getString("BookingDate"));
                if (date.isAfter(LocalDate.now())) {
                    dataFutureList.add(new CustomerData(rs, "Booking RS"));
                } else {
                    dataPastList.add(new CustomerData(rs, "Booking RS"));
                }
            }
            } catch (SQLException se) {
            AlertBox.error("SQL Error", "SQL failed", se);
        }
        bookingPastTable.setItems(dataPastList);
        bookingFutureTable.setItems(dataFutureList);
    }

    /**
     * Queries the database to get parts data for the parts table that relates to the booking id.
     * @param id booking/repair id for the parts
     */
    private void updatePartsTable(Integer id)
    {
        parID.setText("");
        parInstallDate.setText("");
        parWarExpDate.setText("");
        parCost.setText("");
        parDescription.setText("");
        ObservableList<CustomerData> dataList = FXCollections.observableArrayList();
        String query = "SELECT Parts.PartName, PartForRepair.PartID FROM PartForRepair JOIN Parts ON PartForRepair.PartID = Parts.PartID WHERE PartForRepair.RepairID = '" + id + "'";
        ResultSet rs = db.query(query);
        try {
            while (rs.next())
                dataList.add(new CustomerData(rs, "Parts ","RS" ));
        } catch (SQLException se) {
            AlertBox.error("SQL Error", "SQL failed", se);
        }
        partsTable.setItems(dataList);
    }

    /**
     * Called when the pay button is pressed in the billing section (It will be visible when it is relevant)
     * Sets a booking as paid in the accounts table in the database
     * @param id booking id
     */
    private void setPaid(int id)
    {
        try {
            String insert = "UPDATE Account SET PaymentStatus = ? WHERE BookingID =" + id;
            PreparedStatement stmt = db.getCon().prepareStatement(insert);
            stmt.setInt(1, 1);
            stmt.executeUpdate();
            calculateBill(id);
        } catch (SQLException se) {
            se.printStackTrace();
            AlertBox.error("Query Error", "Error Updating paid", se);
        }

    }

    /**
     * Calculated bill information for a selected booking
     * @param id booking id
     */
    private void calculateBill(int id) {

        String billPartsQuery = "SELECT * FROM PartForRepair INNER JOIN Parts ON PartForRepair.PartID = Parts.PartID WHERE PartForRepair.RepairID = " + id;
        ResultSet rsPartsBill = db.query(billPartsQuery);
        billStatus.setWrapText(true);
        double bill=0 ;
        double rate=0;
        double hours=0;
        try {
            while (rsPartsBill.next()) {
                bill = bill + rsPartsBill.getDouble("PartCost");
            }

        } catch (SQLException se) {
            AlertBox.error("Error", "Error loading parts total", se);
            se.printStackTrace();
        }
        String billMechanicQuery = "SELECT Rate, TotalTime FROM Bookings JOIN Mechanics ON Bookings.EmployeeID = Mechanics.EmployeeID WHERE Bookings.BookingID = " + id;
        ResultSet rsMechanicBill = db.query(billMechanicQuery);
        try {
            while (rsMechanicBill.next()) {
                rate = rsMechanicBill.getDouble("Rate");
                hours = rsMechanicBill.getDouble("TotalTime");
                bill = bill+(rate*hours);
            }

        } catch (SQLException se) {
            AlertBox.error("Error", "Error loading mechanic total", se);
            se.printStackTrace();
        }

        //add cost of parts spc if present
        try {
            String spcPartqry = "SELECT Cost FROM SpecialRepairsParts WHERE BookingID = " + id;
            ResultSet rsSpcPart = db.query(spcPartqry);
            while (rsSpcPart.next()) {
                bill = bill + rsSpcPart.getDouble("Cost");
            }
            //Add cost of vehicle spc if present
            String VehicleSpcQry = "SELECT Cost FROM SpecialRepairVehicle WHERE BookingID = " + id;
            ResultSet rsSPCVehicle = db.query(VehicleSpcQry);
            while (rsSPCVehicle.next()) {
                bill = bill + rsSPCVehicle.getDouble("Cost");
            }
        }
        catch (SQLException se) {
            AlertBox.error("Error", "Error loading spc total", se);
            se.printStackTrace();
        }
        billTotal.setText("£" + new DecimalFormat("#.##").format(bill));

        btnPaid.setVisible(false);

        Integer warranty = 0;
        String warrantyQuery = "SELECT Bookings.RegNumber, Vehicle.Warranty FROM Bookings INNER JOIN Vehicle ON Bookings.RegNumber = Vehicle.RegNumber WHERE Bookings.BookingID = " + id;
        try {
            ResultSet rsWarranty = db.query(warrantyQuery);
            warranty = rsWarranty.getInt("Warranty");
        } catch (SQLException se) {
            AlertBox.error("Error", "Error loading bill warranty", se);
            se.printStackTrace();
        }
            int status = 1;
            Integer payment = 0;
            String paymentQuery = "SELECT PaymentStatus FROM Account WHERE BookingID = " + id;
            try {
                ResultSet rsAccount = db.query(paymentQuery);
                payment = rsAccount.getInt("PaymentStatus");

            } catch (SQLException se) {
                LocalDate finish = LocalDate.parse((bookFinishDate.getText()));
                if (finish.isAfter(LocalDate.now())){
                    billStatus.setText("Incomplete");
                }
               else {
                    billStatus.setText("Incomplete Delayed");
                }
               status = 0;
            }

        Integer bookingType = 1; // 1 = diagnosis

        String billTypeQuery = "SELECT BookingType FROM Bookings WHERE Bookings.BookingID = " + id;
        ResultSet rsTypeBill = db.query(billTypeQuery);
        try{
            bookingType = rsTypeBill.getInt("BookingType");
        }
        catch (SQLException se) {
            AlertBox.error("Error", "Error loading bill type", se);
            se.printStackTrace();
        }

        if (bookingType == 1) {
            LocalDate finish = LocalDate.parse((bookFinishDate.getText()));
            if (finish.isAfter(LocalDate.now())){
            billStatus.setText("Incomplete Diagnosis Booking");
            }
            else {
                billStatus.setText("Complete Diagnosis Booking No Charge");
            }
        }
        else {
            if (status == 1) {
                if (warranty == 1) {
                    if (payment == 0) {
                        billStatus.setText("No Bill");
                        if (!billTotal.getText().equals("£0")) billStatus.setText("Unpaid Warranty");
                    } else billStatus.setText("Paid Warranty");
                } else {
                    if (payment == 0) {
                        billStatus.setText("No Bill");
                        if (!billTotal.getText().equals("£0")) {
                            billStatus.setText("Unpaid No Warranty");
                            btnPaid.setVisible(true);
                            btnPaid.setOnAction(event -> {
                                if (AlertBox.choice("Pay", "Confirm Payment of this booking?", "Are you sure?")) {
                                    setPaid(id);
                                }
                            });
                        }
                    } else billStatus.setText("Paid No Warranty");
                }
            }
        }
    }


    /**
     * The following methods relate to the navigation menu (menu bar)
     * Allows navigation to other modules.
     */
    @FXML private void logout(){ Menu.logout(sh); }

    @FXML private void goToUP(){ Menu.toUP(sh); }

    @FXML private void goToBookings(){ Menu.toBookings(sh);}

    private void goToVeh(){ Menu.toVeh(sh); }

    private void goToParts(){ Menu.toParts(sh); }

    private void goToSPC() { Menu.toSPC(); }

    @Override public void setScreenParent(ScreenHandler currentScreen) {
        sh = currentScreen;
    }

}
