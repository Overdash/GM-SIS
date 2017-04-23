package common;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by Prodige on 17/01/2017.
 *
 * PartsMain class to launch application. URLs to all modules are defined here.
 * @author SE11
 */
public class Main extends Application {

   // private Stage primaryStage;
    public static Stage stage;
    //private BorderPane layout;

    //ALL SCREENS IN THE SYSTEM MUST BE DEFINED HERE!! SO DEFINE YOURS YOURSELF IN THE WAY DONE BELOW PLEASE!
    //public static final String *VARNAME* = "Some name";
    //public static final String *PATHNAME* = "/packageToFile/file.fxml <- THIS IS THE CORRECT WAY!
    //TODO IMPORT ALL SCREENS! - Check all UI Functions
    //public static final String AUTHENTICATION = "Authentication";
    //public static final String AUTHPATH = "/common/login.fxml";
    public static final String USERPANEL = "UserPanel";
    public static final String PANELPATH = "/userpanel/UserPanel.fxml";
    public static final String BOOKINGS = "Bookings";
    public static final String BOOKPATH = "/diagrep/gui/Bookings.fxml";
    public static final String CUSTOMER = "Customer";
    public static final String CUSTPATH = "/customers/gui/Customer.fxml";
    public static final String VEHICLE = "Vehicle";
    public static final String VEHPATH = "/Vehicles/GUI/Vehicles.fxml";
    public static final String PARTS = "Parts";
    public static final String PARTSPATH = "/parts/Parts.fxml";

    public void start(Stage primaryStage) throws Exception {
        //this.primaryStage = primaryStage;
        stage = primaryStage;
        //primaryStage.setTitle("GM-SIS System");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        initLogin();
    }

    public static void initLogin(){
        try{
            FXMLLoader floader = new FXMLLoader();
            floader.setLocation(Authentication.class.getResource("login.fxml"));
            Pane mainLay = floader.load();
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("resources/logo.png")));
            Scene sc = new Scene(mainLay);
            stage.setScene(sc);
            stage.show();
        } catch (IOException e) {
            System.out.println("Login failed to show!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        launch(args);
    }

}
