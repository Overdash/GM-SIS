package userpanel;

import common.Database;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import misc.AlertBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Prodige on 15/03/2017.
 */
public class AddEditController implements Initializable{

    @FXML private Button saveBtn, cancelBtn;
    @FXML private TextField txtID, txtFN, txtSN, txtPass;
    @FXML private ChoiceBox<String> accTypeCB;
    @FXML private Label lbTitle;

    private Database db;
    private Stage dialog;
    private boolean isDone = false;
    private boolean isEditing = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        if(db == null)
            popError("No Connection to Database", "Database Connection Failed",
                    "Failed to connect to the database please make sure all is in order.");
        fillType();
        saveBtn.setOnAction(e -> save());
        cancelBtn.setOnAction(event -> cancel());
    }

    public void setDialog(Stage stage){
        dialog = stage;
    }

    public void setUser(AdminData user){
        txtID.setText(user.getcolID());
        txtFN.setText(user.getcolFirstname());
        txtSN.setText(user.getcolSurname());
        txtPass.setText(user.getcolPass());
        accTypeCB.setValue(user.getcolType());
        setEditing(true);
    }

    private void fillType(){
        ObservableList<String> data = FXCollections.observableArrayList("Administrator", "Standard");
        accTypeCB.setItems(data);
    }

    private void save(){
        if(validate()){
            int type = 0;
            if(accTypeCB.getSelectionModel().getSelectedItem().equals("Administrator")) type = 1;
            String sql;
            if(isEditing) sql = "UPDATE Users SET FirstName = '" + txtFN.getText().trim()
                    + "', LastName = '" + txtSN.getText().trim() + "', Password = '" + txtPass.getText().trim() + "', Type = '" + type
                    + "' WHERE ID = '" + txtID.getText().trim() + "'";
                else sql = "INSERT INTO Users(ID, FirstName, LastName, Password, Type) VALUES('"+txtID.getText().trim()+"', '"+txtFN.getText().trim()
                    +"', '"+txtSN.getText().trim()+"', '"+txtPass.getText().trim()+"', '" + type + "')";
            db.update(sql);
            isDone=true;
            dialog.close();
        }
    }

    private void cancel(){
        if(AlertBox.choice("Quit", "Exiting, data will not be saved!", "Are you sure you wish to exit?"))
            dialog.close();
    }

    public boolean isDone(){
        return isDone;
    }

    private void setEditing(boolean isEditing){
        this.isEditing = isEditing;
        txtID.setDisable(true);
        lbTitle.setText("Edit Existing User");
    }

    private boolean validate(){
        String errorMsg = "";
        boolean err = false;
        if(txtID.getText().trim().length() != 5)
            errorMsg += "ID Invalid. Please enter a 5 digit ID (alphanumeric).\n";
        if(txtID.getText().trim().isEmpty())
            errorMsg += "ID Field is empty.\n";
        if(txtFN.getText().trim().isEmpty() || !txtFN.getText().trim().matches("^[a-zA-Z]+"))
            errorMsg += "Invalid First name.\n";
        if(txtSN.getText().trim().isEmpty() || !txtSN.getText().trim().matches("^[a-zA-Z]+"))
            errorMsg += "Invalid Surname.\n";
        if(txtPass.getText().trim().isEmpty())
            errorMsg += "Password field is empty.\n";
        if(accTypeCB.getSelectionModel().getSelectedItem() == null)
            errorMsg += "No account type selected.";
        if(!isEditing){
            try{
                String sql = "SELECT ID FROM Users WHERE ID = '" + txtID.getText().trim() + "'";
                ResultSet rs = db.query(sql);
                if(rs.next()) {
                    popError("User ID '" + txtID.getText().trim() + "' already exists in Database.",
                            "Please enter an non-existing User ID");
                    err = true;
                }
            } catch (SQLException se) {
                se.printStackTrace();
                popError("SQL Error", "SQL Error while validating", "Please check the database connection.");
            }
        }
        if(errorMsg.isEmpty() && !err)
            return true;
        else{
            popError("Invalid Fields", errorMsg);
            return false;
        }
    }

    private void popError(String header, String msg){
        AlertBox.display("Error Saving", header, msg);
    }

    private void popError(String title, String header, String msg){
        AlertBox.display(title, header, msg);
    }
}
