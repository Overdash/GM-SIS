package UI;

import common.Authentication;
import common.Main;
import javafx.application.Platform;
import javafx.scene.effect.Glow;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import misc.AlertBox;

/**
 * Created by Prodige on 18/01/2017.
 */

public class Menu { // Handles everything "Menu-y" XD lel

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void loadTopBars(Label title, Button closeBtn, Button minBtn, BorderPane draggable){

        title.setEffect(new Glow(0.5)); //Sets glow for the title
        closeBtn.setOnMouseClicked(mouseEvent -> Platform.exit()); //Sets close button operations
        minBtn.setOnMouseClicked(mouseEvent -> Main.stage.setIconified(true)); //Sets Minimise button operations

        //Allow the screen to be draggable
        draggable.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        draggable.setOnMouseDragged(e -> {
            draggable.getScene().getWindow().setX(e.getScreenX() - xOffset);
            draggable.getScene().getWindow().setY(e.getScreenY() - yOffset);
        });
    }

    public static void logout(ScreenHandler sh){
        if(AlertBox.choice("Logout", "You're about to logout", "Are you sure you want to logout?")) {
            Authentication.logout();
            sh.unloadAll();
            Main.initLogin();
        }
    }

    public static void toBookings(ScreenHandler sh){
        sh.setScreen(Main.BOOKINGS);
    }

    public static void toUP(ScreenHandler sh){
        sh.setScreen(Main.USERPANEL);
    }

    public static void toCust(ScreenHandler sh){
        sh.setScreen(Main.CUSTOMER);
    }

    public static void toVeh(ScreenHandler sh){
        sh.setScreen(Main.VEHICLE);
    }

    public static void toParts(ScreenHandler sh){
        sh.setScreen(Main.PARTS);
    }

    public static void toSPC(){
        AlertBox.info("Module not complete", "Module not loaded",
                "This module has not been integrated into the system. The person responsible for the module has not " +
                        "produced anything to integrate. All other modules are present and functional.");
    }
}
