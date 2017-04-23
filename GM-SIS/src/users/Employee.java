package users;

import common.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Prodige on 21/01/2017.
 */
public class Employee {

    private int ID;
    private String firstName;
    private String surname;

    public Employee(ResultSet rs){
        try{
            ID = rs.getInt("EmployeeID");
            ResultSet rsE = Database.getInstance().query("SELECT * FROM Employee Where EmployeeID = " + ID);
            rsE.next();
            firstName = rsE.getString("Firstname");
            surname = rsE.getString("Surname");
        } catch (SQLException se){
            se.printStackTrace();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public int getID() {
        return ID;
    }
}
