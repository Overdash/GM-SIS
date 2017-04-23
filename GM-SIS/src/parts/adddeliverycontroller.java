
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;


public class adddeliverycontroller implements Initializable {

    @FXML
    private Button addrep;
    @FXML
    private Button cancelrep;

    @FXML
    private TextField quantityL;



    private Database db;

    @FXML private Button closeButton;

    @FXML
    private ComboBox<String> partdroplist;

    //holds the current part in the combobox
    static String currentpart="";


    //combobox is filled and a listener is added to it in the initialize method
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();

        partdroplist.setItems(fillcombobox2());

        partdroplist.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        currentpart = newValue.toString()
        );
    }


    @FXML
    public void addclicked() {

        String pName= currentpart;

        //validations when nothing is entered
        if(isEmpty(quantityL.getText())){
            AlertBox.info("Warning", "Invalid quantity entered",
                    "please enter a number for quantity");
            return;
        }
        //validation for when an integer is not entered
        if(!isInt(quantityL.getText())){
            AlertBox.info("Warning", "Invalid quantity entered",
                    "please enter a number for quantity");
            return;
        }


        int quantity = Integer.parseInt(quantityL.getText());

        //get the date of exactly 7 days in the future
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 7);

        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yy");
        String formatted = format1.format(c.getTime());


        //insert entered values into the stock delivery database
        String insert = "INSERT INTO StockDeliveries(PartID, DeliveryDate, IsDelivered, Quantity, QuantityLeft) VALUES(?,?,?,?,?)";
        try {

            ResultSet rsparts1 = db.query("SELECT * FROM Parts WHERE PartName LIKE'%" + pName + "%'");
            int pid= rsparts1.getInt("PartID");
            rsparts1.close();
/////////////////////////////////////

            //query to get all columns from stock delivery table
            String Query2 = "SELECT * FROM StockDeliveries";
            ResultSet rsdeliv = db.query(Query2);
            try {
                //if part is already ordered or in warehouse, an prompt will pop up
                while (rsdeliv.next()) {
                    if ((rsdeliv.getInt("PartID") == pid) && rsdeliv.getInt("QuantityLeft")>0) {
                        AlertBox.info("Warning", "Already ordered/in warehouse",
                                "The part is already being ordered or in the warehouse" +
                                        "\n please order once the warehouse stock is finished");
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
/////////////////////////////////////////////////

            PreparedStatement stmt = db.getCon().prepareStatement(insert);

            //inserting the values into the database
            stmt.setInt(1, pid);
            stmt.setString(2, formatted);
            stmt.setInt(3, 0);
            stmt.setInt(4, quantity);
            stmt.setInt(5, quantity);
            stmt.executeUpdate();
            stmt.close();

        }
        catch (SQLException se) {
            se.printStackTrace();
        }

        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();

    }

    //validation methods
    private boolean isInt(String message){

        try{
            int n = Integer.parseInt(message);
            return true;

        }
        catch(NumberFormatException e){
            return false;
        }

    }

    private boolean isEmpty(String message){

        if(message.equals("")){
            return true;
        }
        else{
            return false;
        }

    }

    private ObservableList<String> fillcombobox2() { //fills the combobox for parts
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