package userpanel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Belal on 14/03/2017. - Modified by Pro #TeamWork
 */

public class AdminData {

    private SimpleStringProperty colSurname;
    private SimpleStringProperty colID;
    private SimpleStringProperty colFirstname;
    private SimpleStringProperty colPass;
    private SimpleStringProperty colType;


    public AdminData(ResultSet rs){
        try{
            colFirstname = new SimpleStringProperty(rs.getString("FirstName")); // same in database
            colID = new SimpleStringProperty(rs.getString("ID"));
            colSurname = new SimpleStringProperty(rs.getString("LastName"));
            colPass = new SimpleStringProperty(rs.getString("Password"));
            String type = "Administrator";
            if(!rs.getBoolean("Type")) type = "Standard";
            colType = new SimpleStringProperty(type);
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public String getcolFirstname(){return colFirstname.get();}
    public StringProperty colFirstnameProperty() {return colFirstname;}

    public String getcolID() {
        return colID.get();
    }
    public StringProperty colIDProperty() {return colID;}

    public String getcolSurname() {
        return colSurname.get();
    }
    public StringProperty colSurnameProperty() {return colSurname; }

    public String getcolPass() {
        return colPass.get();
    }
    public StringProperty colPassProperty() {return colPass; }

    public String getcolType() {return colType.get();}
    public StringProperty colTypeProperty() {return colType;}


}