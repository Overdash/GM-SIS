package Vehicles.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage=primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("Vehicles.fxml"));
        primaryStage.setTitle("Vehicles");
        primaryStage.setScene(new Scene(root, 1080, 720));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
