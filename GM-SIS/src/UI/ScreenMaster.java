package UI;

/**
 * Created by Prodige on 19/01/2017.
 *
 * Connects all the screens in the system by allowing them to implement a common interface, creating
 * a framework to interchangeably move between screens/ modules.
 * @author SE11
 */
public interface ScreenMaster {
    void setScreenParent(ScreenHandler currentScreen);
}
