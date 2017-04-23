package misc;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.control.Alert.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Created by Prodige on 17/01/2017.
 *
 * @author Prodige
 */
public class AlertBox {

    public static void display(String title, String header, String message){
        Alert box = new Alert(AlertType.WARNING);

        box.setTitle(title);
        box.setHeaderText(header);
        box.setContentText(message);
        box.showAndWait();
    }

    public static void info(String title, String header, String message){
        Alert box = new Alert(AlertType.INFORMATION);

        box.setTitle(title);
        box.setHeaderText(header);
        box.setContentText(message);
        box.showAndWait();
    }

    public static boolean choice(String title, String header, String message){
        Alert box = new Alert(AlertType.CONFIRMATION);

        box.setTitle(title);
        box.setHeaderText(header);
        box.setContentText(message);
        Optional<ButtonType> result = box.showAndWait();
        return (result.isPresent() && result.get() == ButtonType.OK);
    }

    public static void display(String title, String message) {
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(100);
        window.setResizable(false);

        Label lb = new Label();
        lb.setText(message);
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(lb, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void error(String header, String message){
        Alert box = new Alert(AlertType.ERROR);
        box.setTitle("Error");
        box.setHeaderText(header);
        box.setContentText(message);
        box.showAndWait();
    }

    public static void error(String header, String message, Exception e){
        Alert box = new Alert(AlertType.ERROR);
        box.setTitle("Error");
        box.setHeaderText(header);
        box.setContentText(message);
        box.showAndWait();

        StringWriter sw = new StringWriter();
        PrintWriter print = new PrintWriter(sw);
        e.printStackTrace(print);
        String expTxt = sw.toString();

        Label lb = new Label("Error details (stacktrace): ");
        TextArea ta = new TextArea(expTxt);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setMaxWidth(Double.MAX_VALUE);
        ta.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(ta,Priority.ALWAYS);
        GridPane.setHgrow(ta, Priority.ALWAYS);

        GridPane expCont = new GridPane();
        expCont.setMaxWidth(Double.MAX_VALUE);
        expCont.add(lb,0,0);
        expCont.add(ta,0,1);

        box.getDialogPane().setExpandableContent(expCont);
        box.showAndWait();
    }

    public static String input(String header, String prompt){
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Enter input");
        tid.setHeaderText(header);
        tid.setContentText(prompt);

        Optional<String> result = tid.showAndWait();
        return result.orElse(null);
    }

}
