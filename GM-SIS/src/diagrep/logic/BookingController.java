package diagrep.logic;

import UI.Menu;
import UI.ScreenHandler;
import UI.ScreenMaster;
import Vehicles.Logic.VehiclesController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import common.Database;
import common.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import misc.AlertBox;
import parts.PartsController;
import users.Mechanic;

import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Prodige on 19/01/2017.
 *
 * Controller for the Booking Module for GM-SIS. Vehicles bookings can be checked, added and modified here.
 * Handles the behaviour of the UI and components. Associated with the {@code ScreenMaster} GUI-Framework.
 *
 * @author Prodige.
 */
public class BookingController implements Initializable, ScreenMaster {

    @FXML
    private Button closeBtn;
    @FXML
    private Button minBtn;
    @FXML
    private Label title;
    @FXML
    private Button navCust;
    @FXML
    private Button navVech;
    @FXML
    private Button navParts;
    @FXML
    private BorderPane draggable;
    @FXML
    private Button btnReset;
    @FXML
    private ListView<String> dateList;// = new ListView<>();

    //Searching shizzle
    @FXML
    private TextField searchBox;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnEdit;
    @FXML
    private ChoiceBox<String> searchCB;

    //Table View & Shizzle
    @FXML
    private TableView<Data> bookingTable;
    @FXML
    private TableColumn<Data, Integer> colID;
    @FXML
    private TableColumn<Data, String> colReg;
    @FXML
    private TableColumn<Data, String> colDate;
    @FXML
    private TableColumn<Data, String> colCustID;
    @FXML
    private TableColumn<Data, String> colFName;
    @FXML
    private TableColumn<Data, String> colSurname;
    @FXML
    private TableColumn<Data, Integer> colMechID;
    @FXML
    private TableColumn<Data, Time> colTime;
    @FXML
    private Button sRep;

    //Details Data Labels
    @FXML
    private Label bookID;
    @FXML
    private Label regNum;
    @FXML
    private Label bookType;
    @FXML
    private Label nextBookDate;
    @FXML
    private Label timeLbl;
    @FXML
    private Label warranty;
    @FXML
    private Label mileage;
    @FXML
    private Label mechanic;
    @FXML
    private Label rate;
    @FXML
    private Label vehType;
    @FXML
    private Button btnDelete;

    //Cust. Details Data Labels
    @FXML
    private Label custID;
    @FXML
    private Label firstname;
    @FXML
    private Label surname;
    @FXML
    private Label address1;
    @FXML
    private Label address2;
    @FXML
    private Label postcode;
    @FXML
    private Label email;

    //Vech. Details Data Labels
    @FXML
    private Label reg;
    @FXML
    private Label make;
    @FXML
    private Label model;
    @FXML
    private Label engSize;
    @FXML
    private Label servDate;
    @FXML
    private Label MOT;
    @FXML
    private Label fuelType;

    //Fault Details Data Labels
    @FXML
    private Label faultStatus;
    @FXML
    private TextArea faultList;
    @FXML
    private Label finDateLbl;
    @FXML
    private Button changeRepStatus;

    //Rep Details
    @FXML
    private Tab repInfoTab;
    @FXML
    private Label estFinDate;
    @FXML
    private ListView<String> partsReqLV;
    @FXML
    private Button conToSPC;

    private ScreenHandler sh;
    private Database db;

    private LocalDate selectedBookDate;
    private LocalDate selectedFinishDate;
    private LocalTime selectedTime;
    private ArrayList<String> holidayDates;

    /**
     * All important nodes (e.g. TableView and ListView) are loaded immediately and populated with values.
     * All important listeners are added to the required nodes.
     * This method is invoked on load of the FXML.
     *
     * @param location implicitly called when FXML Loads
     * @param resources implicitly called when FXML Loads
     */
    @SuppressWarnings("unchecked")
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        Menu.loadTopBars(title, closeBtn, minBtn, draggable);
        ObservableList<String> searchChoices = FXCollections.observableArrayList(
                "Vehicle Registration Number",
                "Customer First name",
                "Customer Surname",
                "Vehicle Manufacturer"
        );
        searchCB.setItems(searchChoices);

        // Loading TableView with data
        setFields();
        bookingTable.setItems(loadData());

        // Add Listener to update fields depending on the selected item
        bookingTable.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.intValue() >= 0) {
                        updateDetails(bookingTable.getSelectionModel().getSelectedItem().getBookingID());
                        btnDelete.setDisable(false);
                        btnEdit.setDisable(false);
                    } else {
                        btnEdit.setDisable(true);
                        btnDelete.setDisable(true);
                    }
                }
        );

        // Add Listen to get ChoiceBox Changes
        searchCB.getSelectionModel().selectedIndexProperty().addListener(
                ((observable, oldValue, newValue) -> {
                    int choice = searchCB.getSelectionModel().getSelectedIndex();
                    searchBox.setDisable(false);
                    //System.out.println(choice);
                    switch (choice) {
                        case 0:
                            searchBox.setPromptText("Enter Vehicle Registration Number");
                            btnSearch.setOnAction(event -> search("RegNumber"));
                            break;
                        case 1:
                            searchBox.setPromptText("Enter Customer First name");
                            btnSearch.setOnAction(event -> search("Firstname"));
                            break;
                        case 2:
                            searchBox.setPromptText("Enter Customer Surname");
                            btnSearch.setOnAction(event -> search("Surname"));
                            break;
                        case 3:
                            searchBox.setPromptText("Enter Vehicle Make");
                            btnSearch.setOnAction(event -> search("Make"));
                            break;
                    }
                })
        );

        // Get all the Bank and Public Holidays saved in  holidays.txt
        try {
            InputStream in = getClass().getResourceAsStream("holidays.txt");
            BufferedReader scan = new BufferedReader(new InputStreamReader(in));
            holidayDates = new ArrayList<>();
            String line;
            while ((line = scan.readLine()) != null) {
                holidayDates.add(line.split(",")[0]);
            }
            //System.out.println(holidayDates);
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        navCust.setOnAction(e -> goToCust());
        navVech.setOnAction(e -> goToVeh());
        navParts.setOnAction(e -> goToParts());
        sRep.setOnAction(e -> goToSPC());
        conToSPC.setOnAction(e -> goToSPC());
    }

    @FXML
    private void logout() {
        Menu.logout(sh);
    }

    @FXML
    private void goToUP() {
        Menu.toUP(sh);
    }

    private void goToCust(){ Menu.toCust(sh); }

    private void goToVeh() { Menu.toVeh(sh); }

    private void goToParts(){ Menu.toParts(sh); }

    private void goToSPC() { Menu.toSPC(); }

    @FXML
    private void addNew() {
        addEdit(false, null);
    }

    @FXML
    private void editSelected() {
        addEdit(true, null);
    }

    /**
     * From the methods {@code addNew() editSelected()} this method is told whether the user
     * is adding a new booking or editing an existing booking. A GUI is loaded with all the relevant
     * fields to either add or edit a booking.
     * Fields are validated before successful confirmation.
     * On confirmation the global connection to the database is
     * used to update all the details from the fields.
     *
     * @param editing Checks whether the user is editing or adding a new booking.
     * @param vehReg When being called by a foreign controller to create a booking for a specified vehicle
     */
    @FXML
    public void addEdit(boolean editing, String vehReg) {

        //Nodes
        HBox hb = new HBox();
        ChoiceBox<String> regCB;
        ChoiceBox<Integer> mechList;
        ChoiceBox<String> custListCB;
        RadioButton diagRB;
        RadioButton repRB;

        TextField milesTF = new TextField();
        DatePicker bookDP = new DatePicker();
        DatePicker finishDP = new DatePicker();
        ListView<String> parts4Rep = new ListView<>();
        JFXButton add = new JFXButton();
        JFXButton cancel = new JFXButton();
        TextArea faultsNotes = new TextArea();

        JFXDatePicker timePicker = new JFXDatePicker();
        finishDP.setDisable(true);
        timePicker.setShowTime(true);
        timePicker.setDisable(true);
        parts4Rep.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        parts4Rep.setDisable(true);

        //Setting up - Button appearances
        add.setButtonType(JFXButton.ButtonType.RAISED);
        add.setPrefSize(120, 25);
        if (editing) add.setText("Update Booking");
        else add.setText("Add new Booking");
        add.setTextFill(Paint.valueOf("White"));
        cancel.setButtonType(JFXButton.ButtonType.RAISED);
        cancel.setPrefSize(120, 25);
        cancel.setText("Cancel");
        cancel.setTextFill(Paint.valueOf("White"));

        //Setting up - Stage for popup
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); //Cant interact with the main app without closing the popup
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initOwner(Main.stage);
        VBox dialogVbox = new VBox(15);
        dialogVbox.setPadding(new Insets(10, 0, 0, 20));
        dialogVbox.setStyle("-fx-background-color:\n" +
                "linear-gradient(to bottom, #78b4ff 0%, #2a68ff 100%);");
        ObservableList<String> custList = FXCollections.observableArrayList();
        ResultSet custListRs = db.query("SELECT CustomerID FROM Customers");
        try {
            custList.add("");
            while (custListRs.next())
                custList.add(custListRs.getString(1));
            //regRs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        custListCB = new ChoiceBox<>(custList);
        hb.getChildren().addAll(new Label("Customer ID (Optional): "), custListCB,
                new Label(" Choose to filter Vehicle Reg list, select empty to view all."));
        dialogVbox.getChildren().add(hb);

        //Set the controls
        ObservableList<String> regList = FXCollections.observableArrayList();
        String getAllReg = "SELECT RegNumber FROM Vehicle";
        ResultSet regRs = db.query(getAllReg);
        try {
            while (regRs.next())
                regList.add(regRs.getString(1));
            //regRs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        regCB = new ChoiceBox<>(regList); //Vech Reg
        custListCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> filteredRegList = FXCollections.observableArrayList();
                String getReg = "SELECT RegNumber FROM Vehicle WHERE CustomerID = " + newValue;
                //+custListCB.getSelectionModel().getSelectedItem();
                if (custListCB.getSelectionModel().getSelectedItem().isEmpty())
                    getReg = "SELECT RegNumber FROM Vehicle";
                //System.out.println(custListCB.getSelectionModel().getSelectedItem());
                ResultSet custRegRS = db.query(getReg);
                try {
                    while (custRegRS.next())
                        filteredRegList.add(custRegRS.getString(1));
                    //regRs.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
                regCB.setItems(filteredRegList);
            }
        });
        hb = new HBox(10);
        hb.getChildren().addAll(new Label("Vehicle Registration Number*: "), regCB); //Assume the Reg must be in the Vech DB
        dialogVbox.getChildren().add(hb);

        if (vehReg != null) // customer accounts
        {
            regCB.getSelectionModel().select(vehReg);
        }

        final ToggleGroup togGroup = new ToggleGroup(); //Booking Type
        diagRB = new RadioButton("Diagnosis");
        diagRB.setToggleGroup(togGroup);
        diagRB.setSelected(true);
        repRB = new RadioButton("Repair");
        repRB.setToggleGroup(togGroup);
        togGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (repRB.isSelected())
                        parts4Rep.setDisable(false);
                    else
                        parts4Rep.setDisable(true);
                });
        hb = new HBox(10);
        hb.getChildren().addAll(new Label("Booking Type*: "), diagRB, repRB);
        dialogVbox.getChildren().add(hb);

        ObservableList<Integer> mList = FXCollections.observableArrayList(); //Mechanic
        String getAllMech = "SELECT EmployeeID FROM Mechanics";
        ResultSet mechRS = db.query(getAllMech);
        try {
            while (mechRS.next())
                mList.add(mechRS.getInt(1));
        } catch (SQLException se) {
            se.printStackTrace();
        }
        mechList = new ChoiceBox<>(mList);
        mechList.setOnAction(e -> {
            if (bookDP.getValue() != null) timePicker.setDisable(false);
        });
        hb = new HBox(5);
        hb.getChildren().addAll(new Label("Choose Mechanic's ID*: "), mechList);
        dialogVbox.getChildren().add(hb);

        hb = new HBox(5); //Mileage
        hb.getChildren().addAll(new Label("Current Mileage on Vehicle*: "), milesTF);
        dialogVbox.getChildren().add(hb);

        //Handling Sundays, Past days and Bank holidays (Requirement 5)
        Callback<DatePicker, DateCell> cellFactory = dp -> new DateCell() {
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                boolean match = false;
                for (String holidayDate : holidayDates) {
                    LocalDate ld = LocalDate.parse(holidayDate, DateTimeFormatter.ofPattern("d/MM/yyyy"));
                    if (item.isEqual(ld)) match = true;
                }
                if (item.isBefore(LocalDate.now()) || item.getDayOfWeek() == DayOfWeek.SUNDAY || match) {
                    setDisable(true);
                    setStyle("-fx-background-color: #848383;");
                }
            }
        };
        bookDP.setEditable(false);
        bookDP.setDayCellFactory(cellFactory); //Booking Date
        bookDP.setOnAction(event -> {
            selectedBookDate = bookDP.getValue();
            if (mechList.getValue() != null && selectedBookDate != null) timePicker.setDisable(false);
            if(selectedBookDate != null) finishDP.setDisable(false);
            //if (togGroup.getSelectedToggle().equals(repRB)) finishDP.setDisable(false);
        });
        hb = new HBox(8);
        hb.getChildren().addAll(new Label("Enter Appointment Date*: "), bookDP);
        dialogVbox.getChildren().addAll(hb);//Assumed your car will be checked the same booked day & time appointed

        //Handling Sundays, Past days and Bank holidays (Requirement 5)
        Callback<DatePicker, DateCell> finishCellFactory = fin -> new DateCell() {
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                boolean match = false;
                for (String holidayDate : holidayDates) {
                    LocalDate ld = LocalDate.parse(holidayDate, DateTimeFormatter.ofPattern("d/MM/yyyy"));
                    if (ld.isAfter(selectedBookDate) && item.isEqual(ld)) match = true;
                }
                if (item.isBefore(selectedBookDate) || item.getDayOfWeek() == DayOfWeek.SUNDAY || match) {
                    setDisable(true);
                    setStyle("-fx-background-color: #848383;");
                }
            }
        };
        finishDP.setEditable(false);
        finishDP.setOnAction(event -> selectedFinishDate = finishDP.getValue()); // Exp Finish date
        finishDP.setDayCellFactory(finishCellFactory);
        hb = new HBox(8);
        //hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(new Label("Enter Expected Finish Date*: "), finishDP);
        dialogVbox.getChildren().addAll(hb); //Assumed your car will be completed at a given date after booking.

        //Assume diagnosis slots are only 30minutes long
        timePicker.setEditable(false);
        timePicker.setTime(LocalTime.of(9, 0)); //Booking time
        selectedTime = timePicker.getTime();
        handleError(); //Catches API Error - Not my fault
        timePicker.setOnHiding(event -> {
            try { //Assume all bookings are made latest 30minutes before closing.
                selectedTime = timePicker.getTime();
                //System.out.println(selectedTime);
                if (selectedBookDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                        (selectedTime.isAfter(LocalTime.of(17, 0)) || selectedTime.isBefore(LocalTime.of(9, 0)))) {
                    timePicker.setTime(LocalTime.of(9, 0));
                    AlertBox.display("Invalid Time", "The time '" + selectedTime + "' is invalid!",
                            "The garage is only open between 9am - 5:30pm on weekdays. Last booking" +
                                    " can be made at 5:00pm (17:00)");
                } else if (selectedBookDate.getDayOfWeek() == DayOfWeek.SATURDAY &&
                        (selectedTime.isAfter(LocalTime.of(11, 30)) || selectedTime.isBefore(LocalTime.of(9, 0)))) {
                    timePicker.setTime(LocalTime.of(9, 0));
                    AlertBox.display("Invalid Time", "The time '" + selectedTime + "' is invalid!",
                            "The garage is only opened between 9am - 12pm (noon) on Saturdays. Last booking " +
                                    "can be made at 11:30am");
                }

                if(mechList.getValue() != null){
                    if (!Mechanic.isAvailable(selectedBookDate, selectedTime, mechList.getValue())) {
                        timePicker.setTime(LocalTime.of(9, 0));
                        mechList.setValue(null);
                        timePicker.setDisable(true);
                        AlertBox.display("Mechanic Unavailable",
                                "The chosen mechanic is unavailable on the time of the day selected",
                                "Please either change the chosen mechanic or change the date and/ or time of the booking");
                    }
                }
            } catch (Exception e) {
                timePicker.setTime(LocalTime.of(9, 0));
                e.printStackTrace();
                popError("Error caught while choosing date!", e);
                //Just to catch the known error in the API - not my fault
            }
        });
        timePicker.setDefaultColor(Paint.valueOf("Orange"));
        hb = new HBox(8);
        hb.getChildren().addAll(new Label("Appointment Time*: "), timePicker);
        dialogVbox.getChildren().addAll(hb);

        ListView<String> selected = new ListView<>();
        ArrayList<String> partsData = new ArrayList<>();
        parts4Rep.setPrefSize(280, 140); //Parts List
        parts4Rep.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selected.setItems(parts4Rep.getSelectionModel().getSelectedItems()));
        try {
            String partSQL = "SELECT * FROM Parts";
            ResultSet partRs = db.query(partSQL);
            while (partRs.next())
                partsData.add("ID: " + partRs.getInt("PartID") + " - Name: " + partRs.getString("PartName"));
            parts4Rep.setItems(FXCollections.observableArrayList(partsData));
        } catch (SQLException se) {
            se.printStackTrace();
            popError("SQL Error", "Error gathering Parts Data", se);
        }
        hb = new HBox(8);
        hb.getChildren().addAll(new Label("Select parts for repairs: "), parts4Rep, new Label("Hold Ctrl and Click to select\nmultiple parts. " +
                "These parts will\nnot be automatically ordered.\nRefer to Parts section."));
        dialogVbox.getChildren().add(hb);

        faultsNotes.setPrefSize(280, 140);
        faultsNotes.setWrapText(true);
        hb = new HBox(8);
        hb.getChildren().addAll(new Label("Notes on faults found: "), faultsNotes);
        dialogVbox.getChildren().add(hb);

        //Load existing values if the edit button was clicked
        if (editing) {
            Data selectedData = bookingTable.getSelectionModel().getSelectedItem();
            int selectedBID = selectedData.getBookingID();
            String getSelectedBooking = "SELECT * FROM Bookings WHERE BookingID =" + selectedBID;
            ResultSet selectedBookingRS = db.query(getSelectedBooking);
            try {
                selectedBookingRS.next();
                regCB.setValue(selectedBookingRS.getString("RegNumber"));
                regCB.setDisable(true);
                custListCB.setDisable(true);
                if (!selectedBookingRS.getBoolean("BookingType")) {
                    repRB.setSelected(true);
                    //finishDP.setDisable(false);
                    parts4Rep.setDisable(false);
                } else diagRB.setSelected(true);
                mechList.setValue(selectedBookingRS.getInt("EmployeeID"));
                milesTF.setText(selectedBookingRS.getString("CurrentMileage"));
                bookDP.setValue(LocalDate.parse(selectedBookingRS.getString("BookingDate")));
                finishDP.setValue(LocalDate.parse(selectedBookingRS.getString("FinishDate")));
                timePicker.setDisable(false);
                timePicker.setTime(LocalTime.parse(selectedBookingRS.getString("BookingTime")));
                faultsNotes.setText(selectedBookingRS.getString("Fault"));
                selectedBookDate = bookDP.getValue();
                selectedFinishDate = finishDP.getValue();
                selectedTime = timePicker.getTime();
            } catch (SQLException se) {
                popError("SQL Fetching Error", "Unable to fetch selected data for editing.", se);
                se.printStackTrace();
            }
        }

        add.setStyle("-fx-background-color: #52b9e5");
        cancel.setStyle("-fx-background-color: #52b9e5");
        //Handling add button -> adding all entries to DB
        add.setOnAction(event -> {
            try {
                String reg = regCB.getSelectionModel().getSelectedItem();
                if (reg == null) {
                    popError("Not all fields filled",
                            "Please select a Vehicle Registration number!");
                    regCB.requestFocus();
                    return;
                }
                //System.out.println(reg);
                String compMiles = "SELECT Mileage, CustomerID FROM Vehicle WHERE RegNumber = '" + reg + "'";
                ResultSet rs = db.query(compMiles);
                int cMiles = 0;
                int custID = 0;
                boolean success = false;
                try {
                    cMiles = Integer.parseInt(milesTF.getText());
                    rs.next();
                    if (cMiles < rs.getInt("Mileage")) {
                        popError("Invalid Mileage Input!",
                                "Current Mileage cannot be lower than Mileage registered in the system for this vehicle");
                        milesTF.requestFocus();
                    } else {
                        custID = rs.getInt("CustomerID");
                        success = true;
                    }
                } catch (SQLException se) {
                    popError("Query Error", "Unable to query Vehicles table!", se);
                } catch (NumberFormatException ne) { //Assume only numerical values allowed for mileage
                    popError("Invalid Input!",
                            "Please only enter numerical values (0 - 9) for the Current Mileage", ne);
                    milesTF.clear();
                    milesTF.requestFocus();
                    return;
                }
                if (!success) return;

                int type = 1;
                if (togGroup.getSelectedToggle().equals(repRB)) type = 0;
                int mechID = mechList.getValue();
                String btime = selectedTime.toString();

                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
                if (bookDP.getValue() == null || finishDP.getValue() == null) {
                    popError("Missing date(s)", "Please check Booking & Finish date inputs. Currently empty.");
                    return;
                }
                String bookDate = selectedBookDate.toString();
                String finDate = selectedFinishDate.toString();
                String faultDetail = faultsNotes.getText();
                if (bookDate.matches(".*[a-zA-Z].*") || finDate.matches(".*[a-zA-Z].*")) {
                    popError("Wrong Date Format", "Please make sure the dates are valid. (Check for " +
                            "unexpected characters.");
                    return;
                }
                /*if (!AlertBox.choice("Add Booking", "Are you sure?"
                        , "Are you sure you wish to add this booking?")) return; */
                try {
                    if (type == 0) {
                        ResultSet getSeq = db.query("SELECT seq FROM sqlite_sequence WHERE name = 'Bookings'");
                        getSeq.next();
                        int curSeq = Integer.parseInt(getSeq.getString(1)) + 1;
                        int selectedSize = selected.getItems().size();
                        if (editing) {
                            curSeq = bookingTable.getSelectionModel().getSelectedItem().getBookingID();
                            db.update("DELETE FROM RepairAndParts WHERE BookingID = " + curSeq);
                        }

                        for (int items = 0; items < selectedSize; items++) {
                            selected.getSelectionModel().select(items);
                            String item = selected.getSelectionModel().getSelectedItem();
                            item = item.split(" ")[1];
                            db.update("INSERT INTO RepairAndParts(BookingID, PartID) " +
                                    "VALUES('" + curSeq + "','" + item + "')");
                        }
                    }
                    String insert;
                    if (!editing)
                        insert = "INSERT INTO Bookings(BookingType,EmployeeID,BookingTime,FinishDate,CurrentMileage," +
                                "CustomerID,RegNumber,BookingDate,Fault,TotalTime) VALUES(?,?,?,?,?,?,?,?,?,?)";
                    else
                        insert = "UPDATE Bookings SET BookingType = ?, EmployeeID = ?, BookingTime = ?, FinishDate = ?, " +
                                " CurrentMileage = ?, CustomerID = ?, RegNumber = ?, BookingDate = ?, Fault = ?" +
                                " WHERE BookingID = " + bookingTable.getSelectionModel().getSelectedItem().getBookingID();
                    PreparedStatement stmt = db.getCon().prepareStatement(insert);
                    stmt.setInt(1, type);
                    stmt.setInt(2, mechID);
                    stmt.setString(3, btime);
                    stmt.setString(4, finDate);
                    stmt.setInt(5, cMiles);
                    stmt.setInt(6, custID);
                    stmt.setString(7, reg);
                    stmt.setString(8, bookDate);
                    stmt.setString(9, faultDetail);
                    if (!editing) stmt.setInt(10, 0);
                    stmt.executeUpdate();
                } catch (SQLException se) {
                    se.printStackTrace();
                    if (!editing) popError("SQL Query Error", "Error inserting new Booking", se);
                    else popError("SQL Query Error", "Error updating existing Booking", se);
                }
                if (editing) {
                    bookingTable.getSelectionModel().clearSelection();
                    AlertBox.info("Success", "Update Successful",
                            "Booking details have been updated Successfully");

                }
                if (editing) clearFields();
                refreshTable();
                PartsController partsController = (PartsController) ScreenHandler.getController(Main.PARTS);
                partsController.refresh();
                dialog.close();

            } catch (Exception e) {
                e.printStackTrace();
                popError("Invalid fields", "Please fill in all the marked fields correctly.");
            }
        });
        cancel.setOnAction(event -> {
            if (AlertBox.choice("Close form", "Are you sure?"
                    , "All fields will be disposed, nothing will be saved. Are you sure?"))
                dialog.close();
        });
        hb = new HBox(30);
        hb.getChildren().addAll(add, cancel);
        dialogVbox.getChildren().add(hb);

        dialogVbox.getChildren().add(new Label("Please fill in all fields marked with an asterisk( * )."));
        //Set the scene
        Scene dialogScene = new Scene(dialogVbox, 620, 650);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * This method is invoked by the delete button.
     * Sends a confirmation to the user, if confirmed the currently selected row from
     * the TableView is retrieved & deleted and associated information from the database is
     * also deleted.
     */
    @FXML
    private void confirmDelete() {
        boolean del = AlertBox.choice("Confirmation", "About to delete!",
                "Are you sure you wish to delete the currently selected booking?");
        if (del) {
            int id = bookingTable.getSelectionModel().getSelectedItem().getBookingID();
            int index = bookingTable.getSelectionModel().getSelectedIndex();
            bookingTable.getSelectionModel().clearSelection(index);
            String delQuery = "DELETE FROM Bookings WHERE BookingID = ?";
            try (PreparedStatement stmt = db.getCon().prepareStatement(delQuery)) { //try-with-resources
                stmt.setInt(1, id);
                stmt.executeUpdate();
                db.update("DELETE FROM RepairAndParts WHERE BookingID = " + id);
            } catch (SQLException se) {
                popError("Error Deleting", "SQL Error occurred while deleting this selection", se);
            }
            refreshTable();
            PartsController partsController = (PartsController) ScreenHandler.getController(Main.PARTS);
            partsController.refresh();
            clearFields();
        }
    }

    /**
     * Loads data from fields in the database into a wrapper class {@code Data} to fill the TableView with information.
     * All rows in  the TableView are of type {@code Data}.
     *
     * @return {@code ObservableList<Data>} ObservableList with all information of type Data (Booking Data from DB)
     */
    private ObservableList<Data> loadData() {
        ArrayList<Data> dataList = new ArrayList<>();
        String query = "SELECT Bookings.BookingID, Bookings.RegNumber, Bookings.BookingDate, " +
                "Bookings.CustomerID, Customers.Firstname, Customers.Surname," +
                " EmployeeID, TotalTime, Vehicle.Make FROM Bookings " +
                "INNER JOIN Customers ON Bookings.CustomerID = Customers.CustomerID" +
                " INNER JOIN Vehicle ON Bookings.RegNumber = Vehicle.RegNumber";
        ResultSet rs = db.query(query);
        try {
            while (rs.next())
                dataList.add(new Data(rs));
        } catch (SQLException se) {
            //System.out.println("Loading table flopped g!");
            se.printStackTrace();
            popError("Failed Loading Booking Table", se);
        }
        return FXCollections.observableArrayList(dataList);
    }

    /**
     * Triggered by changing selection from the TableView, this method updates every updatable field
     * (Mainly labels and textarea) that are presented on the Booking Section of the system. Including values
     * in tabs. Also adds relevant listeners for buttons.
     * Retrieves all information from database
     *
     * @param id The ID of the currently selected Booking from the TableView. used to retrieve data from DB
     */
    private void updateDetails(int id) {

        // Retrieve booking info
        String bookingQuery = "SELECT * FROM Bookings WHERE BookingID =" + id;
        ResultSet rsBook = db.query(bookingQuery);
        //DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String custQuery = "SELECT * FROM Customers WHERE CustomerID =" + rsBook.getInt("CustomerID"); // Retrieve Customer info
            ResultSet rsCust = db.query(custQuery);

            String vechQuery = "SELECT * FROM Vehicle WHERE RegNumber = '" + rsBook.getString("RegNumber") + "'";
            ResultSet rsVech = db.query(vechQuery);

            String mechQuery = "SELECT * FROM Mechanics WHERE EmployeeID =" + rsBook.getInt("EmployeeID");
            ResultSet rsMech = db.query(mechQuery);

            //Setting the Details section with the current selection on the table
            bookID.setText(rsBook.getString("BookingID"));
            regNum.setText(rsBook.getString("RegNumber"));
            if (rsBook.getBoolean("BookingType")) bookType.setText("Diagnostics");
            else bookType.setText("Repairs");
            nextBookDate.setText(getNextDate(regNum.getText()));
            timeLbl.setText(rsBook.getString("BookingTime"));
            if (rsVech.getBoolean("Warranty")) warranty.setText("Covered");
            else warranty.setText("Not Covered");
            mileage.setText(rsBook.getString("CurrentMileage"));
            mechanic.setText(rsBook.getString("EmployeeID"));
            rate.setText("£" + rsMech.getString("Rate"));
            vehType.setText(rsVech.getString("Type"));

            //Setting the Customer thingy-ma-bobs (lol tired while doing this so gg)
            custID.setText(rsCust.getString("CustomerID"));
            firstname.setText(rsCust.getString("Firstname"));
            surname.setText(rsCust.getString("Surname"));
            address1.setText(rsCust.getString("FirstAddressLine"));
            address2.setText(rsCust.getString("SecondAddressLine"));
            postcode.setText(rsCust.getString("Postcode"));
            email.setText(rsCust.getString("Email"));

            //Setting the Vehicle thingy-ma-bobs ^^
            reg.setText(regNum.getText());
            make.setText(rsVech.getString("Make"));
            model.setText(rsVech.getString("Model"));
            engSize.setText(rsVech.getString("EngineSize"));
            servDate.setText(rsVech.getString("LastService"));
            MOT.setText(rsVech.getString("MoTDate"));
            fuelType.setText(rsVech.getString("FuelType"));

            updateDateList(regNum.getText());

            //Setting the Faults thingy-ma-bobs
            //faultList #TextBox
            faultList.setWrapText(true);
            faultList.setText(rsBook.getString("Fault"));
            finDateLbl.setText(DateTimeFormatter.ofPattern("d/MM/yyyy").format(LocalDate.parse(rsBook.getString("FinishDate").trim())));
            if (rsBook.getBoolean("FaultFixed") || rsBook.getBoolean("BookingType")) {
                changeRepStatus.setVisible(false);
                faultStatus.setText("No Faults found yet.");
                if (rsBook.getBoolean("FaultFixed")) faultStatus.setText("All faults fixed. Booking Complete.");
                else if (LocalDate.parse(rsBook.getString("FinishDate")).isBefore(LocalDate.now()))
                    faultStatus.setText("Diagnostics booking complete.");
                if (faultList.getText().equals("")) faultList.setText("No recorded faults for this vehicle.");
            } else if (LocalDate.parse(rsBook.getString("BookingDate")).isAfter(LocalDate.now())) {
                changeRepStatus.setVisible(false);
                faultStatus.setText("Booking has not yet started.");
            } else {
                changeRepStatus.setVisible(true);
                faultStatus.setText("Unfixed");
                if(LocalDate.parse(rsBook.getString("FinishDate")).isBefore(LocalDate.now()))
                    faultStatus.setText("Unfixed - Overdue");
                changeRepStatus.setOnAction(event -> {
                    if (AlertBox.choice("Change Repair Status",
                            "Are you sure?",
                            "Are all faults for " + regNum.getText() + " fixed?")) {
                        String inputHours = AlertBox.input("Vehicle is repaired.", "Enter mechanic's hours spent on repair: ");
                        if (inputHours == null) return;
                        try {
                            int hours = Integer.parseInt(inputHours);
                            //, FinishDate = " + LocalDate.now().toString() + "
                            Mechanic mech = new Mechanic(db.query("SELECT * FROM Mechanics WHERE EmployeeID = " + rsBook.getString("EmployeeID")));
                            if (!mech.checkMax(LocalDate.parse(rsBook.getString("BookingDate")),
                                    LocalDate.parse(rsBook.getString("FinishDate")), hours)) {
                                popError("Exceeded hours", "The time inputted for the mechanic exceeds the maximum possible for this booking, try again!");
                                return;
                            }
                            db.update("UPDATE Bookings SET TotalTime = " + hours + ", FaultFixed = 1, FinishDate = '" + LocalDate.now() + "' WHERE BookingID = " + id);
                            //System.out.println(mech.calCost(hours));
                            changeRepStatus.setVisible(false);
                            refreshTable();
                            createBill(id);
                            //System.out.println("Fixed! Must do Mechanic Hours box -> " + hours); //Assumed hours spent by the mechanic are entered manually.
                        } catch (NumberFormatException ne) {
                            popError("Non-numerical input", "Please only enter digits/ numbers.");
                        } catch (SQLException se) {
                            popError("Error finding Mechanic", "The mechanic for this booking was not found!");
                        }
                    }
                });
            }

            if (!rsBook.getBoolean("BookingType")) {
                repInfoTab.setDisable(false);
                String date = rsBook.getString("FinishDate").trim();
                estFinDate.setText(DateTimeFormatter.ofPattern("d/MM/yyyy").format(LocalDate.parse(date)));
                ObservableList<String> storage = FXCollections.observableArrayList();
                ResultSet loadPartsForRep = db.query("SELECT RepairAndParts.PartID, Parts.PartName FROM RepairAndParts INNER JOIN Parts ON RepairAndParts.BookingID =" + id
                        + " AND RepairAndParts.PartID = Parts.PartID");
                while (loadPartsForRep.next())
                    storage.add("ID: " + loadPartsForRep.getString("PartID") + " - Name: " + loadPartsForRep.getString("PartName"));
                partsReqLV.setItems(storage);
            } else repInfoTab.setDisable(true);
            //Setting the Repair details
        } catch (SQLException se) {
            popError("Error loading selected details!", se);
            se.printStackTrace();
        }
    }

    /**
     * Invokes the TotalBill method in the Vehicles Module to create a bill for this booking against the customer
     * under the booking ID.
     *
     * @param id Booking ID of the completed booking
     */
    private void createBill(int id){
        VehiclesController controller = (VehiclesController) ScreenHandler.getController(Main.VEHICLE);
        controller.TotalBill(id);
        AlertBox.info("Successful", "Booking Complete!", "A new bill has successfully been " +
                "created for the customer, for this booking. Please check accounts and ensure it is settled.");
    }

    /**
     * Associates all columns in the TableView to their relevant variables in the wrapper class {@code Data}.
     * Adapted from code.makery.ch
     */
    private void setFields() {
        colID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        colReg.setCellValueFactory(new PropertyValueFactory<>("regNum"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCustID.setCellValueFactory(new PropertyValueFactory<>("custID"));
        colFName.setCellValueFactory(new PropertyValueFactory<>("custFName"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("custSurname"));
        colMechID.setCellValueFactory(new PropertyValueFactory<>("mechID"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeTaken"));
    }

    //Lets now work on the searching shizzle! Lol I feel like I'm doing a tutorial XD
    //Lol when Im just talking to myself through comments... anyways lets code!

    /**
     * Searches database according to the User's selection and input. Updates the table view accordingly
     * to provide results of the search. Uses {@code predicate} to compare existing values.
     * Adapted from code.makery.ch JavaFX course.
     * @param field The field in {@code table} to be checked as a condition
     */
    private void search(String field) {

        if (searchBox.getText().equals("")) {
            popError("Empty Search Box", "Please provide search details or refresh the table.");
            searchBox.requestFocus();
            return;
        }
        String toFind = searchBox.getText();

        FilteredList<Data> filteredData = new FilteredList<>(loadData(), predicate -> false);
        filteredData.setPredicate(data -> {
            String lowerCaseItem;
            lowerCaseItem = toFind.toLowerCase();
            switch (field) {
                case "Firstname":
                    if (data.getCustFName().toLowerCase().contains(lowerCaseItem))
                        return true;
                    //System.out.println("Vech or FN: " + lowerCaseItem);
                    break;
                case "Surname":
                    //System.out.println("Surname: " + lowerCaseItem);
                    if (data.getCustSurname().toLowerCase().contains(lowerCaseItem))
                        return true;
                    break;
                case "RegNumber":
                    if (data.getRegNum().toLowerCase().contains(lowerCaseItem))
                        return true;
                    break;
                case "Make":
                    //System.out.println(data.getMake() + " - Make");
                    if (data.getMake().toLowerCase().contains(lowerCaseItem))
                        return true;
                    break;
            }
            return false;
        });
        if (filteredData.isEmpty()) {
            popError("Search failed!", "No booking found under '" + toFind + "'.");
            searchBox.requestFocus();
            return;
        }
        //System.out.println(table + " " + field);
        //System.out.println(filteredData.toString());
        SortedList<Data> sorted = new SortedList<>(filteredData);
        sorted.comparatorProperty().bind(bookingTable.comparatorProperty());
        bookingTable.setItems(sorted);
        btnReset.setDisable(false);
    }

    /**
     * Checks booking dates in the database given a {@code reg} and compares each date with
     * today's date. If the booking is before today, it's a past booking. Otherwise, it's a future booking.
     *
     * @param reg The Vehicle Registration number to update the ListView with.
     */
    private void updateDateList(String reg) {
        ObservableList<String> dataList = FXCollections.observableArrayList();
        String query = "SELECT BookingDate, BookingID FROM Bookings WHERE RegNumber = '" + reg + "'";
        ResultSet rs = db.query(query);
        try {
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("BookingDate"));
                String status = "Past Booking";
                if (date.isAfter(LocalDate.now()))
                    status = "Future Booking";
                String output = "ID: " + rs.getInt("BookingID") + " - " +
                        DateTimeFormatter.ofPattern("d/MM/yyyy").format(date) + " - " + status;
                dataList.add(output);
            }
        } catch (SQLException se) {
            popError("SQL Error", "Error while grabbing past and future dates!", se);
        }

        dateList.setItems(dataList);
    }

    /**
     * Finds the next booking for a vehicle given its registration number.
     *
     * @param reg Registration number for the Vehicle to check for.
     * @return String value of the date of the next booking.
     */
    private String getNextDate(String reg) {
        String str = "No Next Booking!";
        LocalDate closestDate;
        ChronoUnit unit = ChronoUnit.DAYS;
        String query = "SELECT BookingDate FROM Bookings WHERE RegNumber = '" + reg + "'";
        ResultSet rs = db.query(query);
        try {
            if (rs.next())
                closestDate = LocalDate.parse(rs.getString(1));
            else return str;
            while (rs.next()) {
                LocalDate grabbedDate = LocalDate.parse(rs.getString(1));
                if (grabbedDate.isAfter(LocalDate.now()) && (unit.between(LocalDate.now(), closestDate) <
                        unit.between(LocalDate.now(), grabbedDate)))
                    closestDate = grabbedDate;
                //System.out.println(grabbedDate.toString() + " Current: " + closestDate.toString());
            }
            if (!closestDate.isBefore(LocalDate.now()))
                str = DateTimeFormatter.ofPattern("d/MM/yyyy").format(closestDate);
        } catch (SQLException se) {
            popError("SQL Error", "Error fetching next booking date!", se);
        }
        return str;
    }

    /**
     * Reload the table after searching. Triggered by the reset button - {@code btnReset}.
     */
    @FXML
    private void resetView() {
        refreshTable();
        btnReset.setDisable(true);
    }

    /**
     * Reload/ Update table values;
     */
    public void refreshTable() {
        bookingTable.setItems(loadData());
    }

    /************************************
     * Display error messages to the User. Show Exception stacktrace given {@code e}.
     * @param title Title of the error
     * @param msg Error Message
     *************************************/
    private void popError(String title, String msg) {
        AlertBox.error(title, msg);
    }

    private void popError(String msg, Exception e) {// Will show a popup message
        AlertBox.error("Error with Bookings!", msg, e);
    }

    private void popError(String title, String msg, Exception e) {
        AlertBox.error(title, msg, e);
    }

    @Override
    public void setScreenParent(ScreenHandler currentScreen) {
        sh = currentScreen;
    }

    /**
     * Clear all updatable fields.
     */
    private void clearFields() {
        bookID.setText("");
        regNum.setText("");
        bookType.setText("");
        nextBookDate.setText("");
        timeLbl.setText("");
        warranty.setText("");
        mileage.setText("");
        mechanic.setText("");
        rate.setText("");
        vehType.setText("");

        custID.setText("");
        firstname.setText("");
        surname.setText("");
        address1.setText("");
        address2.setText("");
        postcode.setText("");
        email.setText("");

        reg.setText("");
        make.setText("");
        model.setText("");
        engSize.setText("");
        servDate.setText("");
        MOT.setText("");
        fuelType.setText("");

        faultStatus.setText("");
        finDateLbl.setText("");
        faultList.setText("");
        changeRepStatus.setVisible(false);

        repInfoTab.setDisable(true);
        estFinDate.setText("");

        partsReqLV.setItems(FXCollections.observableArrayList());
        dateList.setItems(FXCollections.observableArrayList());
    }

    //Extra Error handling Methods
    private Thread getThreadByName() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals("JavaFX Application Thread")) return t;
        }
        return null;
    }

    /**
     * Handles API Errors on the JavaFX Application Thread.
     */
    private void handleError() {
        Thread fx = getThreadByName();
        //System.out.println(fx);
        Thread.UncaughtExceptionHandler h = (th, ex) -> {
            if (ex instanceof DateTimeParseException) ;
            //System.out.println("FINALLY CAUGHT!");
        };
        assert fx != null;
        fx.setUncaughtExceptionHandler(h);
    }

}
