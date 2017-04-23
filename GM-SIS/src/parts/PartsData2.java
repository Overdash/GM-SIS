package parts;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Shinu on 18/02/2017.
 */
public class PartsData2 {

    private StringProperty fname;
    private StringProperty sname;
    private IntegerProperty bookid;
    private StringProperty date;
    private IntegerProperty booktype;
    private StringProperty vehreg;



    public PartsData2(ResultSet rsParts){
        try {
            fname = new SimpleStringProperty(rsParts.getString("Firstname"));
            sname = new SimpleStringProperty(rsParts.getString("Surname"));
            bookid = new SimpleIntegerProperty(rsParts.getInt("BookingID"));
            date = new SimpleStringProperty(rsParts.getString("BookingDate"));
            booktype = new SimpleIntegerProperty(rsParts.getInt("BookingType"));
            vehreg = new SimpleStringProperty(rsParts.getString("RegNumber"));

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    //getter methods

    public int getbookid() {return bookid.get();}

    public IntegerProperty bookidProperty() {
        return bookid;
    }

    public String gettime() {return date.get();}

    public StringProperty timeProperty() {
        return date;
    }

    public String getfname() {return fname.get();}

    public StringProperty fnameProperty() {
        return fname;
    }

    public String getsname() {return sname.get();}

    public StringProperty snameProperty() {
        return sname;
    }

    public int getbooktype() {return booktype.get();}

    public IntegerProperty booktypeProperty() {
        return booktype;
    }

    public String getvehreg() {return vehreg.get();}

    public StringProperty vehregProperty() {
        return vehreg;
    }

}
