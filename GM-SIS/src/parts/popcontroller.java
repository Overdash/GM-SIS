
/**
 * Created by Shinu on 21/02/2017.
 */
package parts;

import java.lang.Integer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import misc.AlertBox;
import common.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.*;
import javafx.util.Callback;
import org.joda.time.format.DateTimeFormat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class popcontroller implements Initializable {

    @FXML
    private Button addrep;
    @FXML
    private Button cancelrep;

    @FXML
    private TextField repairidL;
    @FXML
    private TextField partnameL;
    @FXML
    private DatePicker installdateL;
    @FXML
    private TextField warrantyidL;
    @FXML
    private TextField bookingidL;

    @FXML
    private ComboBox<String> bookdroplist;
    @FXML
    private ComboBox<String> partdroplist;

    private Database db;

    @FXML private javafx.scene.control.Button closeButton;

    static int stocklv=0;

    static int currentbooking=-1;

    static String currentpart="";


    //set the comboboxes for booking and parts. add listeners to it
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();

        bookdroplist.setItems(fillcombobox());
        partdroplist.setItems(fillcombobox2());

        //date can only be picked by the calender. cannot be typed
        installdateL.setEditable(false);

        bookdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        currentbooking= Integer.parseInt(newValue)

        );

        partdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        currentpart = newValue.toString()
        );

	//disable future dates for install date for repair as record of repair is recorded after the install
        installdateL.setValue(LocalDate.now());
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isAfter(
                                        LocalDate.now().plusDays(0))
                                        ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #D3D3D3;");
                                }
                                long p = ChronoUnit.DAYS.between(
                                        installdateL.getValue(), item
                                );
                            }
                        };
                    }
                };
        installdateL.setDayCellFactory(dayCellFactory);

    }

    private ObservableList<String> fillcombobox() { //fills the combobox with bookings
        ArrayList<String> list = new ArrayList<>();
        //query to get booking details
        String query = "SELECT BookingID, BookingDate FROM Bookings WHERE BookingType="+0;
        ResultSet rsParts = db.query(query);

        try {
            String bookdate = "";
            while (rsParts.next()) {

		//query for complete repair bookings
                String query2="SELECT * FROM Account WHERE BookingID="+rsParts.getInt("BookingID");
                ResultSet rscomp = db.query(query2);

                bookdate =rsParts.getString("BookingDate");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(bookdate, formatter);

		//if the repair booking is in the past and has not been completed, add it to the list
                if(date.isBefore(LocalDate.now().plusDays(1)) && !rscomp.isBeforeFirst() )
                list.add(rsParts.getString("BookingID"));

                rscomp.close();
            }
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }
        return FXCollections.observableArrayList(list);
    }

    private ObservableList<String> fillcombobox2() { //fills the combobox with parts
        ArrayList<String> list = new ArrayList<>();
        //query to get part details
        String query = "SELECT PartName FROM Parts";
        ResultSet rsParts = db.query(query);

        try {
            while (rsParts.next())
                list.add(rsParts.getString("PartName"));
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }
        return FXCollections.observableArrayList(list);
    }


    @FXML
    public void addclicked() {

        //validations
        if (partdroplist.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a part First",
                    "Please select a repair to add");
            return;
        }
        else if(installdateL.getValue()==null){
            AlertBox.info("Warning", "Pick a date for install",
                    "Please pick a date to install the repair");
            return;
        }

        //convert date to string
        LocalDate localDate = installdateL.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String iDate = localDate.format(formatter);

        //increment year by 1 for warranty expiry date
        String daymonth = iDate.substring(0, 6);
        String year = iDate.substring(6);
        int yr = Integer.parseInt(year);
        yr++;
        year = Integer.toString(yr);
        String wDate = daymonth + year;


        String insert = "INSERT INTO PartforRepair(RepairID, PartID, InstallDate, ExpiryDate) VALUES(?,?,?,?)";

        try{

            //query for part id from selected part
            String Query0 = "SELECT PartID FROM Parts WHERE PartName LIKE'%"  + currentpart + "%'";
            ResultSet rspart = db.query(Query0);
            int pid = Integer.parseInt(rspart.getString("PartID"));
            rspart.close();

            //makes sure multiple parts with the same name is not installed to the same vehicle
            String Query1= "SELECT * FROM PartforRepair WHERE RepairID="+currentbooking;
            ResultSet rsDups = db.query(Query1);
            while (rsDups.next()){
                if(rsDups.getInt("PartID") ==pid){
                    AlertBox.info("Warning", "Part already installed",
                            "The selected repair has already been installed for the vehicle." +
                                    "\n please choose a different part to install");
                    return;
                }
            }

            //checks if there are 10 distinct parts installed to a vehicle
            int count=0;

            try {
                while (rsDups.next()) {
                    count++;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            //only 10 distinct parts can be installed under a booking
            if(count==10){
                AlertBox.info("Warning", "Maximum number of parts installed",
                        "Sorry, you cannot add more repairs" +
                                "\n 10 distinct parts are already installed into this vehicle");
                return;
            }



            //decrement stock when adding a repair
            ResultSet rsstock = db.query("SELECT StockLevel FROM Parts WHERE PartName LIKE'%" + currentpart + "%'");
            int num= rsstock.getInt("StockLevel");
            rsstock.close();
            stocklv=num;
            if(num==0){
                AlertBox.info("Warning", "Out of Stock",
                        "Sorry. The part you have selected is out of stock");
                return;
            }
            num--;
            ResultSet rsupdatestock = db.query("UPDATE Parts SET StockLevel = "+num+" WHERE PartName LIKE'%" + currentpart + "%'");



            //insert into partforrepair table
            PreparedStatement stmt = db.getCon().prepareStatement(insert);
            stmt.setInt(1, currentbooking);
            stmt.setInt(2, pid);
            stmt.setString(3, iDate);
            stmt.setString(4, wDate);
            stmt.executeUpdate();
            stmt.close();


            PartsController.addrid=currentbooking;
        }

        catch (SQLException se) {
            se.printStackTrace();
        }

        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();
    }

    @FXML
    private void closeButtonAction(){ //close button
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();
    }

}

