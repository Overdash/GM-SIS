
/**
 * Created by Shinu on 21/02/2017.
 */
package parts;
import misc.AlertBox;
import common.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class editcontroller implements Initializable {

    @FXML
    private Button addrep;
    @FXML
    private Button cancelrep;


    @FXML
    private TextField partnameL;
    @FXML
    private DatePicker installdateL;

    @FXML
    private ComboBox<String> partdroplist;



    private Database db;

    @FXML private Button closeButton;

    //stores current selected part in the combobox
    static String currentpart ="";
    //variable to initialise stock level
    static int stocklv=0;


    //combobox is filled and listener is added to it
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();

        partdroplist.setItems(fillcombobox2());

        //makes the only editable by picking a day in the calender
        installdateL.setEditable(false);

        partdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        currentpart = newValue.toString()

        );
    }


    @FXML
    public void addclicked() {

        //validation if nothing is selectd in combobox
        if (partdroplist.getSelectionModel().getSelectedItem() == null) {
            AlertBox.info("Warning", "Pick a part First",
                    "Please select a repair to change to");
            return;
        }
        //validation if no date is picked
        else if(installdateL.getValue()==null){
            AlertBox.info("Warning", "Pick a date for install",
                    "Please pick a date to edit the install the repair");
            return;
        }


        //convert date to string
        LocalDate localDate =installdateL.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        String iDate = localDate.format(formatter);

        //increment the year for the warranty expiry date
        String daymonth=iDate.substring(0,6);
        String year=iDate.substring(6);
        int yr=Integer.parseInt(year);
        yr++;
        year=Integer.toString(yr);
        String wDate = daymonth+year;



       int rid = PartsController.editid;
       //System.out.println(rid);

       try {



           //query for the part id
           String Query0 = "SELECT PartID FROM Parts WHERE PartName LIKE'%" +currentpart+ "%'";
           ResultSet rspart = db.query(Query0);
           int pid = Integer.parseInt(rspart.getString("PartID"));
            rspart.close();

           String query = "UPDATE PartforRepair SET PartID=?, InstallDate=?, ExpiryDate=?" +
                   " WHERE RepairID="+rid+" AND PartID="+PartsController.currentrepair;

            String Query1= "SELECT PartID FROM PartforRepair WHERE RepairID="+rid;
           ResultSet rspfr = db.query(Query1);

           //if the part has already been/going to be installed for the vehicle check
           while (rspfr.next()) {
               if (rspfr.getInt("PartID") == pid) {
                   AlertBox.info("Warning", "part already in repair",
                           "The part you have chosen is already used for a repair " +
                                   "\n please select another part or cancel");

                   return;
               }
           }



           //decrement stock when editing a repair
           ResultSet rsstock = db.query("SELECT StockLevel FROM Parts WHERE PartName LIKE'%" + currentpart + "%'");
           int num= rsstock.getInt("StockLevel");
           rsstock.close();

           //increment stock when editing a repair
           ResultSet rsstock2 = db.query("SELECT StockLevel FROM Parts WHERE PartID="+PartsController.selectedpid);
           int num2= rsstock2.getInt("StockLevel");
           rsstock2.close();


           stocklv=num;
           if(num==0){
               AlertBox.info("Warning", "Out of Stock",
                       "Sorry. The part you have selected is out of stock");
               return;
           }
           //decrement stock for the part being change to
           num--;
           ResultSet rsupdatestock = db.query("UPDATE Parts SET StockLevel = "+num+" WHERE PartName LIKE'%" + currentpart + "%'");

           //increment stock for the part that is being replaced
           num2++;
           ResultSet rsupdatestock2 = db.query("UPDATE Parts SET StockLevel = "+num2+" WHERE PartID="+PartsController.selectedpid);


           PreparedStatement stmt = db.getCon().prepareStatement(query);

           //update the repair
           stmt.setInt(1, pid);
           stmt.setString(2, iDate);
           stmt.setString(3, wDate);

           stmt.executeUpdate();
           stmt.close();

       }

     catch (SQLException se) {
        se.printStackTrace();
    }

        // close stage at the end
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

    }


    private ObservableList<String> fillcombobox2() { //fills combobox for parts
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
    private void closeButtonAction(){ //close button
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();
    }

}

