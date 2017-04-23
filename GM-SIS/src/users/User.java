package users;

/**
 * Created by Prodige on 17/01/2017.
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class User { //Sort of singleton due to single user!

    private boolean isAdmin;
    private String firstName;
    private String surname;
    private String userID;
    private boolean logged = false;
    private static User instance = null;

    private User(ResultSet rs){
        try{
            userID = rs.getString("ID");
            firstName = rs.getString("FirstName");
            surname = rs.getString("LastName");
            isAdmin = rs.getBoolean("Type");
            logged = true;
        } catch (SQLException se){
            System.out.println("Error logging User.");
        }
    }

    public static String getFirstName() {
        return instance.firstName;
    }

    public static String getSurname() {
        return instance.surname;
    }

    public static boolean isAdmin() {
        return instance.isAdmin;
    }

    public static boolean isLogged() {
        return instance.logged;
    }

    public static void logout(){
        instance.logged = false;
    }

    public static void setInstance(ResultSet rs){ instance = new User(rs);}
    public static User getInstance(){ return instance; }

    public static String getUserID() {
        return instance.userID;
    }
}
