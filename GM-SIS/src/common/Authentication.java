package common;

import UI.Menu;
import UI.ScreenMaster;
import UI.ScreenHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import misc.*;
import users.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by Prodige on 16/01/2017.
 * This class is responsible for checking the User's credentials and logging them
 * in respects to the level of authorisation they have.
 *
 * @author SE11
 */

public class Authentication implements Initializable, ScreenMaster {

    @FXML private TextField userID;
    @FXML private PasswordField password;
    @FXML private Button login;
    @FXML private Button closeBtn;
    @FXML private Button minBtn;
    @FXML private Label title;
    @FXML private BorderPane draggable;
    private static boolean logged;

    private static Database db;
    //private User singleUser;
    public ScreenHandler master;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        login.setOnAction(event -> authenticate()); //lambda expression to handle login event
        Menu.loadTopBars(title,closeBtn,minBtn,draggable);
    }

    private void authenticate(){
        if(logged){
            popError("Only one user can be logged in at a time.");
            return;
        }
        try{
            String id = userID.getText();
            String pass = password.getText();
            if(id.equals("") || pass.equals("")) //Checks both fields are populated
                popError("Not all fields are filled.");
            else{
                String query = "SELECT * FROM users"; //Query string to get user details
                //System.out.println("entered else");
                ResultSet rs = db.query(query); //Query execution (handled in Database class)
                //System.out.println("query done");
                while(rs.next() && !logged){ //Makes sure the user isnt logged in (only one user at a time)
                    String rowID = rs.getString("ID");
                    String rowPass = rs.getString("Password");

                    if(id.equals(rowID) && pass.equals(rowPass)){ //Checks if credentials are valid (with current row from rs)
                        logged = true;
                        User.setInstance(rs);
                        showUPanel();
                    }
                }
                if(!logged)
                    popError("User ID or Password incorrect. Please try log in again.");
            }
        }catch (SQLException se){
            popError("Database may not be connected. Try again later or try contact an admin.", se);
            se.printStackTrace();
        }
    }

    public static void logout(){
        logged = false;
        User.logout();
    }

    private void popError(String msg){// Will show a popup message
        AlertBox.display("Warning","Login Warning", msg);
    }

    private void popError(String msg, Exception e){// Will show a popup message
        AlertBox.error("Login Error",msg, e);
    }

    public void setScreenParent(ScreenHandler currentScreen) {
        master = currentScreen;
    }

    /**
     * Loads the system accordingly. Loading all the modules and screens first time allowing for more
     * flexible and faster use of the system.
     */
    private void showUPanel(){
        ScreenHandler container = new ScreenHandler();
        container.loadScreen(Main.USERPANEL,Main.PANELPATH);
        container.loadScreen(Main.BOOKINGS, Main.BOOKPATH);
        container.loadScreen(Main.CUSTOMER, Main.CUSTPATH);
        container.loadScreen(Main.VEHICLE, Main.VEHPATH);
        container.loadScreen(Main.PARTS, Main.PARTSPATH);
        //stage.initStyle(StageStyle.UNDECORATED);
        //master = container;
        Group gr = new Group();
        gr.getStylesheets().add(this.getClass().getResource("/UI/MainView.css").toExternalForm());
        gr.getChildren().add(container);
        Scene sc = new Scene(gr, 1080, 720);
        Main.stage.setScene(sc);
        container.setScreen(Main.USERPANEL);
        //stage.show();
    }

    /*private void newUser(ResultSet rs){
        if(!User.isLogged()) {
            popError("Only one user can be logged in at a time. Logging previous user out.");
            logout();
        } else {
            User.setInstance(rs);
        }
    }*/
}
