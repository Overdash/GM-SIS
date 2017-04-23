package parts;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Shinu on 11/02/2017.
 */
public class PartsData {

    private IntegerProperty repairid;
    private StringProperty partname;
    private IntegerProperty prtid;
    private StringProperty installdate;
    private StringProperty warrantydate;
    private StringProperty regnum;
    private StringProperty fname;
    private StringProperty sname;
    private IntegerProperty booktype;
    private IntegerProperty bookid2;


    public PartsData(ResultSet rsParts){
        try {
            repairid = new SimpleIntegerProperty(rsParts.getInt("RepairID"));
            partname = new SimpleStringProperty(rsParts.getString("PartName"));
            prtid = new SimpleIntegerProperty(rsParts.getInt("PartID"));
            installdate = new SimpleStringProperty(rsParts.getString("InstallDate"));
            warrantydate = new SimpleStringProperty(rsParts.getString("ExpiryDate"));
            regnum = new SimpleStringProperty(rsParts.getString("RegNumber"));
            fname = new SimpleStringProperty(rsParts.getString("Firstname"));
            sname = new SimpleStringProperty(rsParts.getString("Surname"));
            booktype = new SimpleIntegerProperty(rsParts.getInt("BookingType"));
            bookid2 = new SimpleIntegerProperty(rsParts.getInt("BookingID"));


        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    //getter methods

    public int getrepairid() {return repairid.get();}

    public IntegerProperty repairidProperty() {
        return repairid;
    }

    public String getpartname() {return partname.get();}

    public StringProperty partnameProperty() {
        return partname;
    }

    public int getprtid() {return prtid.get();}

    public IntegerProperty prtidProperty() {
        return prtid;
    }

    public String getinstalldate() {return installdate.get();}

    public StringProperty installdateProperty() {
        return installdate;
    }

    public String getwarrantydate() {return warrantydate.get();}

    public StringProperty warrantydateProperty() {
        return warrantydate;
    }

    public String getregnum() {return regnum.get();}

    public StringProperty regnumProperty() {
        return regnum;
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

    public int getbookid2() {return bookid2.get();}

    public IntegerProperty bookid2Property() {
        return bookid2;
    }

}
