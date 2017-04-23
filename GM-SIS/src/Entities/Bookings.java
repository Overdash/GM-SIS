package Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * Created by Ussas on 18/02/2017.
 */

public class Bookings {
    private StringProperty FutureBookTime=null;
    private StringProperty FutureBookDate=null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Bookings(ResultSet rsBooking) {
        try {
                FutureBookDate=new SimpleStringProperty(formatter.format(LocalDate.parse(rsBooking.getString("BookingDate"))));
                FutureBookTime=new SimpleStringProperty(rsBooking.getString("BookingTime"));

        }catch (SQLException e){e.printStackTrace();}
    }

    public String getFutureBookTime() {
        return FutureBookTime.get();
    }

    public StringProperty futureBookTimeProperty() {
        return FutureBookTime;
    }

    public void setFutureBookTime(String futureBookTime) {
        this.FutureBookTime.set(futureBookTime);
    }

    public String getFutureBookDate() {
        return FutureBookDate.get();
    }

    public StringProperty futureBookDateProperty() {
        return FutureBookDate;
    }

    public void setFutureBookDate(String futureBookDate) {
        this.FutureBookDate.set(futureBookDate);
    }
}
