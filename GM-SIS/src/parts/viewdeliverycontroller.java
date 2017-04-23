
/**
 * Created by Shinu on 21/02/2017.
 */
package parts;

import common.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;


public class viewdeliverycontroller implements Initializable {

    @FXML
    private Button addrep;
    @FXML
    private Button cancelrep;

    @FXML
    private TextField partcostL;
    @FXML
    private TextField partnameL;
    @FXML
    private TextField descriptionL;


    @FXML
    private TableView<PartsData3> deliverytable;

    @FXML
    private TableColumn<PartsData3, String> pnamecol;
    @FXML
    private TableColumn<PartsData3, String> ddatecol;
    @FXML
    private TableColumn<PartsData3, String> statuscol;
    @FXML
    private TableColumn<PartsData3, String> quantcol;
    @FXML
    private TableColumn<PartsData3, String> quantleftcol;


    private Database db;

    @FXML private Button closeButton;



    //fill delivery table with orders/delivery history
    public void initialize(URL url, ResourceBundle rb) {
        db = Database.getInstance();

        Fields3();
        deliverytable.setItems(loadPartsData3());
        updatestatus();
    }


    //the close button
    @FXML
    public void addclicked() {



        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();

    }

    //validation methods
    private boolean isInt(String message){

        try{
            int n = Integer.parseInt(message);
            return true;

        }
        catch(NumberFormatException e){
            return false;
        }

    }
    //validation methods
    private boolean isEmpty(String message){

        if(message.equals("")){
            return true;
        }
        else{
            return false;
        }

    }


    //observable list to fill the delivery table
    private ObservableList<PartsData3> loadPartsData3() {
        ArrayList<PartsData3> list = new ArrayList<>();

        //query for all the columns needed
        String query="SELECT Parts.PartName, StockDeliveries.DeliveryDate, StockDeliveries.IsDelivered, StockDeliveries.Quantity," +
                " StockDeliveries.QuantityLeft FROM Parts" +
                " JOIN StockDeliveries on Parts.PartID = StockDeliveries.PartID";

        ResultSet rsdel = db.query(query);

        //add to list
        try {
            while (rsdel.next()) {
                list.add(new PartsData3(rsdel));
            }
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        }

        return FXCollections.observableArrayList(list);
    }


    private void Fields3() {

        pnamecol.setCellValueFactory(new PropertyValueFactory<>("pname"));
        ddatecol.setCellValueFactory(new PropertyValueFactory<>("ddate"));
        statuscol.setCellValueFactory(new PropertyValueFactory<>("status"));
        quantcol.setCellValueFactory(new PropertyValueFactory<>("quant"));
        quantleftcol.setCellValueFactory(new PropertyValueFactory<>("quantleft"));

    }


    //if the delivery date is passed then change status to delivered
    private void updatestatus() {

        //get todays date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 0);
        Date newDate = calendar.getTime();

        //query for undelivered orders
        String query="SELECT * FROM StockDeliveries WHERE IsDelivered="+0;

        ResultSet rsdel = db.query(query);

        try {
            while (rsdel.next()) {

                String date=rsdel.getString("DeliveryDate");
                int prtid = Integer.parseInt(rsdel.getString("PartID"));

                if ((new SimpleDateFormat("dd/MM/yy").parse(date).before(newDate))) {
                    //System.out.println(date+" is equal to or passed");


                    String query2 = "UPDATE StockDeliveries SET IsDelivered=? WHERE PartID=" +prtid+ " AND DeliveryDate LIKE'%" + date + "%'";
                    PreparedStatement stmt = db.getCon().prepareStatement(query2);

                    stmt.setInt(1, 1);
                    stmt.executeUpdate();
                    stmt.close();

                }
            }
        } catch (SQLException se) {
            System.out.println("error");
            se.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        deliverytable.setItems(loadPartsData3());
    }


    @FXML
    private void closeButtonAction(){ //close button
        // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();

        stage.close();
    }

}

