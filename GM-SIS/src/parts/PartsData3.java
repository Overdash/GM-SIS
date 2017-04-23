package parts;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by Shinu on 18/02/2017.
 */
public class PartsData3 {

    private StringProperty pname;
    private StringProperty ddate;
    private IntegerProperty status;
    private IntegerProperty quant;
    private IntegerProperty quantleft;


    public PartsData3(ResultSet rsParts){
        try {
            pname = new SimpleStringProperty(rsParts.getString("PartName"));
            ddate = new SimpleStringProperty(rsParts.getString("DeliveryDate"));
            status = new SimpleIntegerProperty(rsParts.getInt("IsDelivered"));
            quant = new SimpleIntegerProperty(rsParts.getInt("Quantity"));
            quantleft = new SimpleIntegerProperty(rsParts.getInt("QuantityLeft"));

        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    //getter methods

    public String getpname() {return pname.get();}

    public StringProperty pnameProperty() {
        return pname;
    }

    public String getddate() {return ddate.get();}

    public StringProperty ddateProperty() {
        return ddate;
    }

    public int getstatus() {return status.get();}

    public IntegerProperty statusProperty() {
        return status;
    }

    public int getquant() {return quant.get();}

    public IntegerProperty quantProperty() {
        return quant;
    }

    public int getquantleft() {return quantleft.get();}

    public IntegerProperty quantleftProperty() {
        return quantleft;
    }


}
