package UI;

import java.util.HashMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import misc.AlertBox;

/**
 * Created by Prodige on 19/01/2017.
 *
 * Handles the task of loading all the screens and changing between them.
 * Uses the Player-role pattern to "switch" screens stored in a HashMap.
 * @author SE11
 */

public class ScreenHandler extends StackPane { // UI will use the Player-role pattern to "switch" screens

    private HashMap<String, Node> screens = new HashMap<>(); //Holds the screens to be displayed inside a HashMap of Nodes

    private static HashMap<String, Object> controllers = new HashMap<>(); //Holds the controllers of the screens separately

    public ScreenHandler() {
        super();
    }

    /**
     * Add the screen to the collection
     * @param name name given to the screen being added
     * @param screen the actual screen to be added
     */
    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    private void addController(String name, Object controller) {
        controllers.put(name,controller);
    }

    /**
     * Returns the Node with the appropriate name
     * @param name name of the screen to get
     * @return screen from HashMap
     */
    public Node getScreen(String name) {
        return screens.get(name);
    }

    public static Object getController(String name){
        return controllers.get(name);
    }


    /**
     * Loads the fxml file, add the screen to the screens collection and finally
     * injects the screenPane to the controller.
     * @param name name of screen to be loaded
     * @param resource URL/ path to the .fxml of the screen
     * @return {@code true} if loaded successfully
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader floader = new FXMLLoader();
            floader.setLocation(getClass().getResource(resource));
            Parent loadScreen = (Parent) floader.load();
            ScreenMaster sc = ((ScreenMaster) floader.getController());
            sc.setScreenParent(this);
            addScreen(name, loadScreen);
            addController(name, floader.getController());
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            popError("Error loading the screen " + name + "!", e);
            return false;
        }
    }

    /**
     * This method tries to display the screen denoted in the parameter {@code name} *SMOOTHLY!*.
     * First it makes sure the screen has been already loaded. If there is more than
     * one screen the new screen is been added second, and then the current screen is removed.
     * If there isn't any screen being displayed, the new screen is just added to the root
     * @param name name of the screen to be used
     * @return if the screen was successfully set to display.
     */
    public boolean setScreen(final String name) {
        if (screens.get(name) != null) {   //screen loaded
            final DoubleProperty opacity = opacityProperty();

            if (!getChildren().isEmpty()) {    // If there is more than one screen
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1)),
                        new KeyFrame(new Duration(50), t -> {
                            getChildren().remove(0);                    //remove the displayed screen
                            getChildren().add(0, screens.get(name));     //add the screen
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                                    new KeyFrame(new Duration(750), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }, new KeyValue(opacity, 1)));
                fade.play();

            } else {
                setOpacity(0.9);
                getChildren().add(screens.get(name));       //no one else been displayed, then just show
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                        new KeyFrame(new Duration(500), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
            popError("Screen not loaded!");
            return false;
        }

    }

    public boolean unloadScreen(String name){
        if(screens.remove(name) == null)
        {
            popError("Screen doesn't exist");
            return false;
        }else{
            controllers.remove(name);
            return true;
        }
    }

    public boolean reloadScreen(String name){
        if(screens.get(name) != null){
            Parent screen = (Parent) getScreen(name);
            Object controller = getController(name);
            unloadScreen(name);
            addScreen(name,screen);
            addController(name,controller);
            return true;
        }
        popError("Cannot reload screen " + name);
        return false;
    }

    /**
     * Called on System exit. All screens are unloaded from the HashMap.
     */
    public void unloadAll(){
        screens.clear();
        controllers.clear();
    }

    private void popError(String msg){// Will show a popup message
        AlertBox.display("Error with Screens!", msg);
    }

    private void popError(String msg, Exception e){// Will show a popup message
        AlertBox.error("Error with Screens!",msg, e);
    }
}
