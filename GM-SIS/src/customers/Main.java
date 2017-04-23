package customers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Created by Belal on 05/02/2017.
 */
//just so i dont have to login everytime
    public class Main extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getResource("gui/Customer.fxml"));
            primaryStage.setTitle("Customer Module");
            primaryStage.setScene(new Scene(root, 1080, 720));
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }


