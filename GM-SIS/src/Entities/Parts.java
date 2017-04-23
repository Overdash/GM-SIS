package Entities;

/**
 * Created by Ussas on 23/03/2017.
 */
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
public class Parts {
  private StringProperty PartName;
  private StringProperty DateInstalled;

  public Parts(ResultSet rsParts){
      try {
          PartName = new SimpleStringProperty(rsParts.getString("PartName"));
          DateInstalled= new SimpleStringProperty(rsParts.getString("InstallDate"));
      }catch(SQLException e){e.printStackTrace();}
      }

    public String getPartName() {
        return PartName.get();
    }

    public StringProperty partNameProperty() {
        return PartName;
    }

    public void setPartName(String partName) {
        this.PartName.set(partName);
    }

    public String getDateInstalled() {
        return DateInstalled.get();
    }

    public StringProperty dateInstalledProperty() {
        return DateInstalled;
    }

    public void setDateInstalled(String dateInstalled) {
        this.DateInstalled.set(dateInstalled);
    }
}

