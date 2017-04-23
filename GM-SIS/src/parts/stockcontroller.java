
/**
 * Created by Shinu on 21/02/2017.
 */
package parts;

import common.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import misc.AlertBox;


public class stockcontroller implements Initializable {

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



    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();
    }


    @FXML
    public void addclicked() {

        //int pid =PartsController.pid++;
        String pName=partnameL.getText();
        String description=descriptionL.getText();


        //validations
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
        String query = "SELECT * FROM Parts";
        ResultSet rsNames = db.query(query);
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

        //convert price in string to price in double
        double partcost=Double.parseDouble(partcostL.getText());

        String insert = "INSERT INTO Parts(PartName, Description, StockLevel, PartCost) VALUES(?,?,?,?)";
          try{
              PreparedStatement stmt = db.getCon().prepareStatement(insert);

              //insert new part
              stmt.setString(1, pName);
              stmt.setString(2, description);
              stmt.setInt(3, 0);
              stmt.setDouble(4, partcost);
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
    private boolean isEmpty(String message){

        if(message.equals("")){
            return true;
        }
        else{
            return false;
        }

    }
    //validation methods
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

    //validation methods
    private boolean has2dpv2(String message){

        int dot=-1;

        for(int i=0; i<message.length(); i++){

            if(message.charAt(i)=='.')
                dot = i;

        }

        if(dot==-1){
            return true;
        }

        String afterdec=message.substring(dot);
        if(afterdec.length()<=3){
            return true;
        }
        else{
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

