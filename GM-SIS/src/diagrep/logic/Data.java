package diagrep.logic;

import javafx.beans.property.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Prodige on 21/01/2017.
 * Wrapper class - Obtains values given in a ResultSet from the Database and assigns properties to be
 * used by the TableView in the {@code BookingController} class.
 *
 * @author Prodige
 */
public class Data { //Wrapper Class for table data

    private IntegerProperty bookingID;
    private StringProperty regNum;
    private StringProperty date;
    private IntegerProperty mechID;
    private IntegerProperty custID;
    private IntegerProperty timeTaken;
    private StringProperty custFName;
    private StringProperty custSurname;
    private StringProperty make;

    public Data(ResultSet rs){
        try{
            //DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            bookingID = new SimpleIntegerProperty(rs.getInt("BookingID"));
            regNum = new SimpleStringProperty(rs.getString("RegNumber"));
            date = new SimpleStringProperty(rs.getString("BookingDate"));
            custID = new SimpleIntegerProperty(rs.getInt("CustomerID"));
            custFName = new SimpleStringProperty(rs.getString("Firstname"));
            custSurname = new SimpleStringProperty(rs.getString("Surname"));
            mechID = new SimpleIntegerProperty(rs.getInt("EmployeeID"));
            timeTaken = new SimpleIntegerProperty(rs.getInt("TotalTime"));
            make = new SimpleStringProperty(rs.getString("Make"));
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public int getBookingID() {
        return bookingID.get();
    }

    public IntegerProperty bookingIDProperty() {
        return bookingID;
    }

    public String getRegNum() {
        return regNum.get();
    }

    public StringProperty regNumProperty() {
        return regNum;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public int getCustID() {
        return custID.get();
    }

    public IntegerProperty custIDProperty() {
        return custID;
    }

    public String getCustFName() {
        return custFName.get();
    }

    public StringProperty custFNameProperty() {
        return custFName;
    }

    public String getCustSurname() {
        return custSurname.get();
    }

    public StringProperty custSurnameProperty() {
        return custSurname;
    }

    public int getMechID() {
        return mechID.get();
    }

    public IntegerProperty mechIDProperty() {
        return mechID;
    }

    public int getTimeTaken() {
        return timeTaken.get();
    }

    public IntegerProperty timeTakenProperty() {
        return timeTaken;
    }

    public String getMake() {
        return make.get();
    }

    public StringProperty makeProperty() {
        return make;
    }
}
