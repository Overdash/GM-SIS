
/**
 * Created by Shinu on 21/02/2017.
 */
package parts;

import misc.AlertBox;
import common.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class editstockcontroller implements Initializable {

    @FXML
    private Button addrep;
    @FXML
    private Button cancelrep;

    @FXML
    private TextField partcostL;
    @FXML
    private TextField partnameL;
    @FXML
    private TextField descriptionL;


    private Database db;

    @FXML private Button closeButton;


    //set the labels for parts
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();

        String pName = PartsController.currentpart;

        String Query0 = "SELECT * FROM Parts WHERE PartName LIKE'%"  + pName + "%'";
        ResultSet rspart = db.query(Query0);

        try{
            partnameL.setText(pName);
            descriptionL.setText(rspart.getString("Description"));
            partcostL.setText(rspart.getString("PartCost"));
        }
        catch (SQLException se) {
            se.printStackTrace();
        }

    }


    @FXML
    public void addclicked() {



        String pName = PartsController.currentpart;

        //validations when entering values to edit a part
        if(isEmpty(partnameL.getText())){

            AlertBox.info("Warning", "Invalid part name entered",
                    "Please enter a valid name for the part name");
            return;
        }
        if((partnameL.getText().charAt(0)==' ')){

            AlertBox.info("Warning", "space(s) in part name",
                    "please remove the space(s) at the start of part name");
            return;

        }

        //check if the part being added is already in the database
        String query2 = "SELECT * FROM Parts";
        ResultSet rsNames = db.query(query2);
        try{
            String name="";
            while(rsNames.next()) {
                name=rsNames.getString("PartName").toLowerCase();
                if((partnameL.getText().toLowerCase().contains(name))){
                    AlertBox.info("Warning", "part already there",
                            "The part is already added to the list of parts");
                    return;
                }
            }

        } catch(SQLException se) {
            se.printStackTrace();
        }

        if(isEmpty(descriptionL.getText())){

            AlertBox.info("Warning", "Invalid description entered",
                    "Please enter a valid description");
            return;
        }
        if((descriptionL.getText().charAt(0)==' ')){

            AlertBox.info("Warning", "space(s) in description",
                    "please remove the space(s) at the start of description");
            return;

        }
        if(isEmpty(partcostL.getText())){

            AlertBox.info("Warning", "Invalid part cost entered",
                    "Please enter a valid number for the part cost");
            return;
        }
        if((!isdouble((partcostL.getText())))){

            AlertBox.info("Warning", "Invalid part cost entered",
                    "Please enter a valid number for the part cost");
            return;
        }
        if(!has2dpv2(partcostL.getText())){

            AlertBox.info("Warning", "Invalid part cost entered",
                    "Please enter a valid number for the part cost");
            return;
        }

        //convert string to double for the price
        double partcost=Double.parseDouble(partcostL.getText());

            try{

                String Query0 = "SELECT PartID FROM Parts WHERE PartName LIKE'%"  + pName + "%'";
                ResultSet rspart = db.query(Query0);
                int pid = Integer.parseInt(rspart.getString("PartID"));
                rspart.close();

                String query = "UPDATE Parts SET PartName=?, Description=?, PartCost=? WHERE PartID ="+pid;

                PreparedStatement stmt = db.getCon().prepareStatement(query);

                //update the values
                stmt.setString(1, partnameL.getText());
                stmt.setString(2, descriptionL.getText());
                stmt.setDouble(3, partcost);

                stmt.executeUpdate();
                stmt.close();

                PartsController.currentpart= partnameL.getText();
            }

     catch (SQLException se) {
        se.printStackTrace();
    }
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();

    }

    //validations
    private boolean isEmpty(String message){

        if(message.equals("")){
            return true;
        }
        else{
            return false;
        }

    }

    //checks if its a double and if its greater than 0
    private boolean isdouble(String message){

        try{
            double n = Double.parseDouble(message);
            if(n>0)
                return true;
            else
                return false;
        }
        catch(NumberFormatException e){
            return false;
        }

    }

    //checks if the price is more than 2dp or less than 2dp
    private boolean has2dpv2(String message) {

        int dot = -1;

        for (int i = 0; i < message.length(); i++) {

            if (message.charAt(i) == '.')
                dot = i;

        }

        if (dot == -1) {
            return true;
        }

        String afterdec = message.substring(dot);
        if (afterdec.length() <= 3) {
            return true;
        } else {
            return false;
        }
    }

    @FXML
    private void closeButtonAction(){ //close button
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();
    }

}

