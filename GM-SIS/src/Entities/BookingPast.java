package Entities;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.text.DecimalFormat;

/**
 * Created by Ussas on 25/03/2017.
 */
public class BookingPast {
    private StringProperty BookingDate=null;
    private StringProperty Bill=null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BookingPast(String Bookingdate,double BookBill){
        BookingDate=new SimpleStringProperty(formatter.format(LocalDate.parse(Bookingdate)));
        if(BookBill==0.0){
            Bill=new SimpleStringProperty("Ongoing");
        }
        else {
            DecimalFormat df = new DecimalFormat(".##");
            Bill = new SimpleStringProperty(df.format(BookBill));
        }
    }

    public String getBookingDate() {
        return BookingDate.get();
    }

    public StringProperty bookingDateProperty() {
        return BookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.BookingDate.set(bookingDate);
    }

    public String getBill() {
        return Bill.get();
    }

    public StringProperty billProperty() {
        return Bill;
    }

    public void setBill(String bill) {
        this.Bill.set(bill);
    }
}
