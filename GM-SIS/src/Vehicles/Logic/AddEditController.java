package Vehicles.Logic;
/**
 * Created by Ussas on 21/02/2017.
 */

import Entities.Vehicle;
import common.Database;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import misc.AlertBox;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddEditController implements Initializable {
    @FXML
    private Label lblTitle;
    @FXML
    private TextField regTxt;
    @FXML
    private TextField makeTxt;
    @FXML
    private TextField modelTxt;
    @FXML
    private TextField colourTxt;
    @FXML
    private TextField engineTxt;
    @FXML
    private TextField mileageTxt;
    @FXML
    private CheckBox WarrantyChk;
    @FXML
    private ChoiceBox<String> SelectCmb;
    @FXML
    private ChoiceBox<String> VtypeCmb;
    @FXML
    private CheckBox NoSelectChk;
    @FXML
    private ChoiceBox<String> WarrabtyCmb;
    @FXML
    private ChoiceBox<String> FTypeCmb;
    @FXML
    private ChoiceBox<Integer> CustIDCmb;
    @FXML
    private DatePicker MOTdate;
    @FXML
    private DatePicker WarrExpiry;
    @FXML
    private Button btnConfirm;
    @FXML
    private Button btnCancel;

    @FXML
    private BorderPane draggable;
    @FXML
    private Label title;


    Database db = Database.getInstance();
    ObservableList<String> DefaultVehicles;
    boolean Warranty;
    Stage stage;
    String[] Details = null;

    //variables to make the mouse dragable
    private static double xOffset = 0;
    private static double yOffset = 0;
    private boolean Edit;
    private Integer customerID = null; //customer accounts
    private Vehicle editvehicle;
    private boolean warrantyCheck;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

    public  AddEditController(boolean edit, Vehicle selectedVehicle) {
        Edit = edit;
        editvehicle = selectedVehicle;
    }

    public AddEditController(boolean edit,Vehicle selectedVehicle, int id){ //customer accounts
        Edit=edit;
        editvehicle=selectedVehicle;
        customerID = id;
    }

    public void initialize(URL location, ResourceBundle resources) {
        //Create changeListener for Default vehicles
        ChangeListener<String> Default = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals("Select a vehicle")) {
                    try {
                        Details = SelectCmb.getSelectionModel().getSelectedItem().split(",");
                        makeTxt.setText(Details[0]);
                        modelTxt.setText(Details[1]);
                        engineTxt.setText(Details[2]);
                        FTypeCmb.getSelectionModel().select(0);

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        db = Database.getInstance();
        //make the window draggable
        draggable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        draggable.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                draggable.getScene().getWindow().setX(event.getScreenX() - xOffset);
                draggable.getScene().getWindow().setY(event.getScreenY() - yOffset);
            }
        });

        //Set warranty field as disabled
        WarrabtyCmb.setDisable(true);
        WarrExpiry.setDisable(true);
        //Listener for cancel button
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage = (Stage) btnCancel.getScene().getWindow();
                if (AlertBox.choice("Confirmation", "Are you sure?", "Are you sure you want close this window?")) {
                    stage.close();
                }
            }
        });


        //add Petrol types to type cmb
        FTypeCmb.getItems().addAll("Petrol", "Diesel");

        //populate vehicle type selection combobox
        VtypeCmb.getItems().addAll("Car", "Van", "Truck");
        SelectCmb.getItems().add("Select a vehicle type");

        //Handler for warranty check box
        WarrantyChk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (WarrantyChk.isSelected()) {
                    //Get list of warranty companies
                    String WarrQry = "SELECT * FROM WarrantyCompany ";
                    ResultSet rsWarranty = db.query(WarrQry);
                    ObservableList<String> Warranty = FXCollections.observableArrayList();
                    try {
                        while (rsWarranty.next()) {
                            Warranty.add(rsWarranty.getString("CompanyName"));

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    warrantyCheck = true;
                    WarrExpiry.setDisable(false);
                    WarrabtyCmb.setDisable(false);
                    WarrabtyCmb.setItems(Warranty);
                } else {
                    warrantyCheck = false;
                    WarrabtyCmb.setDisable(true);
                    WarrExpiry.setDisable(true);
                    WarrabtyCmb.setItems(FXCollections.observableArrayList(""));
                }
            }
        });

        if (Edit == false) {
            lblTitle.setText("ADD NEW VEHICLE");
            makeTxt.setDisable(true);
            modelTxt.setDisable(true);
            //make sure no past dates can be selected for the warranty and for the MOT
            Callback<DatePicker, DateCell> NoPastDate = dp -> new DateCell()
            {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);

                    //block past dates
                    if(item.isBefore(LocalDate.now()))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        Platform.runLater(() -> setDisable(true));

                    }
                }
            };
            MOTdate.setDayCellFactory(NoPastDate);
            WarrExpiry.setDayCellFactory(NoPastDate);


            //Handler for Default vehicle checkbox
            NoSelectChk.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (NoSelectChk.isSelected()) {
                        makeTxt.setDisable(false);
                        modelTxt.setDisable(false);
                        SelectCmb.setDisable(true);
                        SelectCmb.getSelectionModel().selectFirst();
                    } else {
                        makeTxt.setDisable(true);
                        makeTxt.setText("");
                        modelTxt.setDisable(true);
                        modelTxt.setText("");
                        SelectCmb.setDisable(false);
                    }
                }
            });


            //Set list of customer accounts
            String CustQry = "SELECT CustomerID FROM Customers";
            ResultSet rsCust = db.query(CustQry);
            ObservableList<Integer> CustID = FXCollections.observableArrayList();
            try {
                while (rsCust.next()) {
                    CustID.add(rsCust.getInt("CustomerID"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            CustIDCmb.setItems(CustID);

            if (customerID != null)//customer accounts
            {
                CustIDCmb.getSelectionModel().select(customerID);
            }

            //Listener for Select vehicle type
            VtypeCmb.valueProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //Use type of vehicle to change choose vehicle options
                    if (newValue.equals("Car")) {
                        //Populate default vehicle ChoiceBox
                        DefaultVehicles = FXCollections.observableArrayList(
                                "Select a vehicle",
                                "Audi,A3,1.0,Petrol",
                                "Ford,Fiesta,1.0,Petrol",
                                "Ford,Focus,1.0,Petrol",
                                "Mercedes,C class,1.6,Petrol",
                                "Nissan,Juke,1.2,Petrol",
                                "Nissan,Qashqai,1.2,Petrol",
                                "Volkswagen,Golf,1.0,Petrol",
                                "Volkswagen,Polo,1.0,Petrol",
                                "Vauxhall,Astra,1.0,Petrol",
                                "Vauxhall,Corsa,1.0,Petrol"

                        );
                    } else if (newValue.equals("Van")) {
                        DefaultVehicles = FXCollections.observableArrayList(
                                "Select a vehicle",
                                "Citroen,Berlingo,1.2,Petrol",
                                "Ford,Transit,3.7,Petrol",
                                "Ford,Transit Connect,2.5,Petrol",
                                "Peugeot,Partner,1.2,Petrol",
                                "Vauxhall,Vivaro,2.0,Petrol"
                        );
                    } else if (newValue.equals("Truck")) {
                        DefaultVehicles = FXCollections.observableArrayList(
                                "Select a vehicle",
                                "DAF,LF45-150,4.5,Petrol",
                                "Iveco,Eurocargo,4.5,Petrol",
                                "MAN,TGL,4.6,Petrol",
                                "Mercedes,Actros,12,Petrol",
                                "Volvo,FH13,13,Petrol "
                        );
                    }
                    //Removing listener
                    SelectCmb.getSelectionModel().selectedItemProperty().removeListener(Default);
                    SelectCmb.setItems(DefaultVehicles);
                    SelectCmb.getSelectionModel().selectFirst();
                    //listener for selection of a default vehicle
                    SelectCmb.getSelectionModel().selectedItemProperty().addListener(Default);
                }
            });
            //Listener for add button
            btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (checkEmpty()) {
                        if (AlertBox.choice("Confirmation", "Add new record?", "Are you sure you want to add this record?")) {
                            //Convert mot date to String
                            String MOT = formatter.format(MOTdate.getValue());
                            //Check if vehicle has warranty
                            if (warrantyCheck == true) {
                                //if vehicle has warranty create new record in Warranty table
                                //Get the id for the selected warranty company
                                if (WarrabtyCmb.getSelectionModel().isEmpty()) {
                                    AlertBox.error("Select warranty", "Please select a warranty company");
                                } else {
                                    //add new record to db with warranty set to true
                                    String AddQry = "INSERT INTO Vehicle(RegNumber,CustomerID,Make,Model,EngineSize,FuelType,Colour,MoTDate,MileAge,Warranty,Type,LastService) " +
                                            "VALUES('" + regTxt.getText() + "','" + CustIDCmb.getSelectionModel().getSelectedItem() + "','" + makeTxt.getText() + "' , '" + modelTxt.getText() + "','" + engineTxt.getText() + "' ,'" + FTypeCmb.getSelectionModel().getSelectedItem() + "','" +
                                            colourTxt.getText() + "' , '" + MOT + "', '" + Integer.parseInt(mileageTxt.getText()) + "' , '" + 1 + "' , '" + VtypeCmb.getSelectionModel().getSelectedItem() + "','" + VtypeCmb.getSelectionModel().getSelectedItem() + "')";
                                    db.update(AddQry);
                                    String Warrantyqry = "SELECT CompanyID FROM WarrantyCompany WHERE CompanyName='" + WarrabtyCmb.getSelectionModel().getSelectedItem() + "'";
                                    ResultSet WarrantyID = db.query(Warrantyqry);
                                    String Expiry = formatter.format(WarrExpiry.getValue());
                                    try {
                                        db.update("INSERT INTO Warranty(CompanyID,RegNumber,ExpiryDate)" + "VALUES('" + WarrantyID.getInt("CompanyID") + "','" + regTxt.getText() + "', '" + Expiry + "')");
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            //code to add vehicle that does not have warranty
                            else if (warrantyCheck == false) {
                                String AddQry = "INSERT INTO Vehicle(RegNumber,CustomerID,Make,Model,EngineSize,FuelType,Colour,MoTDate,MileAge,Warranty,Type,LastService) " +
                                        "VALUES('" + regTxt.getText() + "','" + CustIDCmb.getSelectionModel().getSelectedItem() + "','" + makeTxt.getText() + "' , '" + modelTxt.getText() + "','" + engineTxt.getText() + "' ,'" + FTypeCmb.getSelectionModel().getSelectedItem() + "','" +
                                        colourTxt.getText() + "' , '" + MOT + "', '" + Integer.parseInt(mileageTxt.getText()) + "' , '" + 0 + "' , '" + VtypeCmb.getSelectionModel().getSelectedItem() + "','" + VtypeCmb.getSelectionModel().getSelectedItem() + "')";
                                db.update(AddQry);
                            }
                            stage=(Stage) btnConfirm.getScene().getWindow();
                            stage.close();
                        }

                    }
                }
            });
//Code for edit from this point
        } else {
            lblTitle.setText("EDIT VEHICLE");
            //make vehicle type selection unavailable to the user
            SelectCmb.setDisable(true);
            //set the customer Id and make it unavailable
            CustIDCmb.getItems().setAll(editvehicle.getCustID());
            CustIDCmb.getSelectionModel().selectFirst();
            CustIDCmb.setDisable(true);
            //Fill all the text fields and set make and model read only
            regTxt.setText(editvehicle.getRegNumber());
            makeTxt.setText(editvehicle.getMake());
            modelTxt.setText(editvehicle.getModel());
            mileageTxt.setText(Integer.toString(editvehicle.getMileage()));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate MOT = LocalDate.parse(editvehicle.getMOTdate(), formatter);
            MOTdate.setValue(MOT);
            colourTxt.setText(editvehicle.getColour());
            //Set fuel
            if (editvehicle.getFuelType().equals("Petrol")) {
                FTypeCmb.getSelectionModel().select(0);
            } else {
                FTypeCmb.getSelectionModel().select(1);
            }
            //Set vehicle type
            if (editvehicle.getVehicleType().equals("Car")) {
                VtypeCmb.getSelectionModel().select(0);
            } else if (editvehicle.getVehicleType().equals("Van")) {
                VtypeCmb.getSelectionModel().select(1);
            } else {
                VtypeCmb.getSelectionModel().select(2);
            }
            //set fuel type and vehicle type to disabled
            //Set engine size
            engineTxt.setText(editvehicle.getEngineSize());
            // IF vehicle has warranty get Warranty expiry date and display warranty company name
            if (editvehicle.isWarranty()) {
                WarrantyChk.fire();
                try {
                    ResultSet rsWarrantyEx = db.query("SELECT ExpiryDate,CompanyID FROM Warranty WHERE RegNumber='" + editvehicle.getRegNumber() + "' LIMIT 1");
                    //String ExpiryValue=rsWarrantyEx.getString("ExpiryDate")
                    while (rsWarrantyEx.next()) {
                        LocalDate Expiry = LocalDate.parse(rsWarrantyEx.getString("ExpiryDate"), formatter);
                        WarrExpiry.setValue(Expiry);
                        String Warrantyqry = "SELECT CompanyName FROM WarrantyCompany WHERE CompanyID='" + rsWarrantyEx.getInt("CompanyID") + "'";
                        ResultSet WarrantyName = db.query(Warrantyqry);
                        String CompanyName = WarrantyName.getString("CompanyName");
                        //compare vehicle Warranty company name with all companies to set company as default
                        for (int i = 0; i < WarrabtyCmb.getItems().size(); i++) {
                            if (CompanyName.equals(WarrabtyCmb.getItems().get(i))) {
                                WarrabtyCmb.getSelectionModel().select(i);
                            }
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //block dates before current warranty expiry
            if(editvehicle.isWarranty()) {
                Callback<DatePicker, DateCell> warranty = dp -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isBefore(WarrExpiry.getValue())) {
                            setStyle("-fx-background-color: #ffc0cb;");
                            Platform.runLater(() -> setDisable(true));

                        }

                    }

                };
                WarrExpiry.setDayCellFactory(warranty);
            }
            else{
                Callback<DatePicker, DateCell> warranty1 = dp -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isBefore(LocalDate.now())) {
                            setStyle("-fx-background-color: #ffc0cb;");
                            Platform.runLater(() -> setDisable(true));

                        }
                    }
                };
                WarrExpiry.setDayCellFactory(warranty1);
            }
            //block dates before current Mot expiry
            Callback<DatePicker, DateCell> MotExpiry= dp -> new DateCell()
            {
                @Override
                public void updateItem(LocalDate item, boolean empty)
                {
                    super.updateItem(item, empty);

                    if(item.isBefore(MOTdate.getValue()))
                    {
                        setStyle("-fx-background-color: #ffc0cb;");
                        Platform.runLater(() -> setDisable(true));

                    }
                }
            };
            MOTdate.setDayCellFactory(MotExpiry);
            btnConfirm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (checkEmpty()) {
                        if (AlertBox.choice("Confirmation", "Edit Record?", "Are you sure you want to make these changes?")) {
                            //retrieve company ID
                            ResultSet rsCompanyID = db.query("SELECT CompanyID FROM WarrantyCompany " +
                                    "WHERE CompanyName='" + WarrabtyCmb.getSelectionModel().getSelectedItem() + "'");
                            String Editqry = ("UPDATE Vehicle SET RegNumber=?,Model=?,Make=?,EngineSize=?,FuelType=?,Colour=?,MoTDate=?,Mileage=?,Warranty=?  WHERE RegNumber='" + editvehicle.getRegNumber() + "'");
                            try {
                                PreparedStatement stm = db.getCon().prepareStatement((Editqry));
                                stm.setString(1, regTxt.getText());
                                stm.setString(2, modelTxt.getText());
                                stm.setString(3, makeTxt.getText());
                                stm.setString(4, engineTxt.getText());
                                stm.setString(5, FTypeCmb.getSelectionModel().getSelectedItem());
                                stm.setString(6, colourTxt.getText());
                                stm.setString(7, formatter.format(MOTdate.getValue()));
                                stm.setInt(8, Integer.parseInt(mileageTxt.getText()));
                                stm.setBoolean(9, warrantyCheck);
                                stm.executeUpdate();
                            } catch (SQLException E) {
                                E.printStackTrace();
                            }
                            //handle vehicle registation change
                            if (!editvehicle.getRegNumber().equals(regTxt)) {
                                String EditBook = "UPDATE Bookings SET RegNumber=? WHERE RegNumber='" + editvehicle.getRegNumber() + "'";
                                try {
                                    PreparedStatement Bookstm = db.getCon().prepareStatement(EditBook);
                                    Bookstm.setString(1, regTxt.getText());
                                    Bookstm.executeUpdate();

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                //Update Warranty details
                                String WarrEdit = "UPDATE Warranty SET RegNumber=? WHERE RegNumber='" + editvehicle.getRegNumber() + "'";
                                try {
                                    PreparedStatement Warrstm = db.getCon().prepareStatement(WarrEdit);
                                    Warrstm.setString(1, regTxt.getText());
                                    Warrstm.executeUpdate();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            //handle warranty details change
                            if (warrantyCheck && editvehicle.isWarranty()) {
                                String WarrUpdate = "UPDATE Warranty SET CompanyID=?,ExpiryDate=? WHERE RegNumber='" + regTxt.getText() + "'";
                                try {
                                    PreparedStatement WarrStatement = db.getCon().prepareStatement(WarrUpdate);
                                    while (rsCompanyID.next()) {
                                        WarrStatement.setInt(1, rsCompanyID.getInt("CompanyID"));
                                        WarrStatement.setString(2, formatter.format(WarrExpiry.getValue()));
                                        WarrStatement.executeUpdate();
                                    }


                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else if (warrantyCheck && !editvehicle.isWarranty()) {
                                //create new warranty record
                                try {
                                    String WarrNewQry = "INSERT INTO Warranty(CompanyID,RegNumber,ExpiryDate) VALUES(?,?,?)";
                                    PreparedStatement WarrNew = db.getCon().prepareStatement(WarrNewQry);
                                    while (rsCompanyID.next()) {
                                        WarrNew.setInt(1, rsCompanyID.getInt("CompanyID"));
                                        WarrNew.setString(2, regTxt.getText());
                                        WarrNew.setString(3, formatter.format(WarrExpiry.getValue()));
                                        WarrNew.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else if (!warrantyCheck && editvehicle.isWarranty()) {
                                //delete warranty record as vehicle is no longer under warranty
                                db.update("DELETE FROM Warranty WHERE RegNumber='" + regTxt.getText() + "'");
                            }
                            stage=(Stage) btnConfirm.getScene().getWindow();
                            stage.close();
                        }
                    }
                }
            });
        }
    }


    public boolean checkEmpty() {
    String msg="";
    boolean error=true;
    //textbpox validation
    if(regTxt.getText().equals("")){msg=msg+"Registation Number\n";error=false;}
    if(makeTxt.getText().equals("")){msg=msg+"Make\n";error=false;}
    if(modelTxt.getText().equals("")){msg=msg+"Model\n";error=false;}
    if(colourTxt.getText().equals("")){msg=msg+"Colour\n";error=false;}
    if (engineTxt.getText().equals("")){msg=msg+"Engine Size\n";error=false;}
    if(mileageTxt.getText().equals("")){msg=msg+"Mileage\n";error=false;}
    //check mileage for letters
        for(int i=0;i<mileageTxt.getText().length();i++){
        char c=mileageTxt.getText().charAt(i);
        if(Character.isLetter(c)){
            error=false;
            msg=msg+"Only numbers allowed for mileage\n";
        }
        }
      //check engine size
        for(int j=0;j<engineTxt.getText().length();j++){
            char e=engineTxt.getText().charAt(j);
            if(Character.isLetter(e)){
                error=false;
                msg=msg+"Only numbers allowed for engine size\n";
            }
        }
    //check choiceboxes
     if(CustIDCmb.getSelectionModel().isEmpty()){msg=msg+"Customer ID\n";error=false;}
     if(VtypeCmb.getSelectionModel().isEmpty()){msg=msg+"Vehicle type\n";error=false;System.out.print("hi");}
     if(FTypeCmb.getSelectionModel().isEmpty()){msg=msg+"Fuel Type\n";error=false;}
    //check warranty
        if(warrantyCheck){
         if(WarrabtyCmb.getSelectionModel().isEmpty()){msg=msg+"Warranty Company\n";error=false;}
         if(WarrExpiry.getValue()==null){msg=msg+"Warranty Expiry Date\n";error=false;}
        }
    //check mot
        if(MOTdate.getValue()==null){msg=msg+"MOT Expiry Date\n";error=false;}
        if(error==false){
            AlertBox.error("Missing values",msg);
            return error;
        }
        else{
            return error;
        }
    }

}




