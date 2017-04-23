package customers.logic;

/**
 * Created by Belal on 12/02/2017.
 */

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

//wrapper for table
public class CustomerData {


    private SimpleStringProperty colFirstname;
    private SimpleStringProperty colSurname;
    private SimpleIntegerProperty colID;
    private SimpleStringProperty colVehReg;
    private SimpleStringProperty colDate;
    private SimpleIntegerProperty colBookID;
    private SimpleIntegerProperty colPartID;
    private SimpleStringProperty colPartName;
    private SimpleStringProperty colType;

    public CustomerData(ResultSet rs){
        try{
            colFirstname = new SimpleStringProperty(rs.getString("Firstname")); // same in database
            colID = new SimpleIntegerProperty(rs.getInt("CustomerID"));
            colSurname = new SimpleStringProperty(rs.getString("Surname"));
            colVehReg = new SimpleStringProperty(rs.getString("RegNumber"));
            colType = new SimpleStringProperty(rs.getString("Type"));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public CustomerData(ResultSet rsBook, String booking){
        try{
            colDate = new SimpleStringProperty(rsBook.getString("BookingDate"));
            colBookID = new SimpleIntegerProperty(rsBook.getInt("BookingID"));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public CustomerData(ResultSet rsBook, String Parts, String Table){
        try{
            colPartID = new SimpleIntegerProperty(rsBook.getInt("PartID"));
            colPartName = new SimpleStringProperty(rsBook.getString("PartName"));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public String getcolFirstname(){return colFirstname.get();}
    public StringProperty colFirstnameProperty() {return colFirstname;}

    public Integer getcolID() {
        return colID.get();
    }
    public IntegerProperty colIDProperty() {return colID;}

    public String getcolSurname() {
        return colSurname.get();
    }
    public StringProperty colSurnameProperty() {return colSurname; }

    public String getcolVehReg() {
        return colVehReg.get();
    }
    public StringProperty colVehRegProperty() {return colVehReg; }

    public String getcolDate() {return colDate.get();}
    public StringProperty colDateProperty() {return colDate; }

    public Integer getcolBookID() {
        return colBookID.get();
    }
    public IntegerProperty colBookIDProperty() {return colBookID;}

    public Integer getcolPartID() {return colPartID.get();}
    public IntegerProperty colPartIDProperty() {return colPartID;}

    public String getcolPartName() {
        return colPartName.get();
    }
    public StringProperty colPartNameProperty() {return colPartName; }

    public String getcolType() {return colType.get();}
    public StringProperty colTypeProperty() {return colType;}

}
