
/**
 * Created by Hossain on 28/01/2017.
 */
package Vehicles.Logic;
import Entities.BookingPast;
import Entities.Bookings;
import Entities.Parts;
import Entities.Vehicle;
import UI.Menu;
import UI.ScreenHandler;
import UI.ScreenMaster;
import Vehicles.GUI.VehicleMain;
import common.Database;
import common.Main;
import customers.logic.CustomerController;
import diagrep.logic.BookingController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import misc.AlertBox;
import parts.PartsController;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VehiclesController implements Initializable, ScreenMaster {

    @FXML
    private AnchorPane nav1;
    @FXML
    private Button navUP;
    @FXML
    private Rectangle accent1;
    @FXML
    private AnchorPane nav2;
    @FXML
    private Button navCust;
    @FXML
    private Rectangle accent2;
    @FXML
    private AnchorPane nav3;
    @FXML
    private Button navVech;
    @FXML
    private Rectangle accent3;
    @FXML
    private AnchorPane nav5;
    @FXML
    private Button navBook;

    @FXML
    private Rectangle accent5;

    @FXML
    private AnchorPane nav4;

    @FXML
    private Button navParts;

    @FXML
    private Rectangle accent4;

    @FXML
    private Button sRep;

    @FXML
    private AnchorPane nav6;
    @FXML
    private Button navOut;
    @FXML
    private Label title;
    @FXML
    private AnchorPane nav21;
    @FXML
    private Button navCust1;
    @FXML
    private Rectangle accent21;
    @FXML
    private Button closeBtn;
    @FXML
    private Button minBtn;
    @FXML
    private BorderPane draggable;
    //Search bar components
    @FXML
    ChoiceBox SearchType;
    @FXML
    Button searchBtn;
    @FXML
    TextField txtSearch;
    @FXML
    private Button addbtn;
    @FXML
    private Button editbtn;
    @FXML
    private Button deletebtn;
    @FXML
    private Button resetbtn;
    @FXML
    private ChoiceBox cmbType;


    //Vehicle Display labels.
    @FXML
    private Label Reglbl;
    @FXML
    private Label makelbl;
    @FXML
    private Label modellbl;
    @FXML
    private Label EngSizelbl;
    @FXML
    private Label FTypelbl;
    @FXML
    private Label MoTlbl;
    @FXML
    private Label colourlbl;
    @FXML
    private Label expirylbl;
    @FXML
    private Label mileAgelbl;
    @FXML
    private Label warrantylbl;
    @FXML
    private Label Typelbl;

    //Table Columns
    @FXML
    public TableView<Data> vehicles;
    @FXML
    private TableColumn<Data, String> RegCol;
    @FXML
    private TableColumn<Data, String> ModelCol;
    @FXML
    private TableColumn<Data, String> MakeCol;
    @FXML
    private TableColumn<?, ?> PostCodeCol;
    @FXML
    private TableColumn<Data, String> FNameCol;
    @FXML
    private TableColumn<Data, String> LNameCol;
    @FXML
    private TableColumn<Data, String> DateCol;
    @FXML
    private TableColumn<Data, Time> TimeCol;

    @FXML
    private TableView<BookingPast> PastBookings;
    @FXML
    private TableColumn<BookingPast, String> PastBookDateCol;
    @FXML
    private TableColumn<BookingPast, String> BookingBillCol;

    @FXML
    private TableView<Bookings> FutureBookings;
    @FXML
    private TableColumn<Bookings, String> FutureBookDateCol;
    @FXML
    private TableColumn<Bookings, Time> FutureBookTimeCol;

    @FXML
    private TableView<Parts> Parts;
    @FXML
    private TableColumn<Parts, String> PartNameCol;
    @FXML
    private TableColumn<Parts, String> DateInstCol;


    private Database db;
    private ScreenHandler sh;
    private Vehicle selectedVehicle = null;
    public Stage stage;

   //All elements needed to sort bookings.
    LocalDate CurrentDate=null;
    String CurrentTime=null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter Format2=DateTimeFormatter.ofPattern("HH:mm");

    public VehiclesController(){

    }

    public void initialize(URL location, ResourceBundle resources) {

        db = Database.getInstance();
        Menu.loadTopBars(title, closeBtn, minBtn, draggable);
        navOut.setOnAction(e -> logout());
        navUP.setOnAction(e -> goToUP());
        navCust.setOnAction(e -> goToCust());
        navBook.setOnAction(e -> goToBookings());
        navParts.setOnAction(e -> goToParts());
        sRep.setOnAction(e -> goToSPC());

        //Get Current Date and time
        CurrentDate=LocalDate.now();
        CurrentTime= Format2.format(LocalTime.now());


        //Load TableView
        Fields();
        vehicles.setItems(Updatetable());

        //placeholder for booking and parts tables when to no vehicle is selected
        PastBookings.setPlaceholder(new Label("Select a vehicle"));
        FutureBookings.setPlaceholder(new Label("Select a vehicle"));
        Parts.setPlaceholder(new Label("Select a vehicle "));

        // Initialize search combobox
        ObservableList<String>  searchOptions= FXCollections.observableArrayList("Select search type","Registration Number","Manufacturer");
        SearchType.setItems(searchOptions);
        SearchType.getSelectionModel().selectFirst();
        //initialize type search box
        ObservableList<String> TypeOptions=FXCollections.observableArrayList("Select type",
                "Car",
                "Van",
                "Truck");
        cmbType.setItems(TypeOptions);
        cmbType.getSelectionModel().selectFirst();


        //Search Button event handler
        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                int typeSelected = SearchType.getSelectionModel().getSelectedIndex();
                switch (typeSelected) {
                    case 0:
                        if(cmbType.getSelectionModel().getSelectedIndex()!=0){
                            Search("Empty","",cmbType.getSelectionModel().getSelectedItem().toString());
                        }
                        else{
                            AlertBox.error("No field","Please enter search value");
                        }
                        break;
                    case 1:
                        Search("RegNumber", txtSearch.getText(),cmbType.getSelectionModel().getSelectedItem().toString());
                        break;
                    case 2:
                        Search("Make", txtSearch.getText(),cmbType.getSelectionModel().getSelectedItem().toString());
                        break;
                }

            }
        });
        //handle rest button
        resetbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                vehicles.setItems(Updatetable());
            }
        });
        //handler for delete button
        deletebtn.setOnAction(new EventHandler<ActionEvent>() {
                                  @Override
                                  public void handle(ActionEvent event) {
                                      if (selectedVehicle == null) {
                                          AlertBox.error("No vehicle selected", "Please select a vehicle record that you would like to Delete");
                                      } else {
                                          if (AlertBox.choice("Confirmation", "Delete Record?", "Are you sure you want to delete this record?")) {

                                              try {

                                                  String deleteQry = "DELETE  FROM Vehicle WHERE RegNumber='" + selectedVehicle.getRegNumber() + "'";
                                                  Statement stm = db.getCon().createStatement();
                                                  db.update("PRAGMA foreign_keys=ON");
                                                  stm.executeUpdate(deleteQry);
                                                  //Delete all bookings and parts corresponding to the vehicle deleted
                                                  ResultSet rsBookingDelete = db.query("SELECT BookingID FROM Bookings WHERE RegNumber='" + selectedVehicle.getRegNumber() + "'");
                                                  while (rsBookingDelete.next()) {
                                                      String deletePartQry = "DELETE FROM PartForRepair WHERE RepairID='" + rsBookingDelete.getInt("BookingID") + "'";
                                                      stm.executeUpdate(deletePartQry);
                                                      stm.executeUpdate("DELETE FROM RepairAndParts WHERE BookingId='" + rsBookingDelete.getInt("BookingID") + "'");
                                                      String deleteBookQry = "DELETE FROM Bookings WHERE RegNumber='" + selectedVehicle.getRegNumber() + "'";
                                                      stm.executeUpdate(deleteBookQry);
                                                      stm.executeUpdate("DELETE FROM SpecialRepairVehicle WHERE vehicleRegistration='" + selectedVehicle.getRegNumber() + "'");
                                                  }
                                                  //Refresh parts and booking
                                                  RefreshBooking();
                                                  RefreshParts();
                                                  RefreshCustomer();
                                              } catch (SQLException e) {
                                                  e.printStackTrace();

                                              }
                                          }
                                          vehicles.setItems(Updatetable());

                                      }
                                  }
                              });


        editbtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (selectedVehicle == null) {
                    AlertBox.error("No vehicle selected", "Please select a vehicle record that you would like to edit");
                } else {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vehicles/GUI/AddEditVehicle.fxml"));
                        AddEditController controller = new AddEditController(true, selectedVehicle);
                        fxmlLoader.setController(controller);
                        Parent root1 = (Parent) fxmlLoader.load();
                        stage = new Stage();
                        stage.initModality(Modality.WINDOW_MODAL);
                        stage.initOwner(VehicleMain.stage);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(new Scene(root1));
                        stage.showAndWait();
                        vehicles.setItems(Updatetable());
                        RefreshCustomer();
                        RefreshBooking();
                        RefreshParts();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //Update detail fields, booking table and parts table hen a row in the tableview is selected
        vehicles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                GetDetails(newValue.getRegNumber());
                FillParts(selectedVehicle);
                //TotalBill(12);
                SetBookTable(selectedVehicle);
            }
        });
    }

    //Handler for Add button

    //refresh vehicle table
    public void refresh(){
        vehicles.setItems(Updatetable());
    }

    @FXML public void addbtn(){
        add(null);
    }

    @FXML public void add (Integer key){
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Vehicles/GUI/AddEditVehicle.fxml"));
                AddEditController controller;
                if (key == null){
                controller=new AddEditController(false,null);
                }
                else {
                    controller = new AddEditController(false,null, key);
                }
                fxmlLoader.setController(controller);
                Parent root1 = (Parent) fxmlLoader.load();
                stage = new Stage();
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(VehicleMain.stage);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(new Scene(root1));
                stage.showAndWait();
                vehicles.setItems(Updatetable());

                CustomerController updateCustomerController = (CustomerController) ScreenHandler.getController(Main.CUSTOMER);
                updateCustomerController.loadCustomerData(2);

            }catch(Exception e){e.printStackTrace();}

        }


    private void Fields() {
        RegCol.setCellValueFactory(new PropertyValueFactory<>("RegNumber"));
        ModelCol.setCellValueFactory(new PropertyValueFactory<>("Model"));
        MakeCol.setCellValueFactory(new PropertyValueFactory<>("Make"));
        PostCodeCol.setCellValueFactory(new PropertyValueFactory<>("PostCode"));
        FNameCol.setCellValueFactory(new PropertyValueFactory<>("FName"));
        LNameCol.setCellValueFactory(new PropertyValueFactory<>("LName"));
        DateCol.setCellValueFactory(new PropertyValueFactory<>("BookDate"));
        TimeCol.setCellValueFactory(new PropertyValueFactory<>("BookTime"));
    }

    private ObservableList<Data> Updatetable() {
        ArrayList<Data> DefaultList = new ArrayList<>();
        //query to get vehicle details
        String query = "SELECT * FROM Vehicle";
        ResultSet rsVehicle = db.query(query);
        return GetData(rsVehicle);
    }


    private void Search(String Field, String toFind,String Type) {
        ArrayList<Data> SearchList = new ArrayList<>();
        ResultSet rsVehicle = null;
        if (toFind.equals("") && Type.equals("Select type")) {
            AlertBox.error("Empty Field", "Search field empty");
        } else {
            if (!Type.equals("Select type")) {
                if (Field.equals("RegNumber")) {
                    String query = "SELECT * FROM Vehicle WHERE RegNumber LIKE '%" + toFind + "%' " +
                            "AND Type='"+Type+"'";
                    rsVehicle = db.query(query);
                } else if (Field.equals("Make")) {
                    String query = "SELECT * FROM Vehicle WHERE Make LIKE '" + toFind + "%'" +
                            " AND Type='"+Type+"'";
                    rsVehicle = db.query(query);
                }
                else{
                    String query = "SELECT * FROM Vehicle WHERE Type='"+Type+"'";
                    rsVehicle = db.query(query);
                }
            }
            else {
                if (Field.equals("RegNumber")) {
                    String query = "SELECT * FROM Vehicle WHERE RegNumber LIKE '%" + toFind + "%'";
                    rsVehicle = db.query(query);
                } else if (Field.equals("Make")) {
                    String query = "SELECT * FROM Vehicle WHERE Make LIKE '" + toFind + "%'";
                    rsVehicle = db.query(query);
                }
            }
            try {
                if (!rsVehicle.isBeforeFirst()) {
                    AlertBox.error("No vehicle found","Can't find the vehicle you are searching");

                }
                else{
                    vehicles.setItems(GetData(rsVehicle));
                }
            }catch (SQLException e){e.printStackTrace();}


        }
    }
    private ObservableList<Data> GetData(ResultSet rsVehicle) {
        boolean found;
        ArrayList<Data> List = new ArrayList<>();
        ResultSet rsCustomer;
        ResultSet rsBooking;
        try {
            while (rsVehicle.next()) {
                found=false;
                String CustID = rsVehicle.getString("CustomerID");
                String RegNumber = rsVehicle.getString("RegNumber");
                //query to get customer details
                String query1 = "SELECT CustomerID,Firstname,Surname,Postcode FROM Customers WHERE CustomerID = '" + CustID + "'";
                rsCustomer = db.query(query1);
                //query to get vehicle details
                String query2 = "SELECT * FROM Bookings WHERE RegNumber ='" + RegNumber + "'";
                rsBooking = db.query(query2);
                if(rsBooking.isBeforeFirst()){
                    while (rsBooking.next()){
                            if(LocalDate.parse(rsBooking.getString("BookingDate")).isAfter(LocalDate.now())) {
                            if(!found){
                                    found = true;
                                    CurrentDate = LocalDate.parse(rsBooking.getString("BookingDate"));
                                    CurrentTime=rsBooking.getString("BookingTime");
                            }
                            else{
                                if(LocalDate.parse(rsBooking.getString("BookingDate")).isBefore(CurrentDate)) {
                                    //List.add(new Data(rsVehicle, rsCustomer, rsBooking.getString("BookingDate"), rsBooking.getString("BookingTime"), true));
                                    CurrentDate = LocalDate.parse(rsBooking.getString("BookingDate"));
                                    CurrentTime = rsBooking.getString("BookingTime");
                                }
                            }
                        }

                    }
                    if(found==true){
                        List.add(new Data(rsVehicle,rsCustomer,formatter.format(CurrentDate),CurrentTime,true));
                    }
                    else{
                            List.add(new Data(rsVehicle,rsCustomer,"no","no",false));

                    }
                }
                else{
                    List.add(new Data(rsVehicle,rsCustomer,"no","no",false));
                }
                //Add new Data Object in arrayList
                //List.add(new Data(rsVehicle, rsCustomer, rsBooking));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(List);
    }
    public void GetDetails(String RegNumber) {
        String vehicleQuery = "SELECT * FROM Vehicle WHERE RegNumber='" + RegNumber + "'";
        ResultSet rsVehicle = db.query(vehicleQuery);
        selectedVehicle = new Vehicle(rsVehicle);
        Reglbl.setText(selectedVehicle.getRegNumber());
        makelbl.setText(selectedVehicle.getMake());
        modellbl.setText(selectedVehicle.getModel());
        EngSizelbl.setText(selectedVehicle.getEngineSize()+"L");
        MoTlbl.setText(selectedVehicle.getMOTdate());
        mileAgelbl.setText(Integer.toString(selectedVehicle.getMileage()));
        Typelbl.setText(selectedVehicle.getVehicleType());
        FTypelbl.setText(selectedVehicle.getFuelType());
        //lstServlbl.setText(selectedVehicle.getLastService());
        colourlbl.setText(selectedVehicle.getColour());
        //Display if under warranty
        if (selectedVehicle.isWarranty()==true) {
            ResultSet rsWarranty=db.query("SELECT Warranty.ExpiryDate,WarrantyCompany.CompanyName FROM Warranty" +
                    " INNER JOIN WarrantyCompany ON Warranty.CompanyID=WarrantyCompany.CompanyID " +
                    "WHERE RegNumber='"+selectedVehicle.getRegNumber()+"'");
            try {
                while(rsWarranty.next()) {
                    warrantylbl.setText(rsWarranty.getString("CompanyName"));
                    if(warrantylbl.getText().equals("Queen Mary Warranty")){
                        warrantylbl.setText("QM Warranty");
                    }
                    expirylbl.setText(rsWarranty.getString("ExpiryDate"));
                }
            }catch(SQLException e ){e.printStackTrace();}
        } else {
            warrantylbl.setText("Not Covered");
        }
    }
    public void SetBookTable(Vehicle selectedVehicle) {
        //set fields in booking table
        PastBookDateCol.setCellValueFactory(new PropertyValueFactory<>("BookingDate"));
        BookingBillCol.setCellValueFactory(new PropertyValueFactory<>("Bill"));
        FutureBookTimeCol.setCellValueFactory(new PropertyValueFactory<>("FutureBookTime"));
        FutureBookDateCol.setCellValueFactory(new PropertyValueFactory<>("FutureBookDate"));

        ArrayList<BookingPast> ListPast = new ArrayList<>();
        ArrayList<Bookings> ListFuture = new ArrayList<>();

        //Retrieve all bookings for selected vehicle
        ResultSet rsBookings=db.query("SELECT BookingDate,BookingID,BookingTime FROM Bookings WHERE RegNumber='"+selectedVehicle.getRegNumber()+"' ORDER BY BookingDate ASC");
        try {
                PastBookings.getItems().clear();
                FutureBookings.getItems().clear();
                PastBookings.setPlaceholder(new Label(" No Booking Found"));
                FutureBookings.setPlaceholder(new Label("No Booking Found"));
                while(rsBookings.next()){
                    if(LocalDate.parse(rsBookings.getString("BookingDate")).isAfter(LocalDate.now())){
                       ListFuture.add(new Bookings(rsBookings));
                    }
                    else if(LocalDate.parse(rsBookings.getString("BookingDate")).isBefore(LocalDate.now())) {
                        ResultSet rsCost = db.query("SELECT BillTotal FROM Account WHERE BookingID='" + rsBookings.getString("BookingID") + "'");
                        if (!rsCost.isBeforeFirst()) {
                            ListPast.add(new BookingPast(rsBookings.getString("BookingDate"), 0));
                        } else {
                            while (rsCost.next()) {
                                ListPast.add(new BookingPast(rsBookings.getString("BookingDate"),rsCost.getDouble("BillTotal")));

                            }
                        }
                    }
                    else{
                        if(LocalTime.parse(rsBookings.getString("BookingTime")).isBefore(LocalTime.now())){
                            ResultSet rsCost = db.query("SELECT BillTotal FROM Account WHERE BookingID='" + rsBookings.getString("BookingID") + "'");

                            if (!rsCost.isBeforeFirst()) {
                                ListPast.add(new BookingPast(rsBookings.getString("BookingDate"), 0));
                            } else {
                                while (rsCost.next()) {
                                    ListPast.add(new BookingPast(rsBookings.getString("BookingDate"),rsCost.getDouble("BillTotal")));

                                }
                            }
                        }
                        else if(LocalTime.parse(rsBookings.getString("BookingTime")).isAfter(LocalTime.now())){
                            ListFuture.add(new Bookings(rsBookings));
                        }
                    }
                }
            PastBookings.setItems(FXCollections.observableArrayList(ListPast));
            FutureBookings.setItems(FXCollections.observableArrayList(ListFuture));
            }catch(SQLException e){e.printStackTrace();}

    }
    //Populate the parts Table
    public void FillParts(Vehicle selectedVehicle){
        //retrieve parts name and date installed from the database
        PartNameCol.setCellValueFactory(new PropertyValueFactory<>("PartName"));
        DateInstCol.setCellValueFactory(new PropertyValueFactory<>("DateInstalled"));

        //Create arrayList for parts
        ArrayList<Parts> PartsUsed=new ArrayList<Parts>();

        //retrieve parts used from database
        String qry="SELECT PartForRepair.InstallDate,Parts.PartName FROM Bookings" +
                " INNER JOIN PartForRepair ON Bookings.BookingID=PartForRepair.RepairID" +
                " INNER JOIN Parts ON PartForRepair.PartID=Parts.PartID " +
                " WHERE RegNumber='"+selectedVehicle.getRegNumber()+"'";
        ResultSet rsParts=db.query(qry);
        try {
            if(!rsParts.isBeforeFirst()){
                Parts.setPlaceholder(new Label("No part Installed"));
            }
            while (rsParts.next()) {
                PartsUsed.add(new Parts(rsParts));
            }
            Parts.setItems(FXCollections.observableArrayList(PartsUsed));
        }catch (SQLException e){e.printStackTrace();}

    }
    //This method is used to bill a customer and is called from the booking table once a booking is completed
    public void TotalBill(int BookID) {
        Double Bill=0.0;
        Double MechanicBill=0.0;
        int PayMessage=0;
        int CustID=0;
        try {
            //qry to get all th information necessary for to bill the customer.
            String Billqry = "SELECT Bookings.CustomerID,Bookings.TotalTime,Mechanics.Rate,Vehicle.Warranty FROM Bookings " +
                    "INNER JOIN Mechanics ON Bookings.EmployeeID=Mechanics.EmployeeID " +
                    "INNER JOIN Vehicle ON Bookings.RegNumber=Vehicle.RegNumber " +
                    " WHERE Bookings.BookingID='" + BookID + "'";
            ResultSet rsBillDetails = db.query(Billqry);
            while (rsBillDetails.next()) {
                CustID = rsBillDetails.getInt("CustomerID");
                //Work out mechanic bill
                MechanicBill = (rsBillDetails.getInt("TotalTime") * rsBillDetails.getDouble("Rate"));
                //work out bill for parts
                ResultSet rsPartsUsed = db.query("SELECT PartForRepair.PartID,Parts.PartCost FROM PartForRepair " +
                        "INNER JOIN Parts ON PartForRepair.PartID=Parts.PartID WHERE RepairID='" + BookID + "'");
                while (rsPartsUsed.next()) {
                    Bill += rsPartsUsed.getDouble("PartCost");
                }
                //add cost of parts spc if present
                String spcPartqry="SELECT Cost FROM SpecialRepairsParts WHERE BookingID='"+BookID+"'";
                ResultSet rsSpcPart=db.query(spcPartqry);
                while(rsSpcPart.next()){
                    Bill=Bill+rsSpcPart.getDouble("Cost");
                }
                //Add cost of vehicle spc if present
                String VehicleSpcQry="SELECT Cost FROM SpecialRepairVehicle WHERE BookingID='"+BookID+"'";
                ResultSet rsSPCVehicle=db.query(VehicleSpcQry);
                while(rsSPCVehicle.next()){
                    Bill=Bill+rsSPCVehicle.getDouble("Cost");
                }
                Bill = Bill + MechanicBill;
            //Check if the vehicle is under warranty

            if (rsBillDetails.getBoolean("Warranty")) {
                PayMessage = 1;
            }
        }
            //Create a new account record
            String Addqry = "INSERT INTO Account(CustomerID,BookingID,BillTotal,PaymentStatus) VALUES(?,?,?,?)";
            PreparedStatement stm = db.getCon().prepareStatement(Addqry);
            stm.setInt(1,CustID);
            stm.setInt(2,BookID);
            stm.setDouble(3,Bill);
            stm.setInt(4,PayMessage);
            stm.executeUpdate();

        }catch(SQLException e){e.printStackTrace();}
    }


    private void RefreshBooking(){
        BookingController controller=(BookingController) ScreenHandler.getController(Main.BOOKINGS);
        controller.refreshTable();
    }

    private void RefreshParts(){
        PartsController controller1=(PartsController) ScreenHandler.getController(Main.PARTS);
        controller1.refresh();

    }
    private void RefreshCustomer(){
        CustomerController controller2=(CustomerController) ScreenHandler.getController(Main.CUSTOMER);
        controller2.loadCustomerData(2);
    }

    private void logout() {
        Menu.logout(sh);
    }

    private void goToUP() {
        Menu.toUP(sh);
    }

    private void goToCust(){ Menu.toCust(sh); }

    private void goToBookings() { Menu.toBookings(sh); }

    private void goToParts(){ Menu.toParts(sh); }

    private void goToSPC() { Menu.toSPC(); }

    @Override
    public void setScreenParent(ScreenHandler currentScreen) {
        sh = currentScreen;
    }
}






