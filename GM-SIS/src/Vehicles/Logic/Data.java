package Vehicles.Logic;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Created by Ussas on 12/02/2017.
 */
public class Data {


    private StringProperty RegNumber;
    private StringProperty Model;
    private StringProperty Make;
    private StringProperty PostCode;
    private StringProperty FName;
    private StringProperty LName;
    private StringProperty BookDate;
    private StringProperty BookTime;




    public Data(ResultSet rsVehicle, ResultSet rsCustomer, String BookingDate,String BookingTime,boolean hasBook) {
        try{

            RegNumber= new SimpleStringProperty(rsVehicle.getString("RegNumber"));
            Model=new SimpleStringProperty(rsVehicle.getString("Model"));
            Make=new SimpleStringProperty(rsVehicle.getString("Make"));
            PostCode=new SimpleStringProperty((rsCustomer.getString("Postcode")));
            FName=new SimpleStringProperty(rsCustomer.getString("FirstName"));
            LName=new SimpleStringProperty(rsCustomer.getString("Surname"));
            if(hasBook) {
                BookDate = new SimpleStringProperty(BookingDate);
                BookTime=new SimpleStringProperty(BookingTime);
            }
            else{
                BookDate=new SimpleStringProperty("-");
                BookTime=new SimpleStringProperty("-");
            }
        } catch (SQLException e) {
         e.printStackTrace();

        }

    }

    public String getRegNumber() {
        return RegNumber.get();
    }

    public StringProperty regNumberProperty() {
        return RegNumber;
    }

    public void setRegNumber(String regNumber) {
        this.RegNumber.set(regNumber);
    }

    public String getModel() {
        return Model.get();
    }

    public StringProperty modelProperty() {
        return Model;
    }

    public void setModel(String model) {
        this.Model.set(model);
    }

    public String getMake() {
        return Make.get();
    }

    public StringProperty makeProperty() {
        return Make;
    }

    public void setMake(String make) {
        this.Make.set(make);
    }

    public String getFName() {
        return FName.get();
    }

    public StringProperty FNameProperty() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName.set(FName);
    }

    public String getLName() {
        return LName.get();
    }

    public StringProperty LNameProperty() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName.set(LName);
    }

    public String getBookDate() {
        return BookDate.get();
    }

    public StringProperty bookDateProperty() {
        return BookDate;
    }

    public String getPostCode() {
        return PostCode.get();
    }

    public StringProperty postCodeProperty() {
        return PostCode;
    }

    public void setPostCode(String postCode) {
        this.PostCode.set(postCode);
    }

    public void setBookDate(String bookDate) {
        this.BookDate.set(bookDate);
    }

    public String getBookTime() {
        return BookTime.get();
    }

    public StringProperty bookTimeProperty() {
        return BookTime;
    }

    public void setBookTime(String bookTime) {
        this.BookTime.set(bookTime);
    }
}

